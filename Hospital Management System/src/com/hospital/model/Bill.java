package com.hospital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {
    private String billId;
    private String patientId;
    private String appointmentId;
    private BigDecimal consultationFee;
    private BigDecimal medicationCharges;
    private BigDecimal testCharges;
    private BigDecimal otherCharges;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime billDate;
    private String patientName;
    private String contactNumber;

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public BigDecimal getMedicationCharges() { return medicationCharges; }
    public void setMedicationCharges(BigDecimal medicationCharges) { this.medicationCharges = medicationCharges; }
    public BigDecimal getTestCharges() { return testCharges; }
    public void setTestCharges(BigDecimal testCharges) { this.testCharges = testCharges; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getBillDate() { return billDate; }
    public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}
