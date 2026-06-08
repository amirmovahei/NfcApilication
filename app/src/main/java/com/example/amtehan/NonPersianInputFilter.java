package com.example.amtehan;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

public class NonPersianInputFilter implements InputFilter {

    private final Context context;

    public NonPersianInputFilter(Context context) {
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            char character = source.charAt(i);

            if (Character.UnicodeBlock.of(character) == Character.UnicodeBlock.ARABIC) {
                Toast.makeText(context, "حروف فارسی مجاز نیست", Toast.LENGTH_SHORT).show();
                return "";
            }
        }
        return null;
    }
}
