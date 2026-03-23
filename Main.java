
public class Main {
    public static void main(String[] args) {
        LibraryManagementSystem lms = new LibraryManagementSystem();

        System.out.println("Adding Books:- ");
        Book b1 = new Book(103, "physics", "Robert Martin", "science", 2008);
        Book b2 = new Book(101, "Java Programming", "Herbert Schildt", "Education", 2019);
        Book b3 = new Book(105, "Data Structures", "Thomas Cormen", "Algorithms", 2009);
        Book b4 = new Book(102, "software masters", "Andrew Hunt", "Software", 1999);
        lms.addBook(b1); lms.addBook(b2); lms.addBook(b3); lms.addBook(b4);
        lms.displayAllBooks();

        System.out.println("Registering Members");
        Member m1 = new Member("M1", "Ahmed Ali", "ahmed@alamein.edu");
        Member m2 = new Member("M2", "Mohamed", "mohamed@yahoo.com");
        Member m3 = new Member("M3", "Khaled Hassan", "khaled@gmail.com");
        lms.registerMember(m1); lms.registerMember(m2); lms.registerMember(m3);

        System.out.println("the queue:-");
        lms.lendBook("M1", 103);
        lms.lendBook("M2", 103);
        lms.lendBook("M3", 103);

        System.out.println("Returning Book + Auto-Assign to Next Member");
        lms.returnBook("M1", 103);
        lms.lendBook("M3", 103);

        System.out.println("Renewing a Book");
        lms.renewBook("M2", 103);

        System.out.println("Updating Member Contact Info");
        m1.updateContactInfo("ahmed.new@alamein.edu");
        System.out.println("Viewing Borrowed Books");
        m2.viewBorrowedBooks();

        System.out.println("Generating Reports");
        lms.generateReports();

        System.out.println("searchBook by ID");
        Book found = lms.searchBookByID(103);
        System.out.println(found != null ? "Found: " + found.getBookDetails() : "Book not found!");

        System.out.println("Updating Book Information");
        lms.updateBookInfo(102, "skillful Programmer", "David Thomas", "Best Practices", 2019);
        lms.displayAllBooks();
        lms.generateReports();

        System.out.println("searchBook by title");
        Book foundTitle = lms.searchBookByTitle("physics");
        System.out.println(foundTitle != null ? "Found: " + foundTitle.getBookDetails() : "Book not found!");

        System.out.println("searchBook by author");
        Book foundAuthor = lms.searchBookByAuthor("Robert Martin");
        System.out.println(foundAuthor != null ? "Found: " + foundAuthor.getBookDetails() : "Book not found!");

        lms.createBillingAccount("M1");
        lms.createBillingAccount("M2");

        Billing billing = lms.searchBillingRecord("M1");
        if (billing != null) {
            System.out.println("Found billing record for member: " + billing.getMemberID());
            System.out.println("Current total fines: " + billing.getTotalFines());
        } else {
            System.out.println("Billing record not found!");
        }

        lms.addFine("M1", 25.0);
        lms.addFine("M2", 15.5);
        lms.addFine("M3", 10.0);

        lms.displayMostBorrowedBooks(3);

        System.out.println("Updating Member Contact Info");
        lms.updateMemberInfo("M1", "Mohamed Ali", "mohamedAli.new@alamein.edu");

        System.out.println("Borrow History for M1");
        lms.displayMemberBorrow("M1");

        lms.displayAllMembers();
    }
}
