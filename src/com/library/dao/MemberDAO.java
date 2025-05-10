package com.library.dao;

import com.library.model.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    public void addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone_number) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhoneNumber());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setMemberId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Member added successfully: " + member.getName());
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
    }

    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        Member member = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    member = mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching member by ID: " + e.getMessage());
        }
        return member;
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all members: " + e.getMessage());
        }
        return members;
    }

    public boolean updateMember(Member member) {
         String sql = "UPDATE members SET name=?, email=?, phone_number=? WHERE member_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhoneNumber());
            pstmt.setInt(4, member.getMemberId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMember(int memberId) {
        // Check for active transactions associated with the member
        String checkTransactionsSql = "SELECT COUNT(*) FROM transactions WHERE member_id = ? AND is_returned = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkTransactionsSql)) {
            checkStmt.setInt(1, memberId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Cannot delete member: Member has active (unreturned) book transactions.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking transactions for member: " + e.getMessage());
            return false;
        }

        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                 System.out.println("Member with ID " + memberId + " deleted successfully.");
            } else {
                 System.out.println("No member found with ID " + memberId + " to delete.");
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setMemberId(rs.getInt("member_id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhoneNumber(rs.getString("phone_number"));
        return member;
    }
}