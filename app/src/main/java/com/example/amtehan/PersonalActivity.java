package com.example.amtehan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PersonalActivity extends AppCompatActivity {

    private Button btnPersonalLink, btnCustomLink, btnGenerateLink;
    private Spinner spinnerBaseDomains;
    private EditText etCustomBaseLink, input;
    private TextView tvLinkPreview, status;

    // متغیر برای تشخیص اینکه کاربر در حالت لینک سفارشی است یا پرسنلی (پیش‌فرض: پرسنلی)
    private boolean isCustomMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // نام فایل XML شما (اگر نام دیگری دارد آن را تغییر دهید)
        setContentView(R.layout.activity_persenol);

        // 1. مقداردهی اولیه المان‌های UI بر اساس آیدی‌های XML
        btnPersonalLink = findViewById(R.id.btnPersonalLink);
        btnCustomLink = findViewById(R.id.btnCustomLink);
        btnGenerateLink = findViewById(R.id.btnGenerateLink);
        spinnerBaseDomains = findViewById(R.id.spinnerBaseDomains);
        etCustomBaseLink = findViewById(R.id.etCustomBaseLink);
        input = findViewById(R.id.input); // فیلد کد ملی / شماره تلفن
        tvLinkPreview = findViewById(R.id.tvLinkPreview);
        status = findViewById(R.id.status); // وضعیت خواندن کارت

        // 2. تنظیم مقادیر پیش‌فرض برای Spinner (دامنه‌های پرسنلی)
        String[] defaultDomains = {
                "https://tanianict.com/widgets/user/",
                "https://example.com/profile/"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultDomains);
        spinnerBaseDomains.setAdapter(adapter);

        // نمایش پیش‌فرض حالت پرسنلی در ابتدای ورود به صفحه
        spinnerBaseDomains.setVisibility(View.VISIBLE);
        etCustomBaseLink.setVisibility(View.GONE);

        // 3. رویداد کلیک دکمه "لینک پرسنلی"
        btnPersonalLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomMode = false;
                spinnerBaseDomains.setVisibility(View.VISIBLE);
                etCustomBaseLink.setVisibility(View.GONE);

                // در صورت نیاز می‌توانید رنگ دکمه‌ها را اینجا تغییر دهید تا حالت انتخاب شده مشخص شود
            }
        });

        // 4. رویداد کلیک دکمه "لینک سفارشی"
        btnCustomLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCustomMode = true;
                spinnerBaseDomains.setVisibility(View.GONE);
                etCustomBaseLink.setVisibility(View.VISIBLE);
            }
        });

        // 5. رویداد کلیک دکمه ذخیره (تولید و هش کردن لینک)
        btnGenerateLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String baseLink = "";
                String nationalIdOrPhone = input.getText().toString().trim();

                // بررسی خالی نبودن کد ملی/شماره
                if (nationalIdOrPhone.isEmpty()) {
                    Toast.makeText(PersonalActivity.this, "لطفاً کد ملی یا شماره همراه را وارد کنید", Toast.LENGTH_SHORT).show();
                    return;
                }

                // دریافت لینک پایه بر اساس حالت انتخاب شده
                if (isCustomMode) {
                    baseLink = etCustomBaseLink.getText().toString().trim();
                    if (baseLink.isEmpty()) {
                        Toast.makeText(PersonalActivity.this, "لطفاً لینک پایه سفارشی را وارد کنید", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    baseLink = spinnerBaseDomains.getSelectedItem().toString();
                }

                // بررسی داشتن اسلش در انتهای لینک پایه
                if (!baseLink.endsWith("/")) {
                    baseLink += "/";
                }

                // هش کردن اطلاعات ورودی
                String hashedData = HashUtils.generateShortHash(nationalIdOrPhone);

                if (hashedData != null) {
                    // ترکیب و ساخت لینک نهایی
                    String finalLink = baseLink + hashedData;

                    // نمایش لینک در پایین صفحه
                    tvLinkPreview.setVisibility(View.VISIBLE);
                    tvLinkPreview.setText("لینک: " + finalLink);

                    // ذخیره لینک در SharedPreferences جهت استفاده برای رایت روی NFC یا شبیه‌سازی
                    SharedPreferences prefs = getSharedPreferences("NfcPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("saved_nfc_link", finalLink);
                    editor.apply();

                    Toast.makeText(PersonalActivity.this, "لینک با موفقیت ذخیره شد آماده رایت...", Toast.LENGTH_SHORT).show();

                    // TODO: در اینجا می‌توانید متد مربوط به رایت روی NFC را فراخوانی کنید
                } else {
                    Toast.makeText(PersonalActivity.this, "خطا در تولید هش!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
