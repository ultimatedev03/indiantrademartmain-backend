package com.itech.itech_backend.enums;

public enum KycStatus {
    NOT_SUBMITTED("Not Submitted"),
    PENDING("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    RESUBMISSION_REQUIRED("Resubmission Required");

    private final String displayName;

    KycStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
