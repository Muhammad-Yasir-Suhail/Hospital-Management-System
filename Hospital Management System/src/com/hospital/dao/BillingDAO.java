package com.hospital.dao;

import com.hospital.model.Bill;
import com.hospital.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {
    public void insert(Bill b) throws SQLException {
        String sql = "INSERT INTO billing(bill_id,patient_id,appointment_id,consultation_fee,medication_charges,test_charges,other_charges,total_amount,payment_status,payment_method) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getBillId()); ps.setString(2, b.getPatientId()); ps.setString(3, b.getAppointmentId()); ps.setBigDecimal(4, b.getConsultationFee()); ps.setBigDecimal(5, b.getMedicationCharges()); ps.setBigDecimal(6, b.getTestCharges()); ps.setBigDecimal(7, b.getOtherCharges()); ps.setBigDecimal(8, b.getTotalAmount()); ps.setString(9, b.getPaymentStatus()); ps.setString(10, b.getPaymentMethod()); ps.executeUpdate();
        }
    }
    public List<Bill> getBills(String status) throws SQLException {
        String sql = "SELECT b.*,p.full_name,p.contact_number FROM billing b JOIN patients p ON p.patient_id=b.patient_id WHERE (?='All' OR b.payment_status=?) ORDER BY bill_date DESC";
        List<Bill> list = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public int countUnpaid() throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM billing WHERE payment_status='Unpaid'"); ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1);} }
    public Bill getById(String id) throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("SELECT b.*,p.full_name,p.contact_number FROM billing b JOIN patients p ON p.patient_id=b.patient_id WHERE bill_id=?")) { ps.setString(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; } } }
    public void markPaid(String id) throws SQLException { try (Connection c = DBConnection.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE billing SET payment_status='Paid' WHERE bill_id=?")) { ps.setString(1, id); ps.executeUpdate(); } }
    private Bill map(ResultSet rs) throws SQLException { Bill b = new Bill(); b.setBillId(rs.getString("bill_id")); b.setPatientId(rs.getString("patient_id")); b.setAppointmentId(rs.getString("appointment_id")); b.setConsultationFee(rs.getBigDecimal("consultation_fee")); b.setMedicationCharges(rs.getBigDecimal("medication_charges")); b.setTestCharges(rs.getBigDecimal("test_charges")); b.setOtherCharges(rs.getBigDecimal("other_charges")); b.setTotalAmount(rs.getBigDecimal("total_amount")); b.setPaymentStatus(rs.getString("payment_status")); b.setPaymentMethod(rs.getString("payment_method")); Timestamp t = rs.getTimestamp("bill_date"); if (t != null) b.setBillDate(t.toLocalDateTime()); b.setPatientName(rs.getString("full_name")); b.setContactNumber(rs.getString("contact_number")); return b; }
}
