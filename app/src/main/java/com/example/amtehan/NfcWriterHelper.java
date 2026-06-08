package com.example.amtehan;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

public class NfcWriterHelper {

    // این متد زمانی فراخوانی می‌شود که کاربر روی "ذخیره" در دیالوگ لینک پرسنلی کلیک کند
    public static void prepareAndWritePersonnelLink(Tag nfcTag, String userInput) {

        // 1. تبدیل ورودی (مثلا کد ملی) به هش کوتاه
        String hashCode = HashUtils.generateShortHash(userInput);

        if (hashCode != null) {
            // 2. ساخت لینک نهایی
            String finalUrl = "https://my.tanianict.com/widget/users/" + hashCode;

            // 3. نوشتن روی کارت
            writeUrlToTag(nfcTag, finalUrl);
        }
    }
    // متد استاندارد برای نوشتن یک لینک (URI) روی کارت NFC به فرمت NDEF
    private static void writeUrlToTag(Tag tag, String url) {
        try {
            NdefRecord uriRecord = NdefRecord.createUri(url);
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{uriRecord});

            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    // کارت قفل است
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                // پیام موفقیت آمیز: لینک با موفقیت روی کارت نوشته شد
            } else {
                // کارت فرمت NDEF ندارد، تلاش برای فرمت کردن آن
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    format.connect();
                    format.format(ndefMessage);
                    format.close();
                    // پیام موفقیت آمیز
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // پیام خطا در نوشتن
        }
    }
}
