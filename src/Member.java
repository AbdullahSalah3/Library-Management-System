
import java.util.ArrayList;
import java.util.Date;

public class Member {
    private String memberID;
    String name;
    public String contactInfo;
    Date membershipDate;
    private ArrayList<Integer> borrowedBooks = new ArrayList<>();

    public Member(String memberID, String name, String contactInfo) {
        this.memberID = memberID;
        this.name = name;
        this.contactInfo = contactInfo;
        this.membershipDate = new Date();
    }

    public String getMemberID() { return memberID; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }

    public void registerMember(){ System.out.println("member registered: " + name); }

    public void updateContactInfo(String newContact) {
        this.contactInfo = newContact;
        System.out.println("Contact updated for " + name);
    }

    public void viewBorrowedBooks() {
        System.out.println("Borrowed books by: " + name + " -> " + borrowedBooks);
    }

    public void addBorrowedBook(int bookID){ borrowedBooks.add(bookID); }
    public void removeBorrowedBook(int bookID){ borrowedBooks.remove(Integer.valueOf(bookID)); }
    public ArrayList<Integer> getBorrowedBooks(){ return borrowedBooks; }

    @Override
    public String toString() {
        return "Member ID: " + memberID + ", Name: " + name + ", Contact: " + contactInfo +
                ", Membership Date: " + membershipDate + ", Borrowed Books: " + borrowedBooks;
    }
}
