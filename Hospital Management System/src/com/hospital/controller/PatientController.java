package com.hospital.controller;

import com.hospital.dao.AuditLogDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.utils.IDGenerator;
import com.hospital.utils.Validator;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientController {
    private final PatientDAO dao = new PatientDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public String register(Patient patient, String actor) throws Exception {
        if (!Validator.isNotEmpty(patient.getFullName())) throw new IllegalArgumentException("Full Name is required.");
        if (!Validator.isValidPhone(patient.getContactNumber())) throw new IllegalArgumentException(Validator.errorMessage);
        if (dao.existsDuplicate(patient.getFullName(), Date.valueOf(patient.getDateOfBirth()), patient.getContactNumber())) throw new IllegalArgumentException("Duplicate patient detected.");
        patient.setPatientId(IDGenerator.generatePatientID());
        dao.insert(patient);
        auditLogDAO.log("patients", patient.getPatientId(), actor, "Patient registered");
        return patient.getPatientId();
    }

    public void update(Patient patient, String actor) throws Exception {
        dao.update(patient);
        auditLogDAO.log("patients", patient.getPatientId(), actor, "Patient updated");
    }

    public List<Patient> search(String mode, String query) throws Exception { return dao.search(mode, query); }
    public Patient getById(String id) throws Exception { return dao.getById(id); }
    public int totalPatients() throws Exception { return dao.countAll(); }
    public LocalDate parseDate(String s) { return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
}
