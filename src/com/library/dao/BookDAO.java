package com.library.dao;

import com.library.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, genre, total_copies, available_copies, published_year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setInt(7, book.getPublishedYear());
            pstmt.executeUpdate();

            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setBookId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Book added successfully: " + book.getTitle());
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        Book book = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book by ID: " + e.getMessage());
        }
        return book;
    }
    
    public Book getBookByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        Book book = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    book = mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book by ISBN: " + e.getMessage());
        }
        return book;
    }


    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all books: " + e.getMessage());
        }
        return books;
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, genre=?, total_copies=?, available_copies=?, published_year=? WHERE book_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setInt(7, book.getPublishedYear());
            pstmt.setInt(8, book.getBookId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String checkTransactionsSql = "SELECT COUNT(*) FROM transactions WHERE book_id = ? AND is_returned = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkTransactionsSql)) {
            checkStmt.setInt(1, bookId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Cannot delete book: It has active (unreturned) transactions.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking transactions for book: " + e.getMessage());
            return false;
        }


        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                 System.out.println("Book with ID " + bookId + " deleted successfully.");
            } else {
                 System.out.println("No book found with ID " + bookId + " to delete.");
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setGenre(rs.getString("genre"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setPublishedYear(rs.getInt("published_year"));
        return book;
    }
}