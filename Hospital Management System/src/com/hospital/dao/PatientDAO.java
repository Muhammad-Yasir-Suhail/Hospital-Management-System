package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    public boolean existsDuplicate(String name, Date dob, String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM patients WHERE full_name=? AND date_of_birth=? AND contact_number=?";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name); ps.setDate(2, dob); ps.setString(3, phone);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    public void insert(Patient p) throws SQLException {
        String sql = "INSERT INTO patients(patient_id,full_name,date_of_birth,gender,contact_number,address,emergency_contact_name,emergency_contact_phone,blood_group,allergies) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getPatientId()); ps.setString(2, p.getFullName()); ps.setDate(3, Date.valueOf(p.getDateOfBirth())); ps.setString(4, p.getGender()); ps.setString(5, p.getContactNumber()); ps.setString(6, p.getAddress()); ps.setString(7, p.getEmergencyContactName()); ps.setString(8, p.getEmergencyContactPhone()); ps.setString(9, p.getBloodGroup()); ps.setString(10, p.getAllergies()); ps.executeUpdate();
        }
    }

    public List<Patient> search(String mode, String q) throws SQLException {
        String sql = switch (mode) { case "By ID" -> "SELECT * FROM patients WHERE patient_id LIKE ?"; case "By Phone" -> "SELECT * FROM patients WHERE contact_number LIKE ?"; default -> "SELECT * FROM patients WHERE full_name LIKE ?"; };
        List<Patient> out = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public Patient getById(String id) throws SQLException {
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM patients WHERE patient_id=?")) {
            ps.setString(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void update(Patient p) throws SQLException {
        String sql = "UPDATE patients SET full_name=?,date_of_birth=?,gender=?,contact_number=?,address=?,emergency_contact_name=?,emergency_contact_phone=?,blood_group=?,allergies=?,last_updated=GETDATE() WHERE patient_id=?";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getFullName()); ps.setDate(2, Date.valueOf(p.getDateOfBirth())); ps.setString(3, p.getGender()); ps.setString(4, p.getContactNumber()); ps.setString(5, p.getAddress()); ps.setString(6, p.getEmergencyContactName()); ps.setString(7, p.getEmergencyContactPhone()); ps.setString(8, p.getBloodGroup()); ps.setString(9, p.getAllergies()); ps.setString(10, p.getPatientId()); ps.executeUpdate();
        }
    }

    public int countAll() throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM patients"); ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1);} }

    private Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getString("patient_id")); p.setFullName(rs.getString("full_name")); p.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate()); p.setGender(rs.getString("gender")); p.setContactNumber(rs.getString("contact_number")); p.setAddress(rs.getString("address")); p.setEmergencyContactName(rs.getString("emergency_contact_name")); p.setEmergencyContactPhone(rs.getString("emergency_contact_phone")); p.setBloodGroup(rs.getString("blood_group")); p.setAllergies(rs.getString("allergies")); Timestamp reg = rs.getTimestamp("registration_date"); if (reg != null) p.setRegistrationDate(reg.toLocalDateTime()); Timestamp upd = rs.getTimestamp("last_updated"); if (upd != null) p.setLastUpdated(upd.toLocalDateTime());
        return p;
    }
}
