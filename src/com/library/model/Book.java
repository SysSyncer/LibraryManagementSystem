package com.library.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int totalCopies;
    private int availableCopies;
    private int publishedYear;

    public Book() {}

    public Book(String title, String author, String isbn, String genre, int totalCopies, int availableCopies, int publishedYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.publishedYear = publishedYear;
    }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public int getPublishedYear() { return publishedYear; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = publishedYear; }

    @Override
    public String toString() {
        return "Book [ID=" + bookId + ", Title=" + title + ", Author=" + author + ", ISBN=" + isbn
                + ", Genre=" + genre + ", Total=" + totalCopies + ", Available=" + availableCopies
                + ", Year=" + publishedYear + "]";
    }
}