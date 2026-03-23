
import java.time.LocalDate;
import java.util.ArrayList;

public class Borrowing {
    private int borrowID;
    private String memberID;
    private int bookID;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returnStatus;

    public Borrowing(int borrowID, String memberID, int bookID) {
        this.borrowID = borrowID;
        this.memberID = memberID;
        this.bookID = bookID;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14);
        this.returnStatus = false;
    }

    static boolean issueBook(String memberID, int bookID,
                             ArrayList<Borrowing> records, Book book, Member member){
        if (book.isAvailabilityStatus()){
            Borrowing borrowing = new Borrowing(records.size() + 1, memberID, bookID);
            records.add(borrowing);
            book.setAvailabilityStatus(false);
            book.incrementBorrowCount();
            if (member != null) member.addBorrowedBook(bookID);
            System.out.println("book " + bookID + " given to " + memberID);
            return true;
        } else {
            System.out.println("book not available");
            return false;
        }
    }

    static long returnBook(int bookID, String memberID,
                           ArrayList<Borrowing> records, Book book, Member member){
        for (Borrowing borrowing : records){
            if (borrowing.bookID == bookID && borrowing.memberID.equals(memberID) && !borrowing.returnStatus){
                borrowing.returnStatus = true;
                book.setAvailabilityStatus(true);
                if (member != null) member.removeBorrowedBook(bookID);

                LocalDate today = LocalDate.now();
                long lateDays = Math.max(0, today.toEpochDay() - borrowing.dueDate.toEpochDay());
                System.out.println("Book " + bookID + " returned by " + memberID + " LateDays: " + lateDays);
                return lateDays;
            }
        }
        System.out.println("no record found for book " + bookID + ", Member " + memberID);
        return -1;
    }

    public static boolean renewBook(int bookID, String memberID, ArrayList<Borrowing> borrowings) {
        for (Borrowing borrowing : borrowings) {
            if (borrowing.bookID == bookID && borrowing.memberID.equals(memberID) && !borrowing.returnStatus){
                borrowing.dueDate = borrowing.dueDate.plusDays(14);
                System.out.println("Book " + bookID + " renewed for " + memberID + " new due date: " + borrowing.dueDate);
                return true;
            }
        }
        System.out.println("renew failed for book: " + bookID + " and member -> " + memberID);
        return false;
    }

    public int getBorrowID() { return borrowID; }
    public String getMemberID() { return memberID; }
    public int getBookID() { return bookID; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturnStatus() { return returnStatus; }
}
