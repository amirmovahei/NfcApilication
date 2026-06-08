package com.example.amtehan;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context context;
    private final Class<?> restartActivity;

    public MyExceptionHandler(Context context, Class<?> restartActivity) {
        this.context = context.getApplicationContext();
        this.restartActivity = restartActivity;
    }
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder errorDetails = new StringBuilder();

        errorDetails.append("Exception: ").append(throwable.toString()).append("\n\n");

        // پیمایش عناصر StackTrace برای دریافت جزئیات
        for (StackTraceElement element : stackTrace) {
            errorDetails.append("at ")
                    .append(element.getClassName())
                    .append(".")
                    .append(element.getMethodName())
                    .append("(")
                    .append(element.getFileName())
                    .append(":")
                    .append(element.getLineNumber())
                    .append(")\n");
        }

        Intent intent = new Intent(context, restartActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("error_message", errorDetails.toString());
        context.startActivity(intent);


        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }







}
