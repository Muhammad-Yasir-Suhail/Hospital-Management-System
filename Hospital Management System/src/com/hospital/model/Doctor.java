package com.hospital.model;

import java.time.LocalTime;

public class Doctor {
    private String doctorId;
    private String fullName;
    private String specialization;
    private String contactNumber;
    private String email;
    private String availableDays;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private String status;

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }
    public LocalTime getShiftStart() { return shiftStart; }
    public void setShiftStart(LocalTime shiftStart) { this.shiftStart = shiftStart; }
    public LocalTime getShiftEnd() { return shiftEnd; }
    public void setShiftEnd(LocalTime shiftEnd) { this.shiftEnd = shiftEnd; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return doctorId + " - " + fullName;
    }
}
