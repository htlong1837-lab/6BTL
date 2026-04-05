package com.auction.common.until;

public class PasswordUtil {

    // Kiểm tra mật khẩu đủ mạnh không
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper = false, hasLower = false, hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c))     hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    // Hash password đơn giản
    public static String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
}