package com.itech.itech_backend.enums;

public enum KycDocumentType {
    PAN_CARD("PAN Card"),
    GST_CERTIFICATE("GST Certificate"),
    BUSINESS_REGISTRATION("Business Registration"),
    BANK_STATEMENT("Bank Statement"),
    INCORPORATION_CERTIFICATE("Incorporation Certificate"),
    TRADE_LICENSE("Trade License"),
    OTHER("Other");

    private final String displayName;

    KycDocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
