package com.example.amtehan;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.nio.charset.StandardCharsets;

public class MyHostApduService extends HostApduService {

    private static final String TAG = "MyHostApduService";
    // همان AID که در فایل xml تعریف کردیم
    private static final String SAMPLE_AID = "F0010203040506";
    // دستور انتخاب (Select APDU)
    private static final String SELECT_APDU_HEADER = "00A40400";
    // پیام موقتی که قرار است به گوشی دوم ارسال شود
    private String messageToSend = "https://tanianict.com/widgets/user/test";

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        String hexCommand = bytesToHex(commandApdu);
        Log.d(TAG, "Received APDU: " + hexCommand);

        // اگر گوشی دوم دستور Select را با AID ما فرستاد
        if (hexCommand.startsWith(SELECT_APDU_HEADER) && hexCommand.contains(SAMPLE_AID)) {
            Log.d(TAG, "AID Selected! Sending data...");
            // تبدیل پیام به بایت و اضافه کردن وضعیت موفقیت آمیز (9000) در انتها
            byte[] messageBytes = messageToSend.getBytes(StandardCharsets.UTF_8);
            byte[] response = new byte[messageBytes.length + 2];
            System.arraycopy(messageBytes, 0, response, 0, messageBytes.length);
            response[response.length - 2] = (byte) 0x90;
            response[response.length - 1] = (byte) 0x00;
            return response;
        }

        // پاسخ ناشناخته
        return new byte[]{(byte) 0x6F, (byte) 0x00};
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d(TAG, "Deactivated: " + reason);
    }

    // متد کمکی برای تبدیل بایت به رشته هگزادسیمال
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
