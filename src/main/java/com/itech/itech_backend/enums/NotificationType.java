package com.itech.itech_backend.enums;

public enum NotificationType {
    INFO("Information"),
    SUCCESS("Success"),
    WARNING("Warning"),
    ERROR("Error"),
    INQUIRY("New Inquiry"),
    QUOTE("New Quote"),
    QUOTE_ACCEPTED("Quote Accepted"),
    MESSAGE("New Message"),
    ORDER("New Order"),
    ORDER_UPDATE("Order Update"),
    KYC_APPROVED("KYC Approved"),
    KYC_REJECTED("KYC Rejected"),
    KYC_UPDATE("KYC Update"),
    PRODUCT_APPROVED("Product Approved"),
    PRODUCT_REJECTED("Product Rejected"),
    SUPPORT_TICKET("Support Ticket"),
    PAYMENT("Payment"),
    REVIEW("New Review"),
    SUBSCRIPTION("Subscription"),
    SYSTEM("System Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
