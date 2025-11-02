package com.itech.itech_backend.enums;

public enum AttributeType {
    TEXT("Text"),
    NUMBER("Number"),
    DECIMAL("Decimal"),
    BOOLEAN("Boolean"),
    SELECT("Single Select"),
    MULTI_SELECT("Multi Select"),
    DATE("Date"),
    COLOR("Color"),
    IMAGE("Image"),
    URL("URL");

    private final String displayName;

    AttributeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isNumeric() {
        return this == NUMBER || this == DECIMAL;
    }

    public boolean isSelectable() {
        return this == SELECT || this == MULTI_SELECT;
    }

    public boolean requiresOptions() {
        return isSelectable() || this == COLOR;
    }
}
