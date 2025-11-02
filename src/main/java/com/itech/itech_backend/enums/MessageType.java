package com.itech.itech_backend.enums;

public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    FILE("File"),
    DOCUMENT("Document"),
    VIDEO("Video"),
    AUDIO("Audio"),
    QUOTE("Quote"),
    REPLY("Reply"),
    FORWARD("Forward"),
    SYSTEM("System"),
    NOTIFICATION("Notification"),
    TYPING("Typing"),
    LOCATION("Location"),
    CONTACT("Contact");

    private final String displayName;

    MessageType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
