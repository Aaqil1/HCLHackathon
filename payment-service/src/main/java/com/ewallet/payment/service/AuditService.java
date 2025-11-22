package com.ewallet.payment.service;

import com.ewallet.payment.entity.AuditLog;
import com.ewallet.payment.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAudit(String entityType, Long entityId, String action, String status, 
                        String message, String oldValue, String newValue, String createdBy) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setAction(action);
            auditLog.setStatus(status);
            auditLog.setMessage(message);
            auditLog.setOldValue(oldValue);
            auditLog.setNewValue(newValue);
            auditLog.setCreatedBy(createdBy);
            
            auditLogRepository.save(auditLog);
            logger.debug("Audit log created: {} - {} - {}", entityType, action, status);
        } catch (Exception e) {
            logger.error("Error creating audit log", e);
            // Don't throw - audit logging should not break business flow
        }
    }
}

