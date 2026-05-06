package com.hospital.dao;

import com.hospital.model.User;
import com.hospital.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role, full_name FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("role"), rs.getString("full_name"));
                }
            }
        }
        return null;
    }
}
