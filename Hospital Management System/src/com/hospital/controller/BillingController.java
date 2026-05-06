package com.hospital.controller;

import com.hospital.dao.AuditLogDAO;
import com.hospital.dao.BillingDAO;
import com.hospital.model.Bill;
import com.hospital.utils.IDGenerator;

import java.util.List;

public class BillingController {
    private final BillingDAO dao = new BillingDAO();
    private final AuditLogDAO audit = new AuditLogDAO();

    public String save(Bill bill, String actor) throws Exception {
        bill.setBillId(IDGenerator.generateBillID());
        dao.insert(bill);
        audit.log("billing", bill.getBillId(), actor, "Bill generated");
        return bill.getBillId();
    }
    public List<Bill> getBills(String status) throws Exception { return dao.getBills(status); }
    public Bill getById(String id) throws Exception { return dao.getById(id); }
    public void markPaid(String id, String actor) throws Exception { dao.markPaid(id); audit.log("billing", id, actor, "Bill marked as paid"); }
    public int unpaidCount() throws Exception { return dao.countUnpaid(); }
}
