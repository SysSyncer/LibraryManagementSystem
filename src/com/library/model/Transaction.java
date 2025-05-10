package com.library.model;

import java.sql.Date; 

public class Transaction {
    private int transactionId;
    private int bookId;
    private int memberId;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate; 
    private boolean isReturned;

    public Transaction() {}

    public Transaction(int bookId, int memberId, Date issueDate, Date dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isReturned = false;
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean isReturned) { this.isReturned = isReturned; }

    @Override
    public String toString() {
        return "Transaction [ID=" + transactionId + ", BookID=" + bookId + ", MemberID=" + memberId
                + ", IssueDate=" + issueDate + ", DueDate=" + dueDate + ", Returned=" + (returnDate != null ? returnDate : "Not Yet")
                + ", Status=" + (isReturned ? "Returned" : "Issued") + "]";
    }
}