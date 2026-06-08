package com.example.amtehan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 102;
    private static final int PICK_CONTACT_REQUEST = 1;
    AlertDialog dialog;
    boolean edit = false;
    private NfcAdapter nfcAdapter;
    private LottieAnimationView lottieAnimationView;
    private TextView status1;
    private Button lastSelectedButton = null;
    private Button text1, text2, text3, text4, text5, text6;
    private Button btnRead, btnWrite; // دکمه های جدید خواندن و نوشتن
    private Handler handler = new Handler();
    private int currentIndex = 0;
    private TextView text;
    private TextView text11;
    private CardView card, card2, card3;
    private EditText input;
    private boolean isButtonClicked = false;
    private boolean isWriteMode = false; // متغیر برای تشخیص حالت فعلی برنامه
    private Tag currentTag;
    String input1;
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    boolean email = false;
    boolean status = false;
    int m = 0;
    boolean number = false;
    RelativeLayout relativeLayout;
    private String dataType = "text";
    private String currentReadUri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.def));
            setLightStatusBarIconColor(false);
        }

        Configuration.getInstance().setUserAgentValue(getPackageName());
        File osmDir = new File(getCacheDir(), "osmdroid");
        Configuration.getInstance().setOsmdroidBasePath(osmDir);
        Configuration.getInstance().setOsmdroidTileCache(osmDir);

        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        nfcAdapter = nfcManager != null ? nfcManager.getDefaultAdapter() : null;

        if (nfcAdapter == null) {
            Toast.makeText(this, "این دستگاه NFC ندارد", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            Toast.makeText(this, "لطفا NFC دستگاه را فعال کنید", Toast.LENGTH_LONG).show();
        }

        initializeUI();

        Button buttonPersonal = findViewById(R.id.button); // این همان دکمه "پرسنلی" در صفحه اصلی است

        if (buttonPersonal != null) {
            buttonPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ساخت Intent برای رفتن به صفحه PersonalActivity
                    Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
                    startActivity(intent);
                }
            });
        }

        Button save = findViewById(R.id.button_save);
        save.setOnClickListener(view -> {
            String inputText22 = input.getText().toString();
            if (inputText22.contains("شماره موجود نیست")) {
                Toast.makeText(this, "شماره پیدا نشد", Toast.LENGTH_SHORT).show();
            } else {
                input1 = input.getText().toString().trim();
                if (input1.isEmpty()) {
                    if (!status) {
                        input.setError("از گزینه های بالا انتخاب فرمایید");
                        input.requestFocus();
                    } else {
                        input.setError("مقداری وارد کنید");
                    }
                } else {
                    if (email) {
                        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
                        if (input1.matches(emailPattern)) {
                            Toast.makeText(this, "کارت را پشت گوشی قرار دهید", Toast.LENGTH_SHORT).show();
                            isButtonClicked = true;
                        } else {
                            input.setError("لطفا یک ایمیل معتبر وارد کنید");
                            input.requestFocus();
                        }
                    } else if (number) {
                        if (input1.startsWith("0")||input1.startsWith("+98")) {
                            Toast.makeText(this, "کارت را پشت گوشی قرار دهید", Toast.LENGTH_SHORT).show();
                            isButtonClicked = true;
                        } else {
                            Toast.makeText(this, "شماره موبایل وارد شده معتبر نیست!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "کارت را پشت گوشی قرار دهید", Toast.LENGTH_SHORT).show();
                        isButtonClicked = true;
                    }
                }
            }
        });


        input.setOnClickListener(view -> {
            if (!edit) {
                Toast.makeText(this, "از گزینه های بالا انتخاب فرمایید", Toast.LENGTH_SHORT).show();
            }
        });


        input.setFocusable(false);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);

        Intent intent = getIntent();
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            onNewIntent(intent);
        }
    }


    private void initializeUI() {
        status1 = findViewById(R.id.status);
        text1 = findViewById(R.id.text1);
        text11 = findViewById(R.id.text11);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);
        card = findViewById(R.id.card);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        input = findViewById(R.id.input);
        text = findViewById(R.id.textstart);
        lottieAnimationView = findViewById(R.id.lottie);
        relativeLayout = findViewById(R.id.relativeLayout);

        btnRead = findViewById(R.id.btn_read);
        btnWrite = findViewById(R.id.btn_write);
        Button btnSave = findViewById(R.id.button_save);

        if (btnRead != null && btnWrite != null) {
            btnRead.setOnClickListener(v -> {
                isWriteMode = false;

                // تغییر استایل دکمه‌ها برای حالت خواندن
                btnRead.setBackgroundResource(R.drawable.button_background2);
                btnRead.setTextColor(getResources().getColor(R.color.white));
                btnWrite.setBackgroundResource(R.drawable.button_background);
                btnWrite.setTextColor(getResources().getColor(R.color.def));

                // مخفی کردن کامل دیالوگ ورود اطلاعات برای نمایش انیمیشن
                card.setVisibility(View.GONE);
                text11.setVisibility(View.GONE);

                text.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.VISIBLE);
                typeNewText("کارت را پشت دستگاه قرار دهید");
            });

            btnWrite.setOnClickListener(v -> {
                isWriteMode = true;

                // تغییر استایل دکمه‌ها برای حالت نوشتن
                btnWrite.setBackgroundResource(R.drawable.button_background2);
                btnWrite.setTextColor(getResources().getColor(R.color.white));
                btnRead.setBackgroundResource(R.drawable.button_background);
                btnRead.setTextColor(getResources().getColor(R.color.def));

                // برگرداندن ارتفاع دیالوگ به حالت تمام صفحه (0dp در ConstraintLayout)
                ViewGroup.LayoutParams layoutParams = card.getLayoutParams();
                layoutParams.height = 0;
                card.setLayoutParams(layoutParams);

                // --- اصلاح مهم: نمایش مجدد کادر اصلی دیالوگ ---
                card.setVisibility(View.VISIBLE);
                card2.setVisibility(View.VISIBLE);
                input.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                text11.setVisibility(View.VISIBLE);

                // اطمینان از نمایش تمام 6 دکمه
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.VISIBLE);
                text3.setVisibility(View.VISIBLE);
                text4.setVisibility(View.VISIBLE);
                text5.setVisibility(View.VISIBLE);
                text6.setVisibility(View.VISIBLE);

                // مخفی کردن وضعیت خواندن و انیمیشن
                text.setVisibility(View.GONE);
                lottieAnimationView.setVisibility(View.GONE);
            });

            // حالت اولیه: کلیک روی خواندن به صورت پیش‌فرض
            btnRead.performClick();
        }

        setButtonClickListener(text1, "text");
        setButtonClickListener(text2, "link");
        setButtonClickListener(text3, "contacts");
        setButtonClickListener(text4, "email");
        setButtonClickListener(text5, "number");
        setButtonClickListener(text6, "location");

        applyCardStyles();

        status1.setOnClickListener(v -> {
            if (currentReadUri != null && !currentReadUri.isEmpty()) {
                // --- بخش لینک‌ها (بدون هیچ تغییری) ---
                try {
                    Intent intent;
                    if (currentReadUri.startsWith("mailto:")) {
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentReadUri));
                        Intent chooser = Intent.createChooser(intent, "باز کردن با...");
                        startActivity(chooser);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "برنامه‌ای برای باز کردن این محتوا یافت نشد", Toast.LENGTH_SHORT).show();
                }
            } else {
                // --- بخش متن ساده (تغییرات جدید) ---
                String textToCopy = status1.getText().toString();
                if (!textToCopy.isEmpty()) {
                    // ۱. کپی کردن متن در کلیپ‌بورد
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "متن کپی شد", Toast.LENGTH_SHORT).show();

                    // ۲. باز کردن پنجره انتخاب برنامه برای ارسال یا ذخیره متن
                    try {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, textToCopy);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, "ارسال یا ذخیره متن با...");
                        startActivity(shareIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void applyCardStyles() {
        card2.setBackgroundResource(R.drawable.box_ren);
    }

    private void setButtonClickListener(Button button, String type) {
        button.setOnClickListener(v -> {
            if (lastSelectedButton != null) {
                lastSelectedButton.setBackgroundResource(R.drawable.button_background);
                lastSelectedButton.setTextColor(getResources().getColor(R.color.def));
            }

            status = true;

            if (type.equals("text")) {
                edit = true;
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                input.setText("");
                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input.setHint("فقط متن بنویسید");
                email = false;
                number = false;
                dataType = "text";
                status1.setText("");
                input.setFilters(new InputFilter[]{new NonPersianInputFilter(MainActivity.this)});
                input.setFilters(new InputFilter[]{new MaxLengthFilter(200, this)});
            } else if (type.equals("link")) {
                input.setText("");
                edit = true;
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input.setHint("فقط لینک وارد کنید");
                email = false;
                number = false;
                dataType = "link";
                status1.setText("");
                input.setFilters(new InputFilter[]{new NonPersianInputFilter(MainActivity.this)});
                input.setFilters(new InputFilter[]{new MaxLengthFilter(200, this)});
            } else if (type.equals("contacts")) {
                edit = true;
                input.setFilters(new InputFilter[]{});
                checkAndRequestReadContactsPermission();
                input.setText("");
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                email = false;
                number = false;
                status1.setText("");
            } else if (type.equals("number")) {
                edit = true;
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                number = true;
                input.setText("");
                email = false;
                status1.setText("");
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setHint("شماره را وارد کنید");
                dataType = "number";
            } else if (type.equals("location")) {
                edit = true;
                input.setFilters(new InputFilter[]{});
                input.setText("");
                email = false;
                number = false;
                status1.setText("");
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                checkLocationPermission();
                dataType = "location";
            } else if (type.equals("email")) {
                input.setText("");
                edit = true;
                status1.setText("");
                email = true;
                input.setFocusable(true);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                input.setHint("ایمیل را وارد کنید");
                input.setFilters(new InputFilter[]{new NonPersianInputFilter(MainActivity.this)});
                number = false;
                input.setFocusableInTouchMode(true);
                dataType = "email";
            }

            button.setBackgroundResource(R.drawable.button_selected);
            button.setTextColor(getResources().getColor(R.color.white));
            lastSelectedButton = button;
        });
    }
    static class MaxLengthFilter implements InputFilter {
        private final int maxLength;
        private final AppCompatActivity activity;

        public MaxLengthFilter(int maxLength, AppCompatActivity activity) {
            this.maxLength = maxLength;
            this.activity = activity;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int totalLength = dest.length() + (end - start) - (dend - dstart);
            if (totalLength > maxLength) {
                Toast.makeText(activity, "حداکثر 200 کاراکتر مجاز است", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    }
    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    private boolean containsPersian(String text) {
        return text.matches(".*[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF].*");
    }

    private void checkAndRequestReadContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSION_REQUEST_CODE);
        } else {
            pickContact();
        }
    }

    private void typeNewText(final String newText) {
        currentIndex = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentIndex < newText.length()) {
                    text.setText(newText.substring(0, currentIndex + 1));
                    currentIndex++;
                    handler.postDelayed(this, 30);
                }
            }
        }, 200);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            currentTag = tag;
            // بررسی حالت نوشتن (اگر دیالوگ نوشتن باز است و دکمه تایید فشرده شده)
            if (isWriteMode) {
                if (isButtonClicked) {
                    writeToCard(currentTag);
                    isButtonClicked = false;
                } else {
                    Toast.makeText(this, "ابتدا اطلاعات را کامل کرده و دکمه ثبت را بزنید", Toast.LENGTH_SHORT).show();
                }
            } else {
                // حالت خواندن
                String data = readFromNFCCard(tag);
                if (data == null || data.isEmpty()) {
                    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                            NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
                            NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                        typeNewText("کارت را مجدد قرار دهید");
                    }
                } else {
                    handleNFCIntent(intent);
                }
            }
        } else {
            Toast.makeText(this, "هیچ کارتی شناسایی نشد", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeToCard(Tag tag) {
        String dataToWrite = sanitizeInput(input.getText().toString());
        Ndef ndef = Ndef.get(tag);

        try {
            if (ndef == null) {
                formatTagToNdef(tag, dataToWrite);
            } else {
                writeNdefData(ndef, dataToWrite);
            }
        } catch (Exception e) {
            Toast.makeText(this, "خطا در پردازش تگ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void formatTagToNdef(Tag tag, String dataToWrite) {
        NdefFormatable formatable = NdefFormatable.get(tag);
        if (formatable == null) {
            Toast.makeText(this, "تگ قابل فرمت به NDEF نیست", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            formatable.connect();

            // --- تغییرات اعمال شده ---
            NdefRecord record;
            if ("link".equals(dataType)) {
                String normalizedLink = dataToWrite;
                // اگر کاربر خودش http/https را وارد نکرده بود، یکی را اضافه می‌کنیم تا مرورگر باز شود
                if (!dataToWrite.startsWith("http://") && !dataToWrite.startsWith("https://")) {
                    normalizedLink = "https://" + dataToWrite;
                }
                record = NdefRecord.createUri(normalizedLink);
            } else {
                record = NdefRecord.createTextRecord("fa", dataToWrite);
            }
            NdefMessage message = new NdefMessage(record);
            // ------------------------

            formatable.format(message);
            Toast.makeText(this, "تگ فرمت شد و داده‌ها ذخیره شدند", Toast.LENGTH_SHORT).show();
            status1.setText(dataToWrite);
        } catch (IOException e) {
            Toast.makeText(this, "خطا در فرمت کردن تگ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "خطا در پردازش تگ", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (formatable != null) {
                    formatable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void writeNdefData(Ndef ndef, String dataToWrite) {
        try {
            ndef.connect();
            if (!ndef.isWritable()) {
                Toast.makeText(this, "تگ قابل نوشتن نیست", Toast.LENGTH_SHORT).show();
                return;
            }

            NdefRecord record;
            if (dataType.equals("location")) {
                String geoUri = convertToGeoUri(dataToWrite);
                if (geoUri == null) {
                    Toast.makeText(this, "مختصات معتبر نیست", Toast.LENGTH_SHORT).show();
                    return;
                }
                record = NdefRecord.createUri(geoUri);
            } else if (dataType.equals("number")) {
                String phoneNumber = extractPhoneNumber(dataToWrite);
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    Toast.makeText(this, "شماره معتبری پیدا نشد", Toast.LENGTH_SHORT).show();
                    return;
                }
                record = NdefRecord.createUri("tel:" + phoneNumber);
            } else if (dataType.equals("contacts")) {
                String phoneNumber = extractPhoneNumber(dataToWrite);
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    Toast.makeText(this, "شماره معتبری پیدا نشد", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = dataToWrite.replace(phoneNumber, "").trim();
                if (name.isEmpty()) {
                    name = "Unknown";
                }

                String vCardData = createVCardString(name, phoneNumber);
                record = new NdefRecord(
                        NdefRecord.TNF_MIME_MEDIA,
                        "text/vcard".getBytes(StandardCharsets.UTF_8),
                        new byte[0],
                        vCardData.getBytes(StandardCharsets.UTF_8)
                );
            } else if (dataType.equals("link")) {
                // بررسی و افزودن http به لینک
                String normalizedLink = dataToWrite;
                if (!dataToWrite.startsWith("http://") && !dataToWrite.startsWith("https://")) {
                    normalizedLink = "https://" + dataToWrite;
                }
                // ذخیره لینک به صورت URI
                record = NdefRecord.createUri(normalizedLink);
            } else if (dataType.equals("email")) {
                record = NdefRecord.createUri("mailto:" + dataToWrite);
            } else {
                record = NdefRecord.createTextRecord("fa", dataToWrite);
            }

            NdefMessage message = new NdefMessage(record);
            ndef.writeNdefMessage(message);

            Toast.makeText(this, "داده‌ها با موفقیت ذخیره شدند", Toast.LENGTH_SHORT).show();
            status1.setText(dataToWrite);
        } catch (IOException e) {
            Toast.makeText(this, "خطا در نوشتن داده‌ها: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (FormatException e) {
            Toast.makeText(this, "فرمت داده نامعتبر است", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (ndef != null) {
                    ndef.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String createVCardString(String name, String phoneNumber) {
        StringBuilder vCard = new StringBuilder();
        vCard.append("BEGIN:VCARD\r\n");
        vCard.append("VERSION:3.0\r\n");
        vCard.append("N:").append(name != null ? name : "").append(";;;;\r\n");
        vCard.append("FN:").append(name != null ? name : "").append("\r\n");
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            vCard.append("TEL;TYPE=CELL:").append(phoneNumber).append("\r\n");
        }
        vCard.append("END:VCARD\r\n");
        return vCard.toString();
    }

    private String convertToGeoUri(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        // تبدیل اعداد فارسی به انگلیسی برای اطمینان از پردازش صحیح
        String normalizedInput = input
                .replace("۰", "0").replace("۱", "1").replace("۲", "2")
                .replace("۳", "3").replace("۴", "4").replace("۵", "5")
                .replace("۶", "6").replace("۷", "7").replace("۸", "8")
                .replace("۹", "9");

        // استخراج اعداد (مختصات) از متن با استفاده از Regex
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("-?\\d+(\\.\\d+)?");
        java.util.regex.Matcher matcher = pattern.matcher(normalizedInput);

        java.util.List<String> numbers = new java.util.ArrayList<>();
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        // اگر حداقل دو عدد (طول و عرض جغرافیایی) پیدا شد
        if (numbers.size() >= 2) {
            try {
                double lat = Double.parseDouble(numbers.get(0));
                double lon = Double.parseDouble(numbers.get(1));

                // بررسی معتبر بودن محدوده مختصات جغرافیایی
                if (lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
                    // 👇 مشکل اینجا بود! به جای کل متن، فقط باید اعداد طول و عرض را قرار دهیم.
                    // فرمت استاندارد برای مسیریاب‌ها: geo:lat,lon
                    return "geo:" + lat + "," + lon;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private String convertPersianDigitsToEnglish(String input) {
        if (input == null) return "";
        return input.replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
    }

    private String extractPhoneNumber(String input) {
        if (input.matches("\\d{11}") && input.startsWith("0")) {
            return input;
        }
        if (input.matches("\\+98\\d{10}")) {
            return input;
        }
        String[] parts = input.split("\\s+");
        for (String part : parts) {
            if (part.matches("\\d{11}") && part.startsWith("0")) {
                return part;
            }
            if (part.matches("\\+98\\d{10}")) {
                return part;
            }
        }
        return null;
    }

    private void handleNFCIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            String data = readFromNFCCard(tag);
            if (data != null) {
                status1.setText(data);
            }
        }
    }
    private String sanitizeInput(String input) {
        if (input == null) return "";
        return input.replaceAll("[^\\p{L}\\p{N}\\s@./:_-]", "").trim();
    }
    private String readFromNFCCard(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            Toast.makeText(this, "تگ از NDEF پشتیبانی نمی‌کند", Toast.LENGTH_SHORT).show();
            return null;
        }
        boolean isConnected = false;
        try {
            ndef.connect();
            isConnected = true;
            NdefMessage message = ndef.getNdefMessage();
            if (message != null && message.getRecords().length > 0) {
                NdefRecord record = message.getRecords()[0];
                String data;

                if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                    byte[] payload = record.getPayload();
                    int languageLength = payload[0];
                    data = new String(payload, languageLength + 1, payload.length - (languageLength + 1), StandardCharsets.UTF_8);

                    //  باز کردن خودکار پیشنهاد برنامه‌ها (یادداشت، پیامک و...) فقط برای متن ساده
                    try {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, "ارسال یا ذخیره متن با...");
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(shareIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                    String rawUri = record.toUri().toString();
                    currentReadUri = rawUri;

                    // --- باز کردن خودکار برنامه پس از اسکن ---
                    if (rawUri.startsWith("http://") || rawUri.startsWith("https://") || rawUri.startsWith("geo:") || rawUri.startsWith("tel:") || rawUri.startsWith("mailto:")) {
                        try {
                            Intent intent;
                            if (rawUri.startsWith("mailto:")) {
                                intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                // در اندروید، باز کردن فرمت geo: به صورت خودکار Chooser مسیریاب‌ها را می‌آورد
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rawUri));
                                Intent chooser = Intent.createChooser(intent, "باز کردن با...");
                                startActivity(chooser);
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "برنامه‌ای برای باز کردن این محتوا یافت نشد", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // پاکسازی دیتا برای نمایش متنی در UI
                    data = cleanUriData(rawUri);

                } else if (record.getTnf() == NdefRecord.TNF_MIME_MEDIA && Arrays.equals(record.getType(), "text/vcard".getBytes(StandardCharsets.UTF_8))) {
                    data = new String(record.getPayload(), StandardCharsets.UTF_8);
                    data = parseVCard(data);
                } else {
                    data = new String(record.getPayload(), StandardCharsets.UTF_8);
                }

                if (data != null && !data.isEmpty()) {
                    updateUI(data);
                    return data;
                } else {
                    return null;
                }
            } else {
                if (m == 3) {
                    Toast.makeText(this, "کارت را به طرف دیگه بگیرید و کمی فاصله دهید", Toast.LENGTH_LONG).show();
                    m = 0;
                } else {
                    Toast.makeText(this, "اطلاعات درست خوانده نشد مجدد تلاش فرمایید", Toast.LENGTH_SHORT).show();
                    m++;
                }
                return null;
            }
        } catch (IOException e) {
            Toast.makeText(this, "خطا در خواندن داده‌ها: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        } catch (FormatException e) {
            Toast.makeText(this, "فرمت داده نامعتبر است", Toast.LENGTH_SHORT).show();
            return null;
        } catch (SecurityException e) {
            Toast.makeText(this, "تگ منقضی شده، لطفاً دوباره امتحان کنید", Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            if (isConnected) {
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void openLocationIntent(String geoUri) {
        try {
            // حذف کاراکترهای مخفی و اسپیس‌های اضافی
            String cleanGeo = geoUri.replaceAll("[\\x00-\\x1F\\x7F]", "").trim();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cleanGeo));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // اجبار اندروید به نمایش پنجره انتخاب برنامه (App Chooser)
            Intent chooser = Intent.createChooser(intent, "مسیریابی با:");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            } else {
                Toast.makeText(this, "هیچ برنامه نقشه‌ای نصب نیست!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در باز کردن نقشه", Toast.LENGTH_SHORT).show();
        }
    }
    private String parseVCard(String vCardData) {
        String name = "Unknown";
        String phoneNumber = "شماره موجود نیست";

        String[] lines = vCardData.split("\r\n");
        for (String line : lines) {
            if (line.startsWith("N:")) {
                String[] nameParts = line.replace("N:", "").split(";");
                if (nameParts.length > 0 && !nameParts[0].isEmpty()) {
                    name = nameParts[0];
                }
            } else if (line.startsWith("TEL")) {
                phoneNumber = line.split(":")[1];
            }
        }
        return name + " " + phoneNumber;
    }
    private String cleanUriData(String data) {
        if (data == null) return "";

        // حذف کاراکترهای کنترلی نامرئی
        String clean = data.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        if (clean.startsWith("mailto:")) {
            return clean.replace("mailto:", "");
        } else if (clean.startsWith("tel:")) {
            return clean.replace("tel:", "");
        } else if (clean.startsWith("geo:")) {
            String coords = clean.replace("geo:", "");
            if (coords.startsWith("0,0?q=")) {
                coords = coords.replace("0,0?q=", "");
            }
            String[] parts = coords.split(",");
            if (parts.length >= 2) {
                return "Lat: " + parts[0] + "\nLon: " + parts[1];
            }
            return coords;
        }
        return clean;
    }
    private void updateUI(String data) {
        status1.setText(data);
        lottieAnimationView.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        text11.setVisibility(View.GONE);

        // جمع کردن ارتفاع دیالوگ برای اینکه فضای خالی دکمه‌های مخفی شده زشت نشود
        ViewGroup.LayoutParams layoutParams2 = card.getLayoutParams();
        layoutParams2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        card.setLayoutParams(layoutParams2);

        // نمایش کادر اصلی و باکس آبی رنگ اطلاعات
        card.setVisibility(View.VISIBLE);
        card2.setVisibility(View.VISIBLE);

        // در حالت نمایش اطلاعات خوانده شده، اجزای مربوط به نوشتن را مخفی می‌کنیم
        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        text3.setVisibility(View.GONE);
        text4.setVisibility(View.GONE);
        text5.setVisibility(View.GONE);
        text6.setVisibility(View.GONE);
        input.setVisibility(View.GONE);

        Button btnSave = findViewById(R.id.button_save);
        if (btnSave != null) {
            btnSave.setVisibility(View.GONE);
        }

        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
        layoutParams.height = convertDpToPx(110);
        relativeLayout.setLayoutParams(layoutParams);

        ViewGroup.MarginLayoutParams layoutParams3 = (ViewGroup.MarginLayoutParams) card3.getLayoutParams();
        layoutParams3.topMargin = convertDpToPx(60);
        card3.setLayoutParams(layoutParams3);

        ViewGroup.MarginLayoutParams layoutParams4 = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        layoutParams4.topMargin = convertDpToPx(100);
        card.setLayoutParams(layoutParams4);
    }


    public int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent == null || (!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) &&
                !NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) &&
                !NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))) {
            typeNewText("کارت را پشت دستگاه قرار دهید");
        }
        if (nfcAdapter != null) {
            Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);

            IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            IntentFilter tagFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            IntentFilter[] intentFilters = new IntentFilter[]{ndefFilter, techFilter, tagFilter};

            String[][] techList = new String[][]{new String[]{Ndef.class.getName()}};

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techList);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    @SuppressLint("WrongConstant")
    private void setLightStatusBarIconColor(boolean isLightTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isLightTheme) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }
    private void showMapDialog() {
        Dialog mapDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mapDialog.setContentView(R.layout.dialog_map);

        mapView = mapDialog.findViewById(R.id.mapView);
        Button btnCurrentLocation = mapDialog.findViewById(R.id.btnCurrentLocation);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        GeoPoint qomPoint = new GeoPoint(34.639944, 50.875942);
        mapView.getController().setCenter(qomPoint);
        mapView.getController().setZoom(14.0);

        locationOverlay = new MyLocationNewOverlay(mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        mapView.getOverlays().add(new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
                Projection projection = mapView.getProjection();
                GeoPoint clickedPoint = (GeoPoint) projection.fromPixels((int) event.getX(), (int) event.getY());
                input.setText(String.format(java.util.Locale.US, "Lat: %.6f, Lon: %.6f",
                        clickedPoint.getLatitude(), clickedPoint.getLongitude()));

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                mapDialog.dismiss();
                return true;
            }
        });
        btnCurrentLocation.setOnClickListener(view -> {
            moveToCurrentLocation(mapDialog);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        mapDialog.show();
    }
    private void moveToCurrentLocation(Dialog mapDialog) {
        GeoPoint location = locationOverlay.getMyLocation();
        if (location != null) {
            GeoPoint currentPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapView.getController().setCenter(currentPoint);
            mapView.getController().setZoom(18.0);
            mapDialog.dismiss();
            input.setText(String.format(java.util.Locale.US, "Lat: %.6f, Lon: %.6f",
                    location.getLatitude(), location.getLongitude()));

        } else {
            Toast.makeText(this, "موقعیت مکانی شما پیدا نشد مجدد تلاش کنید !", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkAndEnableGPS();
        }
    }
    private void checkAndEnableGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null);

            TextView titleText = customView.findViewById(R.id.dialog_title);
            TextView messageText = customView.findViewById(R.id.dialog_message);
            ImageView locationIcon = customView.findViewById(R.id.location_icon);
            Button nextButton = customView.findViewById(R.id.next_button);

            nextButton.setBackgroundResource(R.drawable.button_background2);

            titleText.setText("کاربر گرامی!");
            messageText.setText("برنامه، برای استفاده بهتر از نقشه، به اجازه دسترسی مکان شما نیاز دارد.");
            nextButton.setText("بعدی");

            nextButton.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE);
            });

            builder.setView(customView);
            dialog = builder.create();
            dialog.show();
        } else {
            showMapDialog();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContact();
            } else {
                Toast.makeText(this, "مجوز رد شد عملیات لغو شد", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAndEnableGPS();
            } else {
                Toast.makeText(this, "عملیات لغو شد", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showMapDialog();
            } else {
                Toast.makeText(this, "موقعیت مکانی روشن نیست", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                retrieveContact(contactUri);
            }
        }
    }
    private void retrieveContact(Uri contactUri) {
        String name = null;
        String phoneNumber = null;

        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);

                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone != null && hasPhone.equals("1")) {
                        try (Cursor phoneCursor = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null)) {
                            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                                int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                phoneNumber = phoneCursor.getString(phoneIndex);
                                if (phoneNumber != null) {
                                    phoneNumber = phoneNumber.replaceAll("\\s+", "");
                                    if (phoneNumber.startsWith("+98")) {
                                        phoneNumber = "0" + phoneNumber.substring(3);
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }
        String r = "اطلاعات موجود نیست";
        String rb = "شماره موجود نیست";
        String contactInfo = "" + (name != null ? name : r) + " " + (phoneNumber != null ? phoneNumber : rb);
        input.setText(contactInfo);
    }
}
