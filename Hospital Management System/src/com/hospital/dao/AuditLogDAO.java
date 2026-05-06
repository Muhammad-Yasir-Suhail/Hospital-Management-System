package com.hospital.dao;

import com.hospital.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogDAO {
    public void log(String table, String recordId, String by, String desc) throws SQLException {
        String sql = "INSERT INTO audit_log(table_name,record_id,changed_by,change_description,change_timestamp) VALUES(?,?,?,?,GETDATE())";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, table); ps.setString(2, recordId); ps.setString(3, by); ps.setString(4, desc); ps.executeUpdate();
        }
    }
}
