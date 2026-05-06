package com.hospital.controller;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import com.hospital.utils.IDGenerator;

import java.util.List;

public class DoctorController {
    private final DoctorDAO dao = new DoctorDAO();

    public List<Doctor> getAll(String q) throws Exception { return dao.getAll(q == null ? "" : q); }
    public List<Doctor> getActive() throws Exception { return dao.getActive(); }
    public int totalActive() throws Exception { return dao.countActive(); }
    public void save(Doctor d, boolean edit) throws Exception { if (!edit) d.setDoctorId(IDGenerator.generateDoctorID()); dao.save(d, edit); }
    public void deactivate(String id) throws Exception { dao.deactivate(id); }
}
