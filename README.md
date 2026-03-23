<div align="center">

<br/>

# 📚 Library Management System

**A Java Desktop Application with a colorful Swing GUI — built entirely in IntelliJ IDEA, with no external dependencies and no database.**

<br/>

[![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/idea/)
[![Java Swing](https://img.shields.io/badge/GUI-Java%20Swing-4A90D9?style=for-the-badge)]()
[![In-Memory](https://img.shields.io/badge/Storage-In--Memory-brightgreen?style=for-the-badge)]()
[![No DB](https://img.shields.io/badge/Database-None%20Required-lightgrey?style=for-the-badge)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

<br/>

[🐛 Report Bug](../../issues) · [✨ Request Feature](../../issues)

---

</div>

## 📋 Table of Contents

- [📖 Description](#-description)
- [✨ Features](#-features)
- [🏗️ Architecture & Data Structures](#️-architecture--data-structures)
- [📁 Project Structure](#-project-structure)
- [🖥️ GUI Overview](#️-gui-overview)
- [🚀 Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [How to Run](#how-to-run)
  - [Default Login](#default-login)
- [⚙️ How It Works](#️-how-it-works)
- [🤝 Contributing](#-contributing)
- [🗺️ Roadmap](#️-roadmap)
- [📜 License](#-license)
- [📬 Contact](#-contact)

---

## 📖 Description

**Library Management System (LMS)** is a fully functional desktop application written in **pure Java**, using **Java Swing** for the graphical interface and **in-memory data structures** for storage — meaning it requires **no database, no server, and no external libraries**.

The system is composed of two layers that work together:

- **Core Logic Layer** — A set of plain Java classes (`Book`, `Member`, `Borrowing`, `Billing`, `WaitingList`, `ReportGenerator`, `LibraryManagementSystem`) that implement all business rules using classic data structures like **Binary Search Trees**, **Queues**, **ArrayLists**, and **Heap Sort**.

- **GUI Layer** — A rich, colorful Java Swing interface (`LMSGUI`) featuring a login screen, tabbed dashboard, real-time search, sortable tables, toast notifications, light/dark mode, and CSV export — all with zero external dependencies.

> All data lives in memory during the session. The project is ideal as a university-level software engineering project demonstrating OOP, data structures, and GUI programming.

---

## ✨ Features

### 🔐 Authentication
- Login dialog with username and password
- "Show Password" toggle (reveals/hides the password field)
- "Remember Me" using `java.util.prefs.Preferences` (persists last username)
- Welcome overlay splash screen shown after successful login
- Default credentials: `admin` / `1234`

### 🏠 Dashboard
- Colorful metric cards showing: Total Books, Total Members, Borrowed Items, Due Soon, Available Books, Borrowed Today
- Clickable navigation chips: **Books**, **Members**, **Borrowed**, **Due Soon**, **Available**
- Quick-action buttons in the banner: Add Book, Add Member, Borrow
- All data updates live from the in-memory service

### 📚 Books Tab
- Searchable, sortable table (by ID, Title, Author, Year, Availability)
- Live search — filters as you type using `TableRowSorter` + `DocumentListener`
- **Add Book** — form dialog with year validation (must be between 1500 and current year)
- **Edit Book** — pre-filled form, update title/author/year/availability
- **Delete Book** — with confirmation; blocked if the book has an active loan
- Filter to show **available books only** (triggered from Dashboard chip)
- Keyboard shortcut: `Ctrl+N` to open Add Book, `Ctrl+F` to focus search

### 👤 Members Tab
- Table showing Member ID, Name, Phone
- **Add Member** — form dialog with phone number validation (7–15 digits)
- **Edit Member** — update name and phone
- **Remove Member** — blocked if the member has active loans

### 🔄 Borrow / Return Tab
- Input fields for Member ID and Book ID
- **Borrow** — issues book to member, sets due date 14 days from today
- **Extend +14 Days** — renews the loan (max 2 extensions; blocked if overdue)
- **Return** — returns the book and marks it available again
- Active loans table shows Member, Book, Due Date, and number of extensions
- Overdue rows are highlighted in **red** automatically
- **Due Soon** filter (triggered from Dashboard chip) — shows loans due within 7 days

### 📈 Reports Tab
- Summary text: total books, members, and active borrowed items
- **Export Books CSV** — saves ID, Title, Author, Year, Availability
- **Export Members CSV** — saves ID, Name, Phone
- **Export Loans CSV** — saves MemberID, BookID, DueDate, Extensions
- File chooser dialog; auto-appends `.csv` extension if missing

### ⚙️ Settings Tab
- Toggle **Dark Mode / Light Mode** (persisted via `Preferences`)
- Full theme refresh applied instantly to all open windows

### 🎨 UI & UX
- **Light and Dark modes** with a complete custom theme (`Theme` class)
- Toast notifications (non-blocking) for success actions
- Color-coded buttons: Primary (blue), Secondary (light blue), Danger (red), Neutral
- Colorful metric cards in 6 distinct colors (blue, teal, purple, orange, green)
- Clickable pill-shaped navigation chips with hover effects
- Minimum window size: 1240 × 820

---

## 🏗️ Architecture & Data Structures

The core business logic uses real data structures — not just ArrayList for everything:

### 📘 Binary Search Tree — `Book.BookBST`
Books are stored in a **BST keyed by Book ID**, enabling:
- `insert(Book)` — O(log n) average insertion
- `searchByID(int)` — O(log n) search
- `searchByTitle(String)` — O(n) full tree traversal
- `searchByAuthor(String)` — O(n) full tree traversal
- `printInOrder()` — sorted output by Book ID

### 📊 Heap Sort — `LibraryManagementSystem.displayMostBorrowedBooks()`
To find the **top N most borrowed books**:
1. BST is converted to array via in-order traversal
2. **Heap Sort** is applied using `heapify()` on `borrowCount`
3. Array is reversed to descending order
4. Top N books are printed

### 🔢 Queue — `WaitingList`
When a book is unavailable, members are added to a **per-book queue**:
- `Map<Integer, Queue<String>>` — one `LinkedList` queue per book ID
- `addToWaitingList(bookID, memberID)` — enqueue
- `removeFromWaitingList(bookID)` — dequeue (FIFO)
- On book return: next member in queue is automatically assigned the book

### 🔃 Selection Sort — `ReportGenerator`
Report data items are sorted using **Selection Sort** before generating any report output.

### 📋 ArrayList — Borrowing, Members, Billing
- `ArrayList<Borrowing>` — full borrow/return history
- `ArrayList<Member>` — registered members list
- `ArrayList<Billing>` — billing records per member

### 💰 Billing & Fine Calculation
- Fine rate: **5.0 EGP per overdue day**
- Tax rate: **10%** added on top of base fine
- Formula: `totalAmount = lateDays × 5.0 × 1.10`
- Payment history tracked per member in `ArrayList<Double>`

### 🔄 Borrow / Renew / Return Logic
- **Borrow**: checks availability → creates `Borrowing` record → sets due date 14 days from now → marks book unavailable → adds to member's borrowed list
- **Return**: finds open record → calculates late days → applies fine if overdue → marks book available → auto-assigns to next in waiting queue
- **Renew**: adds 14 days to due date → blocked if another member is waiting

---

## 📁 Project Structure

```
LibraryManagementSystem/
│
├── src/
│   ├── Main.java                      # Console demo — tests all features end-to-end
│   ├── App.java                       # Empty entry point (placeholder)
│   │
│   ├── LMSGUI.java                    # Full Swing GUI (all panels, theme, login)
│   │   ├── LoginDialog                # Login screen with show/remember
│   │   ├── WelcomeOverlay             # Splash screen after login
│   │   ├── DashboardPanel             # Metric cards + navigation chips
│   │   ├── BooksPanel                 # Searchable/sortable books table
│   │   ├── MembersPanel               # Members table with CRUD
│   │   ├── BorrowReturnPanel          # Borrow / Extend / Return workflow
│   │   ├── ReportsPanel               # Summary + CSV export
│   │   ├── SettingsPanel              # Dark mode toggle
│   │   ├── Theme                      # Full color system + button factory
│   │   ├── Toast                      # Non-blocking popup notifications
│   │   ├── ActionChip / Pill          # Clickable rounded navigation pills
│   │   ├── LMSService (interface)     # Service contract
│   │   └── SimpleInMemoryLMSService   # In-memory implementation of service
│   │
│   ├── LibraryManagementSystem.java   # Main controller (core logic)
│   ├── Book.java                      # Book model + BookBST inner class
│   ├── Member.java                    # Member model + borrowed books list
│   ├── Borrowing.java                 # Borrow record + issue/return/renew logic
│   ├── Billing.java                   # Fine tracking + payment history
│   ├── WaitingList.java               # Per-book waiting queue (HashMap + Queue)
│   └── ReportGenerator.java           # Report builder with selection sort
│
└── README.md
```

---

## 🖥️ GUI Overview

The GUI is launched from `LMSGUI.main()` and is organized as a `JTabbedPane` with 6 tabs:

| Tab | Icon | Purpose |
|-----|------|---------|
| Dashboard | 🏠 | Overview cards and quick navigation |
| Books | 📚 | Full book catalog with search and CRUD |
| Members | 👤 | Member registry with CRUD |
| Borrow / Return | 🔄 | Issue, extend, and return books |
| Reports | 📈 | Summary and CSV export |
| Settings | ⚙️ | Dark/light mode toggle |

**Menu Bar:**
- `App` → Exit
- `View` → Toggle Dark Mode
- `Help` → About dialog

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 8 or higher** (the project uses Java 8-compatible syntax)
  ```
  https://www.oracle.com/java/technologies/downloads/
  ```
- **IntelliJ IDEA** (Community Edition is free)
  ```
  https://www.jetbrains.com/idea/download/
  ```

Verify Java is installed:
```bash
java -version
# java version "8.x" or higher
```

> ✅ No Maven, no Gradle, no database, no external JARs needed.

---

### How to Run

**Option 1 — Run the GUI (recommended)**

1. Clone or download the project
   ```bash
   git clone https://github.com/your-username/library-management-system.git
   ```
2. Open IntelliJ IDEA → `File` → `Open` → select the project folder
3. Wait for IntelliJ to index the project
4. Open `LMSGUI.java`
5. Click the green ▶️ **Run** button next to the `main` method
6. The login screen will appear

**Option 2 — Run the Console Demo**

1. Open `Main.java`
2. Click ▶️ **Run**
3. The console will print a full walkthrough of all features: adding books, registering members, borrowing, returning, fines, waiting list, and reports

---

### Default Login

| Field | Value |
|-------|-------|
| **Username** | `admin` |
| **Password** | `1234` |

> The "Remember Me" checkbox saves your username between sessions using `java.util.prefs.Preferences`.

---

## ⚙️ How It Works

### Console Flow (`Main.java`)
The `Main` class demonstrates the full system in order:

1. Create a `LibraryManagementSystem` instance
2. Add 4 books into the BST (`b1` → `b4` with IDs 101–105)
3. Register 3 members (`M1`, `M2`, `M3`)
4. Lend book 103 to M1 → M2 and M3 added to waiting queue (book unavailable)
5. M1 returns book 103 → auto-assigned to M2 from queue
6. Attempt renewal of book 103 for M2 (may be blocked by waiting list)
7. Update member contact info
8. Search book by ID, title, and author
9. Update book info
10. Create billing accounts, add fines with 10% tax
11. Display top 3 most borrowed books (heap sort)
12. Display borrow history for M1
13. Display all members

### GUI Flow (`LMSGUI.java`)
The GUI uses `SimpleInMemoryLMSService` which is a separate in-memory implementation inside `LMSGUI.java`. It comes pre-loaded with 3 sample books and 2 sample members so the app is ready to use instantly on launch.

---

## 🤝 Contributing

Contributions are welcome! 🙌

1. **Fork** the repository
2. **Create** your feature branch
   ```bash
   git checkout -b feature/YourFeature
   ```
3. **Commit** your changes
   ```bash
   git commit -m "feat: add YourFeature"
   ```
4. **Push** and open a Pull Request
   ```bash
   git push origin feature/YourFeature
   ```

### Commit Convention

| Prefix | Use for |
|--------|---------|
| `feat:` | New feature |
| `fix:` | Bug fix |
| `ui:` | GUI or visual changes |
| `refactor:` | Code restructuring |
| `docs:` | Documentation |

---

## 🗺️ Roadmap

- [x] Binary Search Tree for book storage and search
- [x] Queue-based waiting list per book
- [x] Heap Sort for most-borrowed ranking
- [x] Fine calculation with 10% tax
- [x] Borrow / return / renew logic with due dates
- [x] Full Swing GUI with 6 tabs
- [x] Light and Dark mode
- [x] CSV export for books, members, and loans
- [x] Toast notifications
- [x] Login with Remember Me
- [ ] 🖨️ Print borrow receipts
- [ ] 💾 Save and load data to/from a file (JSON or CSV)
- [ ] 📧 Email reminders for overdue books
- [ ] 🔍 Advanced search with multiple filters
- [ ] 📊 Visual charts for borrow statistics

---

## 📜 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

## 📬 Contact

> 📌 *Replace with your real information.*

**Developer** — [@your-username](https://github.com/your-username)

📧 Email: `your.email@example.com`

🔗 Project: [https://github.com/your-username/library-management-system](https://github.com/your-username/library-management-system)

---

<div align="center">

<br/>

Built with ☕ Java — no database, no dependencies, just clean code

<br/>

⭐ If this project helped you, please give it a star!

</div>
