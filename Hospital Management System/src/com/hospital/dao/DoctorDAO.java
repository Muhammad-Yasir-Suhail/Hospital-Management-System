package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    public List<Doctor> getAll(String q) throws SQLException {
        String sql = "SELECT * FROM doctors WHERE full_name LIKE ? OR specialization LIKE ? ORDER BY full_name";
        List<Doctor> list = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%"); ps.setString(2, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public List<Doctor> getActive() throws SQLException {
        List<Doctor> list = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM doctors WHERE status='Active' ORDER BY full_name"); ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        return list;
    }
    public int countActive() throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM doctors WHERE status='Active'"); ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1);} }
    public void save(Doctor d, boolean edit) throws SQLException {
        if (edit) {
            String sql = "UPDATE doctors SET full_name=?,specialization=?,contact_number=?,email=?,available_days=?,shift_start=?,shift_end=?,status=? WHERE doctor_id=?";
            try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, d.getFullName()); ps.setString(2, d.getSpecialization()); ps.setString(3, d.getContactNumber()); ps.setString(4, d.getEmail()); ps.setString(5, d.getAvailableDays()); ps.setTime(6, d.getShiftStart() == null ? null : Time.valueOf(d.getShiftStart())); ps.setTime(7, d.getShiftEnd() == null ? null : Time.valueOf(d.getShiftEnd())); ps.setString(8, d.getStatus()); ps.setString(9, d.getDoctorId()); ps.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO doctors(doctor_id,full_name,specialization,contact_number,email,available_days,shift_start,shift_end,status) VALUES(?,?,?,?,?,?,?,?,?)";
            try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, d.getDoctorId()); ps.setString(2, d.getFullName()); ps.setString(3, d.getSpecialization()); ps.setString(4, d.getContactNumber()); ps.setString(5, d.getEmail()); ps.setString(6, d.getAvailableDays()); ps.setTime(7, d.getShiftStart() == null ? null : Time.valueOf(d.getShiftStart())); ps.setTime(8, d.getShiftEnd() == null ? null : Time.valueOf(d.getShiftEnd())); ps.setString(9, d.getStatus()); ps.executeUpdate();
            }
        }
    }
    public void deactivate(String id) throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE doctors SET status='Inactive' WHERE doctor_id=?")) { ps.setString(1, id); ps.executeUpdate(); } }
    private Doctor map(ResultSet rs) throws SQLException { Doctor d = new Doctor(); d.setDoctorId(rs.getString("doctor_id")); d.setFullName(rs.getString("full_name")); d.setSpecialization(rs.getString("specialization")); d.setContactNumber(rs.getString("contact_number")); d.setEmail(rs.getString("email")); d.setAvailableDays(rs.getString("available_days")); Time s = rs.getTime("shift_start"); if (s != null) d.setShiftStart(s.toLocalTime()); Time e = rs.getTime("shift_end"); if (e != null) d.setShiftEnd(e.toLocalTime()); d.setStatus(rs.getString("status")); return d; }
}
