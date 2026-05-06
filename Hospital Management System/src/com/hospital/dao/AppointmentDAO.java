package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public boolean hasConflict(String doctorId, Date date, Time time, String excludeAppointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=? AND CAST(appointment_time AS VARCHAR(8))=CAST(? AS VARCHAR(8)) AND status!='Cancelled'" + (excludeAppointmentId != null ? " AND appointment_id<>?" : "");
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, doctorId); ps.setDate(2, date); ps.setTime(3, time);
            if (excludeAppointmentId != null) ps.setString(4, excludeAppointmentId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }
    public void insert(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments(appointment_id,patient_id,doctor_id,appointment_date,appointment_time,reason,status) VALUES(?,?,?,?,?,?,'Scheduled')";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getAppointmentId()); ps.setString(2, a.getPatientId()); ps.setString(3, a.getDoctorId()); ps.setDate(4, Date.valueOf(a.getAppointmentDate())); ps.setTime(5, Time.valueOf(a.getAppointmentTime())); ps.setString(6, a.getReason()); ps.executeUpdate();
        }
    }
    public List<Appointment> search(LocalDate date, String doctorId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT a.*,p.full_name AS patient_name,d.full_name AS doctor_name FROM appointments a JOIN patients p ON p.patient_id=a.patient_id JOIN doctors d ON d.doctor_id=a.doctor_id WHERE 1=1");
        if (date != null) sql.append(" AND a.appointment_date=?");
        if (doctorId != null && !doctorId.isBlank()) sql.append(" AND a.doctor_id=?");
        sql.append(" ORDER BY a.appointment_date,a.appointment_time");
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1; if (date != null) ps.setDate(i++, Date.valueOf(date)); if (doctorId != null && !doctorId.isBlank()) ps.setString(i, doctorId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public Appointment getById(String appointmentId) throws SQLException {
        String sql = "SELECT a.*,p.full_name AS patient_name,d.full_name AS doctor_name FROM appointments a JOIN patients p ON p.patient_id=a.patient_id JOIN doctors d ON d.doctor_id=a.doctor_id WHERE a.appointment_id=?";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }
    public int countToday() throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM appointments WHERE appointment_date=CAST(GETDATE() AS DATE)"); ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1);} }
    public void reschedule(String id, Date date, Time time) throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE appointments SET appointment_date=?,appointment_time=?,last_updated=GETDATE() WHERE appointment_id=?")) { ps.setDate(1, date); ps.setTime(2, time); ps.setString(3, id); ps.executeUpdate(); } }
    public void cancel(String id, String reason) throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE appointments SET status='Cancelled',cancellation_reason=?,last_updated=GETDATE() WHERE appointment_id=? AND status NOT IN ('Cancelled','Completed')")) { ps.setString(1, reason); ps.setString(2, id); ps.executeUpdate(); } }
    public void markComplete(String id) throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE appointments SET status='Completed',last_updated=GETDATE() WHERE appointment_id=? AND status='Scheduled'")) { ps.setString(1, id); ps.executeUpdate(); } }
    private Appointment map(ResultSet rs) throws SQLException { Appointment a = new Appointment(); a.setAppointmentId(rs.getString("appointment_id")); a.setPatientId(rs.getString("patient_id")); a.setDoctorId(rs.getString("doctor_id")); a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate()); a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime()); a.setReason(rs.getString("reason")); a.setStatus(rs.getString("status")); a.setCancellationReason(rs.getString("cancellation_reason")); a.setPatientName(rs.getString("patient_name")); a.setDoctorName(rs.getString("doctor_name")); return a; }
}
