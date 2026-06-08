package com.example.amtehan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;




import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by iranian on 15/11/2015.
 */

public class error extends AppCompatActivity {

//    private static Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        setTheme(R.style.Custom);
        TextView txt=findViewById(R.id.txt);
        TextView exit=findViewById(R.id.exit);
        Typeface font = ResourcesCompat.getFont(this,R.font.iransansweb_fanum);
        txt.setTypeface(font, Typeface.NORMAL);
        exit.setTypeface(font, Typeface.NORMAL);
        exit.setOnClickListener(view -> {

                Intent intent = new Intent(error.this, splash.class);
                startActivity(intent);
                finish(); // جایگزینی با System.exit(0)


        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(error.this, R.color.def));
            setLightStatusBarIconColor(false);
        }

        String errorMessage = getIntent().getStringExtra("error_message");
        if (errorMessage != null) {
            sendC(errorMessage);
            Log.e("Error", "No error message found");
        } else {
            Log.e("Error", "No error message found");
        }



    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(error.this, splash.class);
        startActivity(intent);
        System.exit(0);
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


    private void sendC(final String logs) {
        String appName = getString(R.string.app_name);
        String appVersion = "unknown";
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        // دریافت نسخه برنامه
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ErrorActivity", "Failed to get app version: " + e.getMessage());
        }

        Log.d("ErrorActivity", "App Name: " + appName);
        Log.d("ErrorActivity", "App Version: " + appVersion);
        Log.d("ErrorActivity", "Device Name: " + deviceName);

        // ایجاد فایل لاگ
        File logFile = new File(getFilesDir(), "error_log.txt");
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write(logs); // ابتدا لاگ‌ها نوشته شوند
            writer.write("\n\n--- Device Information ---\n");
            writer.write("App Name: " + appName + "\n");
            writer.write("App Version: " + appVersion + "\n");
            writer.write("Device Name: " + deviceName + "\n");
            Log.d("ErrorActivity", "Log file created: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("ErrorActivity", "Error writing log to file: " + e.getMessage());
            return;
        }

        // ساخت درخواست ارسال
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", "faragard")
                .addFormDataPart("hardware", "mobile")
                .addFormDataPart("hardware_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                .addFormDataPart("os", "android")
                .addFormDataPart("os_ver", String.valueOf(Build.VERSION.SDK_INT))
                .addFormDataPart("logs", "error_log.txt", RequestBody.create(MediaType.parse("application/octet-stream"), logFile))
                .build();

        Request request = new Request.Builder()
                .url("https://glpi.ir/esb/v1/system/logs")
                .post(requestBody)
                .build();

        // ارسال درخواست
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ErrorActivity", "Logs sent successfully: " + response.body().string());
                } else {
                    Log.e("ErrorActivity", "Failed to send logs. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ErrorActivity", "Failed to send logs: " + e.getMessage());
            }
        });
    }





}
