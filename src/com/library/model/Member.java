package com.library.model;

public class Member {
    private int memberId;
    private String name;
    private String email;
    private String phoneNumber;

    public Member() {}

    public Member(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() {
        return "Member [ID=" + memberId + ", Name=" + name + ", Email=" + email + ", Phone=" + phoneNumber + "]";
    }
}