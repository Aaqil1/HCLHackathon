package com.ewallet.notification.dto;

public class NotificationResponse {
    private String notificationBatchId;
    private String status;

    public NotificationResponse() {
    }

    public NotificationResponse(String notificationBatchId, String status) {
        this.notificationBatchId = notificationBatchId;
        this.status = status;
    }

    // Getters and Setters
    public String getNotificationBatchId() {
        return notificationBatchId;
    }

    public void setNotificationBatchId(String notificationBatchId) {
        this.notificationBatchId = notificationBatchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

