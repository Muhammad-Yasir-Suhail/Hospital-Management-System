package com.hospital.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IDGenerator {
    public static String generatePatientID() throws SQLException {
        return generate("SELECT MAX(patient_id) FROM patients", "PAT-");
    }

    public static String generateAppointmentID() throws SQLException {
        return generate("SELECT MAX(appointment_id) FROM appointments", "APT-");
    }

    public static String generateDoctorID() throws SQLException {
        return generate("SELECT MAX(doctor_id) FROM doctors", "DOC-");
    }

    public static String generateBillID() throws SQLException {
        return generate("SELECT MAX(bill_id) FROM billing", "BIL-");
    }

    private static String generate(String sql, String prefix) throws SQLException {
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next() && rs.getString(1) != null) {
                String[] parts = rs.getString(1).split("-");
                next = Integer.parseInt(parts[1]) + 1;
            }
            return prefix + String.format("%04d", next);
        }
    }
}
