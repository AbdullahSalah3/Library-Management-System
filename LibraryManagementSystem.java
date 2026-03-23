
import java.util.ArrayList;

public class LibraryManagementSystem {
    private Book.BookBST books = new Book.BookBST();
    ArrayList<Member> members = new ArrayList<>();
    ArrayList<Borrowing> borrowings = new ArrayList<>();
    private WaitingList waitingList = new WaitingList();
    private ArrayList<Billing> billings = new ArrayList<>();
    private ReportGenerator reporter = new ReportGenerator();

    private static final double taxRate = 0.1;
    private static final double fineperDay = 5.0;

    // Getter needed by other methods
    public Book.BookBST getBooks() { return books; }

    // Book
    public void addBook(Book book) {
        getBooks().insert(book);
        book.addBook();
    }

    public Book searchBookByID(int id) { return getBooks().searchByID(id); }
    public Book searchBookByTitle(String title) { return getBooks().searchByTitle(title); }
    public Book searchBookByAuthor(String author) { return getBooks().searchByAuthor(author); }

    public void updateBookInfo(int bookID, String title, String author, String genre, int year) {
        Book book = getBooks().searchByID(bookID);
        if (book != null) {
            book.updateBookInfo(title, author, genre, year);
        } else {
            System.out.println("Book ID " + bookID + " not found for update!");
        }
    }

    public void displayAllBooks() {
        if (getBooks().root == null) {
            System.out.println("No books in the library yet.");
        } else {
            getBooks().printInOrder();
        }
    }

    // Member
    public void registerMember(Member member) {
        members.add(member);
        billings.add(new Billing(member.getMemberID()));
        member.registerMember();
    }

    public Member getMenberByID(String memberID){
        for (Member member : members) {
            if (member.getMemberID().equals(memberID)) return member;
        }
        return null;
    }

    public Member searchMember(String memberID) {
        for (Member member : members) {
            if (member.getMemberID().equals(memberID)) return member;
        }
        return null;
    }

    public void displayAllMembers() {
        for (Member member : members) {
            System.out.println(member.toString());
        }
    }

    public void updateMemberInfo(String memberID, String newName, String newContactInfo) {
        Member member = searchMember(memberID);
        if (member == null) {
            System.out.println("This Member not found");
            return;
        }
        if (newName != null && !newName.isEmpty()) member.setName(newName);
        if (newContactInfo != null && !newContactInfo.isEmpty()) member.updateContactInfo(newContactInfo);
        System.out.println("Member info updated successfully");
    }

    // Borrowing
    public boolean lendBook(String memberID, int bookID) {
        Book book = getBooks().searchByID(bookID);
        if (book == null) { System.out.println("Book ID " + bookID + " not found!"); return false; }

        Member member = getMenberByID(memberID);
        if (member == null){ System.out.println("Member " + memberID + " not found!"); return false; }

        if (book.isAvailabilityStatus()) {
            boolean accept = Borrowing.issueBook(memberID, bookID, borrowings, book, member);
            if (accept){
                System.out.println("Book " + bookID + " successfully lent to " + memberID);
                return true;
            } else {
                return false;
            }
        } else {
            waitingList.addToWaitingList(bookID, memberID);
            System.out.println("Book " + bookID + " is unavailable. " + memberID + " added to waiting list.");
            return false;
        }
    }

    public boolean returnBook(String memberID, int bookID) {
        Book book = getBooks().searchByID(bookID);
        if (book == null) { System.out.println("Book not found!"); return false; }

        Member member = getMenberByID(memberID);
        if (member == null){ System.out.println("Member not found!"); return false; }

        long lateDays = Borrowing.returnBook(bookID, memberID, borrowings, book, member);
        if (lateDays == -1) return false;

        if (lateDays > 0){
            double baseFine = lateDays * fineperDay;
            addFine(memberID, baseFine);
        }

        String nextMember = waitingList.removeFromWaitingList(bookID);
        if (nextMember != null){
            System.out.println("auto-assign book " + bookID + " to next member: " + nextMember);
            lendBook(nextMember, bookID);
        }
        return true;
    }

    public boolean renewBook(String memberID, int bookID) {
        Book book = getBooks().searchByID(bookID);
        if (book == null) { System.out.println("Book not found!"); return false; }

        if (!waitingList.isEmpty(bookID)) {
            System.out.println("Cannot renew: other members waiting for book.");
            return false;
        }

        boolean accept = Borrowing.renewBook(bookID, memberID, borrowings);
        if(!accept) System.out.println("Renew failed!");
        return accept;
    }

    // Billing
    public void createBillingAccount(String member_id) {
        Billing bill = searchBillingRecord(member_id);
        if (bill == null){
            bill = new Billing(member_id);
            billings.add(bill);
        }
        System.out.println("Billing account created for member ID: " + member_id);
    }

    public Billing searchBillingRecord(String memberID) {
        for (Billing billing : billings) {
            if (billing.getMemberID().equals(memberID)) return billing;
        }
        return null;
    }

    public void addFine(String memberID, double fine) {
        Billing billing = searchBillingRecord(memberID);
        if (billing == null) {
            billing = new Billing(memberID);
            billings.add(billing);
        }

        double taxAmount = fine * taxRate;
        double totalAmount = fine + taxAmount;

        billing.calculateFine(totalAmount);

        System.out.println("Member: " + memberID);
        System.out.println("Base fine: " + fine);
        System.out.println("tax of 10%: " +  taxAmount);
        System.out.println("Total amount added to bill: " +  totalAmount);
    }

    // Reports (demo)
    public void generateReports() {
        reporter.addData("sold");
        reporter.addData("borrowed");
        reporter.addData("borrowed");
        System.out.println(reporter.generateBookReport());
    }

    private void heapSort(Book[] array) {
        // Build max-heap
        for (int i = array.length / 2 - 1; i >= 0; i--) {
            getBooks().heapify(array, array.length, i);
        }
        // Extract elements
        for (int i = array.length - 1; i > 0; i--) {
            books.swap(array, 0, i);
            books.heapify(array, i, 0);
        }
    }

    public void displayMostBorrowedBooks(int topNumber) {
        if (getBooks().isEmpty()) {
            System.out.println("No books now available in the library");
            return;
        }
        Book[] array = getBooks().toArray(new Book[0]);
        heapSort(array);

        // الآن array مرتب تصاعديًا، نخليه نزوليًا
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            books.swap(array, i, j);
        }

        System.out.println("Top " + topNumber + " Most Borrowed Books:");
        int limit = Math.min(topNumber, array.length);
        for (int i = 0; i < limit; i++) {
            Book book = array[i];
            System.out.println("Rank " + (i+1) + " | Title: " + book.getTitle() +
                    " | Author: " + book.getAuthor() + " | Borrow Count: " + book.getBorrowCount());
        }
    }

    public void displayMemberBorrow(String memberID) {
        boolean found = false;
        System.out.println("Borrow History for Member: " + memberID);
        for (Borrowing b : borrowings) {
            if (b.getMemberID().equals(memberID)) {
                found = true;
                System.out.println("Borrow#" + b.getBorrowID() +
                        " | BookID=" + b.getBookID() +
                        " | Borrow=" + b.getBorrowDate() +
                        " | Due=" + b.getDueDate() +
                        " | Returned=" + b.isReturnStatus());
            }
        }
        if (!found) System.out.println("No borrow records for " + memberID);
    }
}
