package com.itech.itech_backend.modules.core.util;

import com.itech.itech_backend.modules.core.model.User;

public class UserCompatibilityHelper {
    
    public static String roleToString(User.UserRole role) {
        return role != null ? role.name() : null;
    }
    
    public static User.UserRole stringToRole(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            return User.UserRole.BUYER;
        }
        try {
            return User.UserRole.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return User.UserRole.BUYER;
        }
    }
    
    public static String replaceInRole(User.UserRole role, String oldStr, String newStr) {
        return role != null ? role.name().replace(oldStr, newStr) : "";
    }
    
    public static boolean isRoleEmpty(User.UserRole role) {
        return role == null;
    }
    
    public static boolean isUserVerified(User user) {
        return user != null && user.getIsVerified() != null && user.getIsVerified();
    }
    
    public static void setUserVerified(User user, boolean verified) {
        if (user != null) {
            user.setIsVerified(verified);
        }
    }
    
    public static boolean isUserActive(User user) {
        return user != null && user.getIsActive() != null && user.getIsActive();
    }
    
    public static void setUserActive(User user, boolean active) {
        if (user != null) {
            user.setIsActive(active);
        }
    }
}
