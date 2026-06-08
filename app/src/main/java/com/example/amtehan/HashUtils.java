package com.example.amtehan;
import java.security.MessageDigest;

public class HashUtils {

    // تولید یک هش کوتاه (مثلا 12 کاراکتری) از متن ورودی با الگوریتم SHA-512
    public static String generateShortHash(String input) {
        try {
            // تغییر الگوریتم از SHA-256 به SHA-512
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // برگرداندن 12 کاراکتر اول برای کوتاه ماندن لینک
            // (اگر می‌خواهید کل طول هش را داشته باشید، می‌توانید فقط hexString.toString() را ریترن کنید)
            return hexString.toString().substring(0, 12);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
