package com.library.dao;

import com.library.model.Book;
import com.library.model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private BookDAO bookDAO = new BookDAO();

    public boolean issueBook(int bookId, int memberId, int loanDays) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null || book.getAvailableCopies() <= 0) {
            System.err.println("Book not available or does not exist.");
            return false;
        }

        if (isBookAlreadyIssuedToMember(bookId, memberId)) {
             System.err.println("This book is already issued to this member and not yet returned.");
             return false;
        }


        String sqlTransaction = "INSERT INTO transactions (book_id, member_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
        String sqlUpdateBook = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            try (PreparedStatement pstmtTransaction = conn.prepareStatement(sqlTransaction)) {
                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(loanDays);

                pstmtTransaction.setInt(1, bookId);
                pstmtTransaction.setInt(2, memberId);
                pstmtTransaction.setDate(3, Date.valueOf(issueDate));
                pstmtTransaction.setDate(4, Date.valueOf(dueDate));
                pstmtTransaction.executeUpdate();
            }

            try (PreparedStatement pstmtUpdateBook = conn.prepareStatement(sqlUpdateBook)) {
                pstmtUpdateBook.setInt(1, bookId);
                pstmtUpdateBook.executeUpdate();
            }

            conn.commit(); 
            System.out.println("Book ID " + bookId + " issued to Member ID " + memberId + " successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); 
                    System.err.println("Transaction rolled back.");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                } catch (SQLException e) {
                    System.err.println("Error restoring auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    public boolean isBookAlreadyIssuedToMember(int bookId, int memberId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE book_id = ? AND member_id = ? AND is_returned = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if book is already issued: " + e.getMessage());
        }
        return false; 
    }


    public boolean returnBook(int transactionId) { 
        Transaction transaction = getTransactionById(transactionId);
        if (transaction == null || transaction.isReturned()) {
            System.err.println("Transaction not found or book already returned.");
            return false;
        }

        String sqlTransaction = "UPDATE transactions SET return_date = ?, is_returned = TRUE WHERE transaction_id = ?";
        String sqlUpdateBook = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTransaction = conn.prepareStatement(sqlTransaction)) {
                pstmtTransaction.setDate(1, Date.valueOf(LocalDate.now()));
                pstmtTransaction.setInt(2, transactionId);
                pstmtTransaction.executeUpdate();
            }

            try (PreparedStatement pstmtUpdateBook = conn.prepareStatement(sqlUpdateBook)) {
                pstmtUpdateBook.setInt(1, transaction.getBookId());
                pstmtUpdateBook.executeUpdate();
            }

            conn.commit();
            System.out.println("Book for transaction ID " + transactionId + " returned successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error restoring auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        Transaction transaction = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transaction = mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transaction by ID: " + e.getMessage());
        }
        return transaction;
    }


    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY issue_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    public List<Transaction> getOverdueBooks() {
        List<Transaction> overdueTransactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE due_date < CURDATE() AND is_returned = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                overdueTransactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching overdue books: " + e.getMessage());
        }
        return overdueTransactions;
    }


    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setBookId(rs.getInt("book_id"));
        transaction.setMemberId(rs.getInt("member_id"));
        transaction.setIssueDate(rs.getDate("issue_date"));
        transaction.setDueDate(rs.getDate("due_date"));
        transaction.setReturnDate(rs.getDate("return_date")); 
        transaction.setReturned(rs.getBoolean("is_returned"));
        return transaction;
    }
}