
import java.util.ArrayList;

public class Book {
    private int bookID;
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
    private boolean availabilityStatus = true;
    private int borrowCount = 0;

    public Book(int bookID, String title, String author, String genre, int publicationYear) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
    }

    // Setters
    public void setBookID(int bookID) { this.bookID = bookID; }
    public void setAuthor(String author) { this.author = author; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public void setAvailabilityStatus(boolean availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    // Counters
    public void incrementBorrowCount() { this.borrowCount++; }

    // Getters
    public int getBookID() { return bookID; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getPublicationYear() { return publicationYear; }
    public boolean isAvailabilityStatus() { return availabilityStatus; }
    public int getBorrowCount() { return borrowCount; }

    // Spec methods
    public void addBook() { System.out.println("Book added: " + title); }

    public void updateBookInfo(String newTitle, String newAuthor, String newGenre, int newYear) {
        this.title = newTitle;
        this.author = newAuthor;
        this.genre = newGenre;
        this.publicationYear = newYear;
        System.out.println("Book updated: " + title);
    }

    public String getBookDetails() {
        return "ID: " + bookID +
                "\nTitle: " + title +
                "\nAuthor: " + author +
                "\nGenre: " + genre +
                "\nYear: " + publicationYear +
                "\nAvailable: " + availabilityStatus;
    }

    // ====== Binary Search Tree for Books by ID ======
    static class BookBST {
        class Node {
            Book book;
            Node left, right;
            Node(Book book) { this.book = book; }
        }

        Node root;

        public boolean isEmpty() { return root == null; }

        public void insert(Book book) {
            root = insertRec(root, book);
        }

        private Node insertRec(Node root, Book book) {
            if (root == null) return new Node(book);
            if (book.getBookID() < root.book.getBookID()) root.left = insertRec(root.left, book);
            else if (book.getBookID() > root.book.getBookID()) root.right = insertRec(root.right, book);
            return root;
        }

        public Book searchByID(int id) { return searchRec(root, id); }
        private Book searchRec(Node root, int id) {
            if (root == null) return null;
            if (root.book.getBookID() == id) return root.book;
            if (id < root.book.getBookID()) return searchRec(root.left, id);
            return searchRec(root.right, id);
        }

        public Book searchByTitle(String title) { return searchRecTitle(root, title); }
        private Book searchRecTitle(Node root, String title) {
            if (root == null) return null;
            if (root.book.getTitle().equals(title)) return root.book;
            Book left = searchRecTitle(root.left, title);
            if (left != null) return left;
            return searchRecTitle(root.right, title);
        }

        public Book searchByAuthor(String author) { return searchRecAuthor(root, author); }
        private Book searchRecAuthor(Node root, String author) {
            if (root == null) return null;
            if (root.book.getAuthor().equals(author)) return root.book;
            Book left = searchRecAuthor(root.left, author);
            if (left != null) return left;
            return searchRecAuthor(root.right, author);
        }

        public void printInOrder() { printRec(root); }
        private void printRec(Node root) {
            if (root != null) {
                printRec(root.left);
                System.out.println(root.book.getBookDetails());
                printRec(root.right);
            }
        }

        // Heap helpers for popularity sorting (by borrowCount)
        public void heapify(Book[] arr, int n, int i) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < n && arr[left].getBorrowCount() > arr[largest].getBorrowCount())
                largest = left;
            if (right < n && arr[right].getBorrowCount() > arr[largest].getBorrowCount())
                largest = right;

            if (largest != i) {
                swap(arr, i, largest);
                heapify(arr, n, largest);
            }
        }

        public void swap(Book[] arr, int i, int j) {
            Book temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        // Convert BST inorder to array
        public Book[] toArray(Book[] books) {
            ArrayList<Book> list = new ArrayList<>();
            inorder(root, list);
            return list.toArray(new Book[0]);
        }

        private void inorder(Node node, ArrayList<Book> list) {
            if (node == null) return;
            inorder(node.left, list);
            list.add(node.book);
            inorder(node.right, list);
        }
    }
}
