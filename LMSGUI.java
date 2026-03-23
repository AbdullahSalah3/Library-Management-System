
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * LMS GUI — Login (big) -> Welcome -> Dashboard with clickable chips
 * Java 8 compatible; no external dependencies.
 */
public class LMSGUI extends JFrame {

    // -------- Preferences & Flags --------
    private static final Preferences PREFS = Preferences.userRoot().node("lms_gui_colorful");
    private static boolean DARK_MODE = PREFS.getBoolean("dark", false);

    // Enable/disable Login screen
    private static final boolean USE_LOGIN = true;

    // -------- Service (in-memory demo) --------
    private final LMSService service = new SimpleInMemoryLMSService();

    // -------- UI --------
    private JTabbedPane tabs;
    private JCheckBoxMenuItem darkToggle;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.install(DARK_MODE);
            if (USE_LOGIN) {
                boolean ok = LoginDialog.show();
                if (!ok) System.exit(0);
                WelcomeOverlay.show("Welcome to Library Management System", 1800);
            }
            LMSGUI app = new LMSGUI();
            app.setVisible(true);
        });
    }

    public LMSGUI() {
        super("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1240, 820));
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        // Build panels with references for actions
        BooksPanel booksPanel = new BooksPanel(service);
        MembersPanel membersPanel = new MembersPanel(service);
        BorrowReturnPanel borrowPanel = new BorrowReturnPanel(service);

        // Actions used by clickable chips on the dashboard
        UIActions actions = new UIActions() {
            @Override public void goBooks()   { tabs.setSelectedIndex(tabs.indexOfTab("📚 Books")); }
            @Override public void goMembers() { tabs.setSelectedIndex(tabs.indexOfTab("👤 Members")); }
            @Override public void goBorrow()  { tabs.setSelectedIndex(tabs.indexOfTab("🔄 Borrow / Return")); }
            @Override public void showAvailableBooks() { goBooks(); booksPanel.showAvailableOnly(); }
            @Override public void showDueSoon()        { goBorrow(); borrowPanel.showDueSoon(7); } // 7 days
        };

        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.addTab("🏠 Dashboard", new DashboardPanel(service, actions));
        tabs.addTab("📚 Books", booksPanel);
        tabs.addTab("👤 Members", membersPanel);
        tabs.addTab("🔄 Borrow / Return", borrowPanel);
        tabs.addTab("📈 Reports", new ReportsPanel(service));
        tabs.addTab("⚙️ Settings", new SettingsPanel(() -> {
            darkToggle.setSelected(!darkToggle.isSelected());
            DARK_MODE = darkToggle.isSelected();
            PREFS.putBoolean("dark", DARK_MODE);
            Theme.install(DARK_MODE);
            Theme.refreshAll();
        }));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);

        Theme.applyRoot(getContentPane());
        pack();
    }

    // ------------------- MENU BAR -------------------
    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu app = new JMenu("App");
        JMenu view = new JMenu("View");
        JMenu help = new JMenu("Help");

        JMenuItem exit = new JMenuItem("Exit ⛔");
        exit.setMnemonic(KeyEvent.VK_X);
        exit.addActionListener(e -> System.exit(0));

        darkToggle = new JCheckBoxMenuItem("🌗 Dark Mode");
        darkToggle.setSelected(DARK_MODE);
        darkToggle.addActionListener(e -> {
            DARK_MODE = darkToggle.isSelected();
            PREFS.putBoolean("dark", DARK_MODE);
            Theme.install(DARK_MODE);
            Theme.refreshAll();
            Toast.show(this, DARK_MODE ? "Dark Mode enabled" : "Light Mode enabled", Theme.PRIMARY);
        });

        JMenuItem about = new JMenuItem("About ℹ️");
        about.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Library Management System\nColorful Clean UI (Java Swing)\n© Your Name",
                        "About", JOptionPane.INFORMATION_MESSAGE)
        );

        app.add(exit);
        view.add(darkToggle);
        help.add(about);
        bar.add(app); bar.add(view); bar.add(help);
        return bar;
    }

    // ------------------- ACTIONS FOR CHIPS -------------------
    interface UIActions {
        void goBooks();
        void goMembers();
        void goBorrow();
        void showAvailableBooks();
        void showDueSoon();
    }

    // ------------------- DASHBOARD -------------------
    static class DashboardPanel extends JPanel {
        private final LMSService service;
        private final UIActions actions;

        DashboardPanel(LMSService service, UIActions actions) {
            super(new BorderLayout(16, 16));
            this.service = service;
            this.actions = actions;
            setBorder(new EmptyBorder(20, 22, 20, 22));

            // Banner (solid primary)
            JPanel banner = new JPanel(new BorderLayout());
            banner.setBackground(Theme.PRIMARY);
            banner.setBorder(new EmptyBorder(24, 26, 24, 26));

            JLabel title = new JLabel("Welcome to Library Management System");
            title.setFont(new Font("SansSerif", Font.BOLD, 28));
            title.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel("Manage books and members");
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            subtitle.setForeground(new Color(255, 255, 255, 220));

            JPanel texts = new JPanel(new GridLayout(0,1,0,6));
            texts.setOpaque(false);
            texts.add(title); texts.add(subtitle);

            JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
            actionsRow.setOpaque(false);
            JButton addBook   = Theme.btnLight("➕ Add Book");
            JButton addMember = Theme.btnLight("👤 Add Member");
            JButton borrow    = Theme.btnLight("📥 Borrow");
            actionsRow.add(addBook); actionsRow.add(addMember); actionsRow.add(borrow);

            banner.add(texts, BorderLayout.WEST);
            banner.add(actionsRow, BorderLayout.EAST);

            // Chips row (clickable)
            JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            chips.setBorder(new EmptyBorder(8, 0, 4, 0));
            chips.setOpaque(false);
            chips.add(new ActionChip("Books",     Theme.BLUE,   actions::goBooks));
            chips.add(new ActionChip("Members",   Theme.TEAL,   actions::goMembers));
            chips.add(new ActionChip("Borrowed",  Theme.PURPLE, actions::goBorrow));      // instead of "Loans"
            chips.add(new ActionChip("Due Soon",  Theme.ORANGE, actions::showDueSoon));   // instead of "Overdue"
            chips.add(new ActionChip("Available", Theme.GREEN,  actions::showAvailableBooks));

            // Metrics — colorful cards
            JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
            grid.add(metricCard("📚 Books",           String.valueOf(service.getBooks().size()),   Theme.BLUE));
            grid.add(metricCard("👤 Members",         String.valueOf(service.getMembers().size()), Theme.TEAL));
            grid.add(metricCard("📥 Borrowed Items",  String.valueOf(service.getLoans().size()),   Theme.PURPLE));
            grid.add(metricCard("⏳ Due Soon",        String.valueOf(dueSoonCount(service, 7)),     Theme.ORANGE));
            grid.add(metricCard("✅ Available Books", String.valueOf(availableCount(service)),      Theme.GREEN));
            grid.add(metricCard("📅 Borrowed Today",  String.valueOf(borrowedToday(service)),       Theme.BLUE));

            add(banner, BorderLayout.NORTH);
            add(chips, BorderLayout.CENTER);
            add(grid, BorderLayout.SOUTH);

            Theme.applyRoot(this);

            // Hints
            addBook.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Books → Add Book.", "Hint", JOptionPane.INFORMATION_MESSAGE));
            addMember.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Members → Add Member.", "Hint", JOptionPane.INFORMATION_MESSAGE));
            borrow.addActionListener(e -> JOptionPane.showMessageDialog(this, "Go to Borrow / Return tab.", "Hint", JOptionPane.INFORMATION_MESSAGE));
        }

        private int availableCount(LMSService s) {
            int c = 0; for (Book b : s.getBooks()) if (b.available) c++; return c;
        }
        private int borrowedToday(LMSService s) {
            int c = 0; for (Loan l : s.getLoans()) if (l.dueDate.minusDays(14).equals(LocalDate.now())) c++; return c;
        }
        private int dueSoonCount(LMSService s, int days) {
            int c = 0;
            LocalDate now = LocalDate.now(), limit = now.plusDays(days);
            for (Loan l : s.getLoans()) if (!l.dueDate.isBefore(now) && !l.dueDate.isAfter(limit)) c++;
            return c;
        }

        private JPanel metricCard(String title, String value, Color bg) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(bg);
            card.setBorder(new EmptyBorder(16, 18, 16, 18));
            JLabel t = new JLabel(title);
            t.setFont(new Font("SansSerif", Font.BOLD, 16));
            t.setForeground(Color.WHITE);
            JLabel v = new JLabel(value, SwingConstants.RIGHT);
            v.setFont(new Font("SansSerif", Font.BOLD, 26));
            v.setForeground(Color.WHITE);
            card.add(t, BorderLayout.WEST);
            card.add(v, BorderLayout.EAST);
            return card;
        }
    }

    // ------------------- BOOKS -------------------
    static class BooksPanel extends JPanel {
        private final LMSService service;
        private final DefaultTableModel model;
        private final JTable table;
        private final JTextField searchField;
        private TableRowSorter<DefaultTableModel> sorter;

        BooksPanel(LMSService service) {
            super(new BorderLayout(12, 12));
            this.service = service;
            setBorder(new EmptyBorder(16, 16, 16, 16));

            JPanel header = new JPanel(new BorderLayout(10, 0));
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.LINE, 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            JLabel title = new JLabel("📚 Books");
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            header.add(title, BorderLayout.WEST);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            searchField = new JTextField(22);
            JButton searchBtn = Theme.btnPrimary("🔍 Search");
            JButton clearBtn  = Theme.btnNeutral("✖ Clear");
            right.add(searchField); right.add(searchBtn); right.add(clearBtn);
            header.add(right, BorderLayout.EAST);

            model = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Year", "Available"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            Theme.styleTable(table);
            refreshTable();

            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { applyFilter(); }
                public void removeUpdate(DocumentEvent e) { applyFilter(); }
                public void changedUpdate(DocumentEvent e) { applyFilter(); }
            });
            searchBtn.addActionListener(e -> applyFilter());
            clearBtn.addActionListener(e -> { searchField.setText(""); applyFilter(); clearFilter(); });

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton add = Theme.btnPrimary("➕ Add Book");
            JButton edit = Theme.btnSecondary("✏️ Edit Book");
            JButton del = Theme.btnDanger("🗑️ Delete Book");
            actions.add(add); actions.add(edit); actions.add(del);

            add(header, BorderLayout.NORTH);
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            Theme.applyRoot(this);

            add.addActionListener(e -> addBookDialog());
            edit.addActionListener(e -> editSelectedBook());
            del.addActionListener(e -> deleteSelectedBook());
            registerShortcuts();
        }

        private void registerShortcuts() {
            JComponent root = this;
            int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); // Java 8
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask), "addBook");
            root.getActionMap().put("addBook", new AbstractAction() { public void actionPerformed(ActionEvent e) { addBookDialog(); }});
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, mask), "focusSearch");
            root.getActionMap().put("focusSearch", new AbstractAction() { public void actionPerformed(ActionEvent e) { searchField.requestFocus(); }});
        }

        private void refreshTable() {
            model.setRowCount(0);
            for (Book b : service.getBooks()) {
                model.addRow(new Object[]{b.id, b.title, b.author, b.year, b.available});
            }
        }

        private void applyFilter() {
            String q = searchField.getText().trim();
            sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + q));
        }

        public void showAvailableOnly() {
            if (sorter == null) return;
            sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    Object v = entry.getValue(4); // "Available" column
                    return v != null && String.valueOf(v).equalsIgnoreCase("true");
                }
            });
        }

        public void clearFilter() {
            if (sorter != null) sorter.setRowFilter(null);
        }

        private boolean validateYearField(JTextField yearField) {
            try {
                int y = Integer.parseInt(yearField.getText().trim());
                int now = java.time.Year.now().getValue();
                if (y < 1500 || y > now) throw new NumberFormatException();
                yearField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xD1D5DB)),
                        new EmptyBorder(6,8,6,8)
                ));
                return true;
            } catch (Exception ex) {
                yearField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xEF4444), 2),
                        new EmptyBorder(6,8,6,8)
                ));
                yearField.requestFocus();
                return false;
            }
        }

        private void addBookDialog() {
            JTextField id = new JTextField();
            JTextField title = new JTextField();
            JTextField author = new JTextField();
            JTextField year = new JTextField();
            JPanel form = formPanel(new String[]{"🆔 ID", "📖 Title", "✍️ Author", "📅 Year"},
                    new JComponent[]{id, title, author, year});
            int ok = JOptionPane.showConfirmDialog(this, form, "Add Book", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                if (!validateYearField(year)) return;
                Book b = new Book(id.getText().trim(), title.getText().trim(), author.getText().trim(),
                        Integer.parseInt(year.getText().trim()), true);
                boolean added = service.addBook(b);
                if (added) { refreshTable(); Toast.show(this, "Book added", Theme.BLUE); }
                else { JOptionPane.showMessageDialog(this, "Book ID already exists.", "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }

        private void editSelectedBook() {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Please select a book to edit.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            int modelRow = table.convertRowIndexToModel(r);
            String idVal = model.getValueAt(modelRow, 0).toString();
            Book existing = service.findBook(idVal);
            if (existing == null) { JOptionPane.showMessageDialog(this, "Selected book not found.", "Error", JOptionPane.ERROR_MESSAGE); return; }

            JTextField title = new JTextField(existing.title);
            JTextField author = new JTextField(existing.author);
            JTextField year = new JTextField(String.valueOf(existing.year));
            JCheckBox available = new JCheckBox("Available", existing.available);

            JPanel form = formPanel(new String[]{"📖 Title", "✏️ Author", "📅 Year", "✅ Status"},
                    new JComponent[]{title, author, year, available});
            int ok = JOptionPane.showConfirmDialog(this, form, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                if (!validateYearField(year)) return;
                Book updated = new Book(existing.id, title.getText().trim(), author.getText().trim(),
                        Integer.parseInt(year.getText().trim()), available.isSelected());
                boolean done = service.updateBook(updated);
                if (done) { refreshTable(); Toast.show(this, "Book updated", Theme.BLUE); }
                else { JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }

        private void deleteSelectedBook() {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Please select a book to delete.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            int modelRow = table.convertRowIndexToModel(r);
            String idVal = model.getValueAt(modelRow, 0).toString();
            int c = JOptionPane.showConfirmDialog(this, "Delete book " + idVal + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                boolean done = service.deleteBook(idVal);
                if (done) { refreshTable(); Toast.show(this, "Book deleted", Theme.BLUE); }
                else { JOptionPane.showMessageDialog(this, "Delete failed (maybe on loan).", "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }

        private JPanel formPanel(String[] labels, JComponent[] fields) {
            JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
            for (int i = 0; i < labels.length; i++) {
                p.add(new JLabel(labels[i]));
                p.add(fields[i]);
            }
            Theme.applyRoot(p);
            return p;
        }
    }

    // ------------------- MEMBERS -------------------
    static class MembersPanel extends JPanel {
        private final LMSService service;
        private final DefaultTableModel model;
        private final JTable table;

        MembersPanel(LMSService service) {
            super(new BorderLayout(12, 12));
            this.service = service;
            setBorder(new EmptyBorder(16, 16, 16, 16));

            JPanel header = new JPanel(new BorderLayout());
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.LINE, 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            JLabel t = new JLabel("👤 Members");
            t.setFont(new Font("SansSerif", Font.BOLD, 20));
            header.add(t, BorderLayout.WEST);

            model = new DefaultTableModel(new Object[]{"ID", "Name", "Phone"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            Theme.styleTable(table);
            refresh();

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            JButton add = Theme.btnPrimary("➕ Add Member");
            JButton edit = Theme.btnSecondary("✏️ Edit");
            JButton remove = Theme.btnDanger("🗑️ Remove");
            actions.add(add); actions.add(edit); actions.add(remove);

            add(header, BorderLayout.NORTH);
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            Theme.applyRoot(this);

            add.addActionListener(e -> addMemberDialog());
            edit.addActionListener(e -> editSelectedMember());
            remove.addActionListener(e -> removeSelectedMember());
        }

        private void refresh() {
            model.setRowCount(0);
            for (Member m : service.getMembers()) {
                model.addRow(new Object[]{m.id, m.name, m.phone});
            }
        }

        private boolean validatePhone(JTextField phone) {
            String p = phone.getText().trim();
            if (p.matches("\\d{7,15}")) {
                phone.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0xD1D5DB)),
                        new EmptyBorder(6,8,6,8)
                ));
                return true;
            }
            phone.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xEF4444), 2),
                    new EmptyBorder(6,8,6,8)
            ));
            phone.requestFocus();
            return false;
        }

        private void addMemberDialog() {
            JTextField id = new JTextField();
            JTextField name = new JTextField();
            JTextField phone = new JTextField();
            JPanel form = formPanel(new String[]{"🆔 ID", "👤 Name", "📞 Phone"}, new JComponent[]{id, name, phone});
            int ok = JOptionPane.showConfirmDialog(this, form, "Add Member", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                if (!validatePhone(phone)) return;
                Member m = new Member(id.getText().trim(), name.getText().trim(), phone.getText().trim());
                boolean added = service.addMember(m);
                if (added) { refresh(); Toast.show(this, "Member added", Theme.TEAL); }
                else { JOptionPane.showMessageDialog(this, "Member ID already exists.", "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }

        private void editSelectedMember() {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Please select a member.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            String idVal = model.getValueAt(table.convertRowIndexToModel(r), 0).toString();
            Member existing = service.findMember(idVal);
            if (existing == null) { JOptionPane.showMessageDialog(this, "Member not found.", "Error", JOptionPane.ERROR_MESSAGE); return; }

            JTextField name = new JTextField(existing.name);
            JTextField phone = new JTextField(existing.phone);
            JPanel form = formPanel(new String[]{"👤 Name", "📞 Phone"}, new JComponent[]{name, phone});
            int ok = JOptionPane.showConfirmDialog(this, form, "Edit Member", JOptionPane.OK_CANCEL_OPTION);
            if (ok == JOptionPane.OK_OPTION) {
                if (!validatePhone(phone)) return;
                existing.name = name.getText().trim();
                existing.phone = phone.getText().trim();
                boolean done = service.updateMember(existing);
                if (done) { refresh(); Toast.show(this, "Member updated", Theme.TEAL); }
                else { JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }

        private void removeSelectedMember() {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Please select a member.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            String idVal = model.getValueAt(table.convertRowIndexToModel(r), 0).toString();
            int c = JOptionPane.showConfirmDialog(this, "Delete member " + idVal + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                boolean done = service.deleteMember(idVal);
                if (done) { refresh(); Toast.show(this, "Member removed", Theme.TEAL); }
                else { JOptionPane.showMessageDialog(this, "Delete failed (has active loans?).", "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }

        private JPanel formPanel(String[] labels, JComponent[] fields) {
            JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
            for (int i = 0; i < labels.length; i++) {
                p.add(new JLabel(labels[i]));
                p.add(fields[i]);
            }
            Theme.applyRoot(p);
            return p;
        }
    }

    // ------------------- BORROW/RETURN -------------------
    static class BorrowReturnPanel extends JPanel {
        private final LMSService service;
        private final JTextField memberId = new JTextField();
        private final JTextField bookId = new JTextField();

        private final DefaultTableModel loansModel = new DefaultTableModel(new Object[]{"Member", "Book", "Due", "Ext"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        private final JTable loansTable = new JTable(loansModel);
        private TableRowSorter<DefaultTableModel> loansSorter;

        BorrowReturnPanel(LMSService service) {
            super(new BorderLayout(12, 12));
            this.service = service;
            setBorder(new EmptyBorder(16, 16, 16, 16));

            JPanel header = new JPanel(new BorderLayout());
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.LINE, 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            JLabel t = new JLabel("🔄 Borrow / Return");
            t.setFont(new Font("SansSerif", Font.BOLD, 20));
            header.add(t, BorderLayout.WEST);

            JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
            form.add(new JLabel("👤 Member ID")); form.add(memberId);
            form.add(new JLabel("📚 Book ID"));   form.add(bookId);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton borrow = Theme.btnPrimary("📥 Borrow");
            JButton extend = Theme.btnSecondary("⏩ Extend +14 days");
            JButton ret    = Theme.btnPrimary("📤 Return");
            actions.add(borrow); actions.add(extend); actions.add(ret);

            Theme.styleTable(loansTable);
            JScrollPane loansPane = new JScrollPane(loansTable);
            loansPane.setPreferredSize(new Dimension(460, 0));
            loansSorter = new TableRowSorter<>(loansModel);
            loansTable.setRowSorter(loansSorter);

            add(header, BorderLayout.NORTH);
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            add(loansPane, BorderLayout.EAST);
            Theme.applyRoot(this);

            borrow.addActionListener(e -> onBorrow());
            extend.addActionListener(e -> onExtend());
            ret.addActionListener(e -> onReturnBook());

            refreshLoans();
        }

        private void onBorrow() {
            String m = memberId.getText().trim();
            String b = bookId.getText().trim();
            if (m.isEmpty() || b.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Member ID and Book ID are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LMSResult res = service.borrow(m, b);
            showResult(res, "Borrowed successfully.");
            refreshLoans();
        }

        private void onExtend() {
            String m = memberId.getText().trim();
            String b = bookId.getText().trim();
            if (m.isEmpty() || b.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Member ID and Book ID are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LMSResult res = service.extend(m, b);
            showResult(res, "Extended by 14 days.");
            refreshLoans();
        }

        private void onReturnBook() {
            String m = memberId.getText().trim();
            String b = bookId.getText().trim();
            if (m.isEmpty() || b.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Member ID and Book ID are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LMSResult res = service.returnBook(m, b);
            showResult(res, "Returned successfully.");
            refreshLoans();
        }

        private void showResult(LMSResult res, String successMsg) {
            if (res.ok) Toast.show(this, successMsg, Theme.BLUE);
            else JOptionPane.showMessageDialog(this, res.message, "Error", JOptionPane.ERROR_MESSAGE);
        }

        private void refreshLoans() {
            loansModel.setRowCount(0);
            for (Loan l : service.getLoans()) {
                loansModel.addRow(new Object[]{l.memberId, l.bookId, l.dueDate.toString(), l.extensions});
            }
            loansTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    String dueStr = table.getValueAt(row, 2).toString();
                    LocalDate due = LocalDate.parse(dueStr);
                    if (due.isBefore(LocalDate.now())) c.setForeground(Theme.RED);
                    else c.setForeground(Theme.FG);
                    return c;
                }
            });
        }

        public void showDueSoon(int days) {
            final LocalDate now = LocalDate.now();
            final LocalDate limit = now.plusDays(days);
            loansSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    String dueStr = String.valueOf(entry.getValue(2));
                    try {
                        LocalDate due = LocalDate.parse(dueStr);
                        return !due.isBefore(now) && !due.isAfter(limit);
                    } catch (Exception ex) { return false; }
                }
            });
        }

        public void clearDueSoon() {
            if (loansSorter != null) loansSorter.setRowFilter(null);
        }
    }

    // ------------------- REPORTS -------------------
    static class ReportsPanel extends JPanel {
        ReportsPanel(LMSService service) {
            super(new BorderLayout(12, 12));
            setBorder(new EmptyBorder(16, 16, 16, 16));

            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setText("📈 Reports\n\n"
                    + "- Current books: " + service.getBooks().size() + "\n"
                    + "- Current members: " + service.getMembers().size() + "\n"
                    + "- Borrowed items: " + service.getLoans().size() + "\n\n"
                    + "Export data to CSV for quick sharing.\n");
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton exportBooks   = Theme.btnSecondary("Export Books CSV");
            JButton exportMembers = Theme.btnSecondary("Export Members CSV");
            JButton exportLoans   = Theme.btnSecondary("Export Loans CSV");
            actions.add(exportBooks); actions.add(exportMembers); actions.add(exportLoans);

            exportBooks.addActionListener(e -> chooseAndExport(file -> exportBooksCSV(service, file)));
            exportMembers.addActionListener(e -> chooseAndExport(file -> exportMembersCSV(service, file)));
            exportLoans.addActionListener(e -> chooseAndExport(file -> exportLoansCSV(service, file)));

            add(new JScrollPane(area), BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            Theme.applyRoot(this);
        }

        private interface FileExporter { void export(java.io.File f) throws Exception; }

        private void chooseAndExport(FileExporter exporter) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save CSV");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new java.io.File(file.getParentFile(), file.getName() + ".csv");
                }
                try {
                    exporter.export(file);
                    Toast.show(this, "Exported: " + file.getName(), Theme.PURPLE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void exportBooksCSV(LMSService service, java.io.File file) throws Exception {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file, "UTF-8")) {
                pw.println("ID,Title,Author,Year,Available");
                for (Book b : service.getBooks()) {
                    pw.printf("%s,%s,%s,%d,%s%n",
                            safeCSV(b.id), safeCSV(b.title), safeCSV(b.author), b.year, b.available);
                }
            }
        }

        private void exportMembersCSV(LMSService service, java.io.File file) throws Exception {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file, "UTF-8")) {
                pw.println("ID,Name,Phone");
                for (Member m : service.getMembers()) {
                    pw.printf("%s,%s,%s%n", safeCSV(m.id), safeCSV(m.name), safeCSV(m.phone));
                }
            }
        }

        private void exportLoansCSV(LMSService service, java.io.File file) throws Exception {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file, "UTF-8")) {
                pw.println("MemberID,BookID,DueDate,Extensions");
                for (Loan l : service.getLoans()) {
                    pw.printf("%s,%s,%s,%d%n", safeCSV(l.memberId), safeCSV(l.bookId), l.dueDate, l.extensions);
                }
            }
        }

        private String safeCSV(String s) {
            if (s == null) return "";
            return s.replace(",", " ");
        }
    }

    // ------------------- SETTINGS -------------------
    static class SettingsPanel extends JPanel {
        SettingsPanel(Runnable toggleDark) {
            super(new BorderLayout(12, 12));
            setBorder(new EmptyBorder(16, 16, 16, 16));

            JLabel l = new JLabel("⚙️ Settings");
            l.setFont(new Font("SansSerif", Font.BOLD, 18));
            JButton toggle = Theme.btnSecondary("🌗 Toggle Dark Mode");
            toggle.addActionListener(e -> toggleDark.run());

            JPanel top = new JPanel(new BorderLayout());
            top.add(l, BorderLayout.WEST);
            top.add(toggle, BorderLayout.EAST);

            add(top, BorderLayout.NORTH);
            Theme.applyRoot(this);
        }
    }

    // ==================== THEME (Fixed colorful palette) ====================
    static class Theme {
        // Base
        static Color BG, FG, CTRL, PRIMARY, SECONDARY, DANGER, LINE, FIELD_BG, FIELD_FG;
        // Extra colors for chips/cards
        static final Color BLUE   = new Color(0x2563EB);
        static final Color TEAL   = new Color(0x14B8A6);
        static final Color PURPLE = new Color(0x8B5CF6);
        static final Color GREEN  = new Color(0x16A34A);
        static final Color ORANGE = new Color(0xF59E0B);
        static final Color RED    = new Color(0xEF4444);

        static void install(boolean dark) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

            if (dark) {
                BG        = new Color(0x14161A);
                FG        = new Color(0xE9ECEF);
                CTRL      = new Color(0x1D2127);
                PRIMARY   = new Color(0x3B82F6);
                SECONDARY = new Color(0x60A5FA);
                DANGER    = new Color(0xF87171);
                LINE      = new Color(0x2B3138);
                FIELD_BG  = new Color(0x22262C);
                FIELD_FG  = FG;
            } else {
                BG        = new Color(0xF7FAFC);
                FG        = new Color(0x111827);
                CTRL      = Color.WHITE;
                PRIMARY   = new Color(0x2563EB);
                SECONDARY = new Color(0x60A5FA);
                DANGER    = new Color(0xEF4444);
                LINE      = new Color(0xE5E7EB);
                FIELD_BG  = Color.WHITE;
                FIELD_FG  = FG;
            }

            UIManager.put("Panel.background", BG);
            UIManager.put("Panel.foreground", FG);
            UIManager.put("Label.foreground", FG);
            UIManager.put("MenuBar.background", CTRL);
            UIManager.put("MenuBar.foreground", FG);
            UIManager.put("Menu.background", CTRL);
            UIManager.put("Menu.foreground", FG);
            UIManager.put("MenuItem.background", CTRL);
            UIManager.put("MenuItem.foreground", FG);
            UIManager.put("CheckBoxMenuItem.background", CTRL);
            UIManager.put("CheckBoxMenuItem.foreground", FG);
            UIManager.put("TabbedPane.background", BG);
            UIManager.put("TabbedPane.foreground", FG);
            UIManager.put("Table.background", CTRL);
            UIManager.put("Table.foreground", FG);
            UIManager.put("Table.gridColor", LINE);
            UIManager.put("ScrollPane.background", BG);
            UIManager.put("Viewport.background", BG);
            UIManager.put("TextField.background", FIELD_BG);
            UIManager.put("TextField.foreground", FIELD_FG);
            UIManager.put("Button.background", CTRL);
            UIManager.put("Button.foreground", FG);

            Font base = new Font("SansSerif", Font.PLAIN, 14);
            UIManager.put("Label.font", base);
            UIManager.put("Button.font", base);
            UIManager.put("TextField.font", base);
            UIManager.put("Menu.font", base);
            UIManager.put("MenuItem.font", base);
        }

        static void refreshAll() {
            for (Window w : Window.getWindows()) {
                if (w == null) continue;
                SwingUtilities.updateComponentTreeUI(w);
                if (w instanceof JFrame) applyRoot(((JFrame) w).getContentPane());
                else if (w instanceof JDialog) applyRoot(((JDialog) w).getContentPane());
            }
        }

        static void applyRoot(Container root) {
            if (root == null) return;
            root.setBackground(BG);
            root.setForeground(FG);
            for (Component child : root.getComponents()) style(child);
        }

        private static void style(Component c) {
            if (c instanceof JScrollPane sp) {
                sp.getViewport().setBackground(BG);
                sp.setBackground(BG);
            } else if (c instanceof JTable t) {
                styleTable(t);
            } else if (c instanceof JTextField tf) {
                tf.setBackground(FIELD_BG);
                tf.setForeground(FIELD_FG);
                tf.setCaretColor(FG);
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LINE),
                        new EmptyBorder(8, 10, 8, 10)
                ));
            } else if (c instanceof JLabel l) {
                l.setForeground(FG);
            } else if (c instanceof JPanel p) {
                p.setBackground(BG);
                p.setForeground(FG);
            }
            if (c instanceof Container cont) for (Component child : cont.getComponents()) style(child);
        }

        static void styleTable(JTable table) {
            table.setBackground(CTRL);
            table.setForeground(FG);
            table.setGridColor(LINE);
            table.setRowHeight(30);
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                header.setBackground(DARK_MODE ? new Color(0x1E293B) : new Color(0xEEF2FF));
                header.setForeground(FG);
                header.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
            table.setSelectionBackground(DARK_MODE ? SECONDARY : new Color(0xDBEAFE));
            table.setSelectionForeground(FG);
        }

        // Buttons
        static JButton btnPrimary(String text)  { return flatBtn(text, PRIMARY, Color.WHITE); }
        static JButton btnSecondary(String text){ return flatBtn(text, SECONDARY, Color.WHITE); }
        static JButton btnDanger(String text)   { return flatBtn(text, DANGER, Color.WHITE); }
        static JButton btnNeutral(String text)  { return flatBtn(text, CTRL, FG); }
        static JButton btnLight(String text)    { return flatBtn(text, new Color(255,255,255,40), Color.WHITE); }

        private static JButton flatBtn(String text, Color bg, Color fg) {
            JButton b = new JButton(text);
            b.setBackground(bg);
            b.setForeground(fg);
            b.setFocusPainted(false);
            b.setBorder(new EmptyBorder(10, 16, 10, 16));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return b;
        }
    }

    // ------------------- CHIP / PILL -------------------
    static class Pill extends JLabel {
        private final Color bg;
        Pill(String text, Color bg) {
            super(text);
            this.bg = bg;
            setForeground(Color.WHITE);
            setOpaque(false);
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ActionChip extends Pill {
        ActionChip(String text, Color bg, Runnable onClick) {
            super(text, bg);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Click");
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { setForeground(new Color(255,255,255,230)); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { setForeground(Color.WHITE); }
            });
        }
    }

    // ------------------- TOAST -------------------
    static class Toast {
        static void show(Component ownerComp, String msg, Color bg) {
            Window ownerWin = SwingUtilities.getWindowAncestor(ownerComp);
            JWindow w = new JWindow(ownerWin);
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(bg);
            p.setBorder(new EmptyBorder(8, 14, 8, 14));
            JLabel l = new JLabel(msg);
            l.setForeground(Color.WHITE);
            p.add(l, BorderLayout.CENTER);
            w.add(p);
            w.pack();
            Point pt;
            if (ownerWin != null) {
                pt = ownerWin.getLocationOnScreen();
                w.setLocation(pt.x + ownerWin.getWidth() - w.getWidth() - 24, pt.y + 24);
            } else {
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                w.setLocation(screen.width - w.getWidth() - 24, 24);
            }
            w.setAlwaysOnTop(true);
            w.setVisible(true);
            javax.swing.Timer t = new javax.swing.Timer(1400, new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    w.setVisible(false);
                    w.dispose();
                }
            });
            t.setRepeats(false);
            t.start();
        }
    }

    // ------------------- WELCOME OVERLAY -------------------
    static class WelcomeOverlay {
        static void show(String message, int millis) {
            final JWindow w = new JWindow();
            JPanel root = new JPanel(new BorderLayout());
            root.setBorder(new EmptyBorder(30, 40, 30, 40));
            root.setBackground(Theme.PRIMARY);

            JLabel title = new JLabel(message, SwingConstants.CENTER);
            title.setForeground(Color.WHITE);
            title.setFont(new Font("SansSerif", Font.BOLD, 28));

            root.add(title, BorderLayout.CENTER);
            w.add(root);
            w.setSize(600, 200);
            w.setLocationRelativeTo(null);
            w.setAlwaysOnTop(true);
            w.setVisible(true);

            javax.swing.Timer t = new javax.swing.Timer(millis, new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    w.setVisible(false);
                    w.dispose();
                }
            });
            t.setRepeats(false);
            t.start();
        }
    }

    // ------------------- LOGIN (Bigger, with Show/Remember) -------------------
    static class LoginDialog {
        static boolean show() {
            JDialog dlg = new JDialog((Frame) null, "Login", true);
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setSize(720, 480);
            dlg.setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(0, 0));

            // Left hero
            JPanel hero = new JPanel(new BorderLayout());
            hero.setBackground(Theme.PRIMARY);
            hero.setBorder(new EmptyBorder(30, 28, 30, 28));
            JLabel icon = new JLabel("📚", SwingConstants.CENTER);
            icon.setFont(new Font("SansSerif", Font.PLAIN, 72));
            JLabel welcome = new JLabel("Welcome to LMS", SwingConstants.CENTER);
            welcome.setForeground(Color.WHITE);
            welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
            hero.add(icon, BorderLayout.CENTER);
            hero.add(welcome, BorderLayout.SOUTH);
            hero.setPreferredSize(new Dimension(280, 480));

            // Right form
            JPanel formCard = new JPanel(new BorderLayout());
            formCard.setBorder(new EmptyBorder(26, 28, 26, 28));
            JLabel title = new JLabel("Sign in");
            title.setFont(new Font("SansSerif", Font.BOLD, 22));

            JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
            JTextField user = new JTextField();
            JPasswordField pass = new JPasswordField();

            final Preferences LOGIN_PREFS = Preferences.userRoot().node("lms_gui_login");
            user.setText(LOGIN_PREFS.get("last_user", ""));

            char defaultEcho = pass.getEchoChar();
            JCheckBox show = new JCheckBox("Show password");
            show.addActionListener(e -> pass.setEchoChar(show.isSelected() ? (char)0 : defaultEcho));
            JCheckBox remember = new JCheckBox("Remember me");
            remember.setSelected(!user.getText().trim().isEmpty());

            form.add(new JLabel("👤 Username")); form.add(user);
            form.add(new JLabel("🔒 Password")); form.add(pass);
            form.add(new JLabel(""));            form.add(show);
            form.add(new JLabel(""));            form.add(remember);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            JButton ok = Theme.btnPrimary("Sign in");
            JButton cancel = Theme.btnNeutral("Cancel");
            actions.add(cancel); actions.add(ok);

            cancel.addActionListener(e -> { dlg.dispose(); });
            final boolean[] success = {false};
            ok.addActionListener(e -> {
                String u = user.getText().trim();
                String p = new String(pass.getPassword());
                // Demo credentials: admin / 1234
                if (u.equals("admin") && p.equals("1234")) {
                    if (remember.isSelected()) LOGIN_PREFS.put("last_user", u);
                    else LOGIN_PREFS.remove("last_user");
                    success[0] = true;
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            formCard.add(title, BorderLayout.NORTH);
            formCard.add(form, BorderLayout.CENTER);
            formCard.add(actions, BorderLayout.SOUTH);

            root.add(hero, BorderLayout.WEST);
            root.add(formCard, BorderLayout.CENTER);

            Theme.applyRoot(root);
            dlg.setContentPane(root);
            dlg.setVisible(true);
            return success[0];
        }
    }

    // ------------------- SERVICE & MODELS -------------------
    interface LMSService {
        // books
        List<Book> getBooks();
        Book findBook(String id);
        boolean addBook(Book b);
        boolean updateBook(Book b);
        boolean deleteBook(String id);
        List<Book> searchBooks(String query);

        // members
        List<Member> getMembers();
        Member findMember(String id);
        boolean addMember(Member m);
        boolean updateMember(Member m);
        boolean deleteMember(String id);

        // loans
        List<Loan> getLoans();
        LMSResult borrow(String memberId, String bookId);
        LMSResult extend(String memberId, String bookId);
        LMSResult returnBook(String memberId, String bookId);
    }

    static class SimpleInMemoryLMSService implements LMSService {
        private final Map<String, Book> books = new LinkedHashMap<>();
        private final Map<String, Member> members = new LinkedHashMap<>();
        private final Map<String, Loan> loans = new LinkedHashMap<>(); // key: memberId+"|"+bookId

        SimpleInMemoryLMSService() {
            addBook(new Book("B-001", "Clean Code", "Robert C. Martin", 2008, true));
            addBook(new Book("B-002", "Effective Java", "Joshua Bloch", 2018, true));
            addBook(new Book("B-003", "Introduction to Algorithms", "CLRS", 2009, false));

            addMember(new Member("M-100", "Alice Johnson", "0100000000"));
            addMember(new Member("M-101", "Bob Smith", "0111111111"));
        }

        public List<Book> getBooks() { return new ArrayList<>(books.values()); }
        public Book findBook(String id) { return books.get(id); }
        public boolean addBook(Book b) {
            if (b == null || b.id == null || b.id.isEmpty() || books.containsKey(b.id)) return false;
            books.put(b.id, b); return true;
        }
        public boolean updateBook(Book b) {
            if (b == null || !books.containsKey(b.id)) return false;
            books.put(b.id, b); return true;
        }
        public boolean deleteBook(String id) {
            if (id == null || !books.containsKey(id)) return false;
            for (Loan l : loans.values()) if (l.bookId.equals(id)) return false;
            books.remove(id); return true;
        }
        public List<Book> searchBooks(String query) {
            String q = query == null ? "" : query.toLowerCase();
            List<Book> res = new ArrayList<>();
            for (Book b : books.values()) {
                if ((b.id != null && b.id.toLowerCase().contains(q))
                        || (b.title != null && b.title.toLowerCase().contains(q))
                        || (b.author != null && b.author.toLowerCase().contains(q))) {
                    res.add(b);
                }
            }
            return res;
        }

        public List<Member> getMembers() { return new ArrayList<>(members.values()); }
        public Member findMember(String id) { return members.get(id); }
        public boolean addMember(Member m) {
            if (m == null || m.id == null || m.id.isEmpty() || members.containsKey(m.id)) return false;
            members.put(m.id, m); return true;
        }
        public boolean updateMember(Member m) {
            if (m == null || !members.containsKey(m.id)) return false;
            members.put(m.id, m); return true;
        }
        public boolean deleteMember(String id) {
            if (id == null || !members.containsKey(id)) return false;
            for (Loan l : loans.values()) if (l.memberId.equals(id)) return false;
            members.remove(id); return true;
        }

        public List<Loan> getLoans() { return new ArrayList<>(loans.values()); }

        public LMSResult borrow(String memberId, String bookId) {
            Member m = members.get(memberId);
            Book b = books.get(bookId);
            if (m == null) return LMSResult.fail("Member not found.");
            if (b == null) return LMSResult.fail("Book not found.");
            if (!b.available) return LMSResult.fail("Book is not available.");
            String key = memberId + "|" + bookId;
            if (loans.containsKey(key)) return LMSResult.fail("This loan already exists.");
            Loan l = new Loan(memberId, bookId, LocalDate.now().plusDays(14), 0);
            loans.put(key, l);
            b.available = false;
            return LMSResult.ok();
        }

        public LMSResult extend(String memberId, String bookId) {
            String key = memberId + "|" + bookId;
            Loan l = loans.get(key);
            if (l == null) return LMSResult.fail("No active loan found.");
            if (l.dueDate.isBefore(LocalDate.now())) return LMSResult.fail("Cannot extend: overdue.");
            if (l.extensions >= 2) return LMSResult.fail("Maximum extensions reached.");
            l.dueDate = l.dueDate.plusDays(14);
            l.extensions++;
            return LMSResult.ok();
        }

        public LMSResult returnBook(String memberId, String bookId) {
            String key = memberId + "|" + bookId;
            Loan l = loans.remove(key);
            if (l == null) return LMSResult.fail("No active loan found.");
            Book b = books.get(bookId);
            if (b != null) b.available = true;
            return LMSResult.ok();
        }
    }

    // Models
    static class Book { String id, title, author; int year; boolean available;
        Book(String id, String title, String author, int year, boolean available) { this.id=id; this.title=title; this.author=author; this.year=year; this.available=available; } }
    static class Member { String id, name, phone; Member(String id, String name, String phone) { this.id=id; this.name=name; this.phone=phone; } }
    static class Loan { String memberId, bookId; LocalDate dueDate; int extensions;
        Loan(String memberId, String bookId, LocalDate dueDate, int extensions) { this.memberId=memberId; this.bookId=bookId; this.dueDate=dueDate; this.extensions=extensions; } }
    static class LMSResult { final boolean ok; final String message;
        private LMSResult(boolean ok, String msg) { this.ok=ok; this.message=msg; }
        static LMSResult ok() { return new LMSResult(true, ""); }
        static LMSResult fail(String msg) { return new LMSResult(false, msg); } }
}
