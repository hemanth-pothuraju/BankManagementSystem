package com.bankms.dao;

import com.bankms.model.User;
import com.bankms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /** Inserts a new user. Returns false if the e-mail already exists. */
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password, phone, address) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                return false; // duplicate e-mail
            }
            throw e;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, full_name, email, password, phone, address FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    u.setPassword(rs.getString("password"));
                    u.setPhone(rs.getString("phone"));
                    u.setAddress(rs.getString("address"));
                    return u;
                }
            }
        }
        return null;
    }
}
