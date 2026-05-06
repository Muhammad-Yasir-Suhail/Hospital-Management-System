package com.hospital.controller;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.AuditLogDAO;
import com.hospital.model.Appointment;
import com.hospital.utils.IDGenerator;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class AppointmentController {
    private final AppointmentDAO dao = new AppointmentDAO();
    private final AuditLogDAO audit = new AuditLogDAO();

    public String book(Appointment a, String actor) throws Exception {
        if (dao.hasConflict(a.getDoctorId(), Date.valueOf(a.getAppointmentDate()), Time.valueOf(a.getAppointmentTime()), null)) throw new IllegalArgumentException("Time slot unavailable. Please choose another.");
        a.setAppointmentId(IDGenerator.generateAppointmentID());
        dao.insert(a);
        audit.log("appointments", a.getAppointmentId(), actor, "Appointment booked");
        return a.getAppointmentId();
    }
    public List<Appointment> schedule(LocalDate date, String doctorId) throws Exception { return dao.search(date, doctorId); }
    public Appointment getById(String id) throws Exception { return dao.getById(id); }
    public void reschedule(String id, LocalDate date, Time time, String actor) throws Exception {
        Appointment existing = dao.getById(id);
        if (existing == null) throw new IllegalArgumentException("Appointment not found.");
        if (dao.hasConflict(existing.getDoctorId(), Date.valueOf(date), time, id)) throw new IllegalArgumentException("Slot not available");
        dao.reschedule(id, Date.valueOf(date), time);
        audit.log("appointments", id, actor, "Appointment rescheduled");
    }
    public void cancel(String id, String reason, String actor) throws Exception { dao.cancel(id, reason); audit.log("appointments", id, actor, "Appointment cancelled: " + reason); }
    public void complete(String id, String actor) throws Exception { dao.markComplete(id); audit.log("appointments", id, actor, "Appointment marked completed"); }
    public int todaysAppointments() throws Exception { return dao.countToday(); }
}
