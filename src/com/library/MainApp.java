package com.library;

import com.library.dao.BookDAO;
import com.library.dao.DatabaseConnection;
import com.library.dao.MemberDAO;
import com.library.dao.TransactionDAO;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.util.InputUtil;

import java.util.List;

public class MainApp {

    private static BookDAO bookDAO = new BookDAO();
    private static MemberDAO memberDAO = new MemberDAO();
    private static TransactionDAO transactionDAO = new TransactionDAO();

    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
            System.out.println("Successfully connected to the database!");
        } catch (Exception e) {
            System.err.println("Failed to connect to database. Exiting application.");
            return; 
        }


        while (true) {
            showMenu();
            int choice = InputUtil.getIntInput("Enter your choice: ");

            switch (choice) {
                case 1: addBook(); break;
                case 2: viewAllBooks(); break;
                case 3: searchBook(); break;
                case 4: updateBook(); break;
                case 5: deleteBook(); break;
                case 6: addMember(); break;
                case 7: viewAllMembers(); break;
                case 8: updateMember(); break;
                case 9: deleteMember(); break;
                case 10: issueBook(); break;
                case 11: returnBook(); break;
                case 12: viewAllTransactions(); break;
                case 13: viewOverdueBooks(); break;
                case 0:
                    System.out.println("Exiting Library Management System. Goodbye!");
                    DatabaseConnection.closeConnection(); 
                    InputUtil.closeScanner(); 
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("------------------------------------");
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Library Management System ---");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Book (by ID or ISBN)");
        System.out.println("4. Update Book");
        System.out.println("5. Delete Book");
        System.out.println("--- Member Operations ---");
        System.out.println("6. Add Member");
        System.out.println("7. View All Members");
        System.out.println("8. Update Member");
        System.out.println("9. Delete Member");
        System.out.println("--- Transaction Operations ---");
        System.out.println("10. Issue Book");
        System.out.println("11. Return Book");
        System.out.println("12. View All Transactions");
        System.out.println("13. View Overdue Books");
        System.out.println("0. Exit");
    }

    private static void addBook() {
        System.out.println("\n--- Add New Book ---");
        String title = InputUtil.getStringInput("Enter title: ");
        String author = InputUtil.getStringInput("Enter author: ");
        String isbn = InputUtil.getStringInput("Enter ISBN: ");
        String genre = InputUtil.getStringInput("Enter genre: ");
        int totalCopies = InputUtil.getIntInput("Enter total copies: ");
        int publishedYear = InputUtil.getIntInput("Enter published year (YYYY): ");

        Book book = new Book(title, author, isbn, genre, totalCopies, totalCopies, publishedYear); 
        bookDAO.addBook(book);
    }

    private static void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books found in the library.");
        } else {
            books.forEach(System.out::println);
        }
    }

    private static void searchBook() {
        System.out.println("\n--- Search Book ---");
        String searchType = InputUtil.getStringInput("Search by 'ID' or 'ISBN': ").toUpperCase();
        Book book = null;
        if ("ID".equals(searchType)) {
            int bookId = InputUtil.getIntInput("Enter Book ID: ");
            book = bookDAO.getBookById(bookId);
        } else if ("ISBN".equals(searchType)) {
            String isbn = InputUtil.getStringInput("Enter Book ISBN: ");
            book = bookDAO.getBookByIsbn(isbn);
        } else {
            System.out.println("Invalid search type.");
            return;
        }

        if (book != null) {
            System.out.println("Book found: " + book);
        } else {
            System.out.println("Book not found.");
        }
    }
    
    private static void updateBook() {
        System.out.println("\n--- Update Book ---");
        int bookId = InputUtil.getIntInput("Enter ID of the book to update: ");
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.println("Current details: " + book);
        System.out.println("Enter new details (leave blank to keep current value):");

        String title = InputUtil.getStringInput("New title ("+book.getTitle()+"): ");
        if (!title.isEmpty()) book.setTitle(title);

        String author = InputUtil.getStringInput("New author ("+book.getAuthor()+"): ");
        if (!author.isEmpty()) book.setAuthor(author);
        
        String isbn = InputUtil.getStringInput("New ISBN ("+book.getIsbn()+"): ");
        if (!isbn.isEmpty()) book.setIsbn(isbn);

        String genre = InputUtil.getStringInput("New genre ("+book.getGenre()+"): ");
        if (!genre.isEmpty()) book.setGenre(genre);

        String totalCopiesStr = InputUtil.getStringInput("New total copies ("+book.getTotalCopies()+"): ");
        if (!totalCopiesStr.isEmpty()) {
            try {
                int newTotal = Integer.parseInt(totalCopiesStr);
                if (newTotal < book.getAvailableCopies()) { 
                    System.err.println("Warning: New total copies cannot be less than currently available copies ("+book.getAvailableCopies()+"). Adjusting available copies.");
                    book.setAvailableCopies(newTotal); 
                }
                book.setTotalCopies(newTotal);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number for total copies. Keeping current.");
            }
        }
        
        String availableCopiesStr = InputUtil.getStringInput("New available copies ("+book.getAvailableCopies()+"): ");
         if (!availableCopiesStr.isEmpty()) {
            try {
                int newAvailable = Integer.parseInt(availableCopiesStr);
                if (newAvailable > book.getTotalCopies()) {
                     System.err.println("Available copies cannot exceed total copies ("+book.getTotalCopies()+"). Setting to total copies.");
                     book.setAvailableCopies(book.getTotalCopies());
                } else {
                    book.setAvailableCopies(newAvailable);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid number for available copies. Keeping current.");
            }
        }


        String publishedYearStr = InputUtil.getStringInput("New published year ("+book.getPublishedYear()+"): ");
        if (!publishedYearStr.isEmpty()){
             try {
                book.setPublishedYear(Integer.parseInt(publishedYearStr));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number for published year. Keeping current.");
            }
        }

        if (bookDAO.updateBook(book)) {
            System.out.println("Book updated successfully.");
        } else {
            System.out.println("Failed to update book.");
        }
    }

    private static void deleteBook() {
        int bookId = InputUtil.getIntInput("Enter ID of the book to delete: ");
        if (bookDAO.deleteBook(bookId)) {
			System.out.println("Book deleted successfully.");
		} else {
			System.out.println("Failed to delete book. It may have active transactions.");
        }
    }

    private static void addMember() {
        System.out.println("\n--- Add New Member ---");
        String name = InputUtil.getStringInput("Enter member name: ");
        String email = InputUtil.getStringInput("Enter member email: ");
        String phone = InputUtil.getStringInput("Enter member phone: ");
        Member member = new Member(name, email, phone);
        memberDAO.addMember(member);
    }

    private static void viewAllMembers() {
        System.out.println("\n--- All Members ---");
        List<Member> members = memberDAO.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            members.forEach(System.out::println);
        }
    }
    
    private static void updateMember() {
        System.out.println("\n--- Update Member ---");
        int memberId = InputUtil.getIntInput("Enter ID of the member to update: ");
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }
        System.out.println("Current details: " + member);
        System.out.println("Enter new details (leave blank to keep current value):");

        String name = InputUtil.getStringInput("New name ("+member.getName()+"): ");
        if (!name.isEmpty()) member.setName(name);

        String email = InputUtil.getStringInput("New email ("+member.getEmail()+"): ");
        if (!email.isEmpty()) member.setEmail(email);

        String phone = InputUtil.getStringInput("New phone ("+member.getPhoneNumber()+"): ");
        if (!phone.isEmpty()) member.setPhoneNumber(phone);
        
        if (memberDAO.updateMember(member)) {
            System.out.println("Member updated successfully.");
        } else {
            System.out.println("Failed to update member.");
        }
    }

    private static void deleteMember() {
        System.out.println("\n--- Delete Member ---");
        int memberId = InputUtil.getIntInput("Enter ID of the member to delete: ");
        memberDAO.deleteMember(memberId); 
    }

    private static void issueBook() {
        System.out.println("\n--- Issue Book ---");
        int bookId = InputUtil.getIntInput("Enter Book ID to issue: ");
        int memberId = InputUtil.getIntInput("Enter Member ID: ");
        int loanDays = InputUtil.getIntInput("Enter loan period in days (e.g., 14): ");
        
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            System.out.println("Book with ID " + bookId + " not found.");
            return;
        }
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            System.out.println("Member with ID " + memberId + " not found.");
            return;
        }

        transactionDAO.issueBook(bookId, memberId, loanDays);
    }

    private static void returnBook() {
        System.out.println("\n--- Return Book ---");
        System.out.println("Active transactions (not yet returned):");
        List<Transaction> activeTransactions = transactionDAO.getAllTransactions().stream()
                                                .filter(t -> !t.isReturned()).toList();
        if (activeTransactions.isEmpty()) {
            System.out.println("No active transactions to return.");
            return;
        }
        activeTransactions.forEach(System.out::println);
        
        int transactionId = InputUtil.getIntInput("Enter Transaction ID to mark as returned: ");
        transactionDAO.returnBook(transactionId);
    }

    private static void viewAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            transactions.forEach(System.out::println);
        }
    }
    
    private static void viewOverdueBooks() {
        System.out.println("\n--- Overdue Books (Not Returned) ---");
        List<Transaction> overdue = transactionDAO.getOverdueBooks();
        if (overdue.isEmpty()) {
            System.out.println("No overdue books currently.");
        } else {
            overdue.forEach(t -> {
                Book book = bookDAO.getBookById(t.getBookId());
                Member member = memberDAO.getMemberById(t.getMemberId());
                System.out.println(t + " | Book: " + (book!=null?book.getTitle():"N/A") + " | Member: " + (member!=null?member.getName():"N/A"));
            });
        }
    }
}