package com.hospital.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int logId;
    private String tableName;
    private String recordId;
    private String changedBy;
    private String changeDescription;
    private LocalDateTime changeTimestamp;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }
    public LocalDateTime getChangeTimestamp() { return changeTimestamp; }
    public void setChangeTimestamp(LocalDateTime changeTimestamp) { this.changeTimestamp = changeTimestamp; }
}
