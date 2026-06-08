//package com.example.amtehan;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.PendingIntent;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.nfc.FormatException;
//import android.nfc.NdefMessage;
//import android.nfc.NdefRecord;
//import android.nfc.NfcAdapter;
//import android.nfc.NfcManager;
//import android.nfc.Tag;
//import android.nfc.tech.Ndef;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.ContactsContract;
//import android.provider.Settings;
//import android.text.InputFilter;
//import android.text.InputType;
//import android.text.Spanned;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.airbnb.lottie.LottieAnimationView;
//
//import org.osmdroid.config.Configuration;
//import org.osmdroid.library.BuildConfig;
//import org.osmdroid.util.GeoPoint;
//import org.osmdroid.views.MapView;
//import org.osmdroid.views.Projection;
//import org.osmdroid.views.overlay.Overlay;
//import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//public class tsts extends AppCompatActivity {
//    private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 100;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
//    private static final int LOCATION_SETTINGS_REQUEST_CODE = 102;
//    private static final int PICK_CONTACT_REQUEST = 1;
//
//    AlertDialog dialog;
//    boolean edit = false;
//    private NfcAdapter nfcAdapter;
//    private LottieAnimationView lottieAnimationView;
//    private TextView status1;
//    private Button lastSelectedButton = null;
//    private Button text1, text2, text3, text4, text5, text6;
//    private Handler handler = new Handler();
//    private int currentIndex = 0;
//    private TextView text;
//    private TextView text11;
//    private CardView card, card2, card3;
//    private EditText input;
//    private boolean isButtonClicked = false;
//    private Tag currentTag;
//    String input1;
//    private MapView mapView;
//    private MyLocationNewOverlay locationOverlay;
//    boolean email = false;
//    boolean status = false;
//    int m = 0;
//    boolean number = false;
//    RelativeLayout relativeLayout;
//    String currentDataType = "text";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.def));
//            setLightStatusBarIconColor(false);
//        }
//
//        // تنظیم User-Agent برای OsmDroid
//        Configuration.getInstance().setUserAgentValue(getPackageName());
//
//        // تنظیمات کش
//        File osmDir = new File(getCacheDir(), "osmdroid");
//        Configuration.getInstance().setOsmdroidBasePath(osmDir);
//        Configuration.getInstance().setOsmdroidTileCache(osmDir);
//
//        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
//        nfcAdapter = nfcManager != null ? nfcManager.getDefaultAdapter() : null;
//
//        if (nfcAdapter == null) {
//            Toast.makeText(this, "این دستگاه NFC ندارد", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//
//        if (!nfcAdapter.isEnabled()) {
//            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
//            Toast.makeText(this, "لطفا NFC دستگاه را فعال کنید", Toast.LENGTH_LONG).show();
//        }
//
//        initializeUI();
//
//        Button save = findViewById(R.id.button_save);
//        save.setOnClickListener(view -> {
//            String inputText22 = input.getText().toString();
//            if (inputText22.contains("شماره موجود نیست")) {
//                Toast.makeText(this, "شماره پیدا نشد", Toast.LENGTH_SHORT).show();
//            } else {
//                input1 = input.getText().toString().trim();
//                if (input1.isEmpty()) {
//                    if (!status) {
//                        input.setError("از گزینه های بالا انتخاب فرمایید");
//                        input.requestFocus();
//                    } else {
//                        input.setError("مقداری وارد کنید");
//                    }
//                } else {
//                    if (email) {
//                        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
//                        if (input1.matches(emailPattern)) {
//                            Toast.makeText(this, "کارت را پشت گوشی قرار دهید", Toast.LENGTH_SHORT).show();
//                            isButtonClicked = true;
//                        } else {
//                            input.setError("لطفا یک ایمیل معتبر وارد کنید");
//                            input.requestFocus();
//                        }
//                    } else if (number) {
//                        if (input1.startsWith("0")) {
//                            Toast.makeText(this, "کارت را پشت گوشی قرار دهید", Toast.LENGTH_SHORT).show();
//                            isButtonClicked = true;
//                        } else {
//                            Toast.makeText(this, "شماره موبایل وارد شده معتبر نیست!", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(this, "کارت را پشت گوشی قرار دهید", Toast.LENGTH_SHORT).show();
//                        isButtonClicked = true;
//                    }
//                }
//            }
//        });
//
//        input.setOnClickListener(view -> {
//            if (!edit) {
//                Toast.makeText(this, "از گزینه های بالا انتخاب فرمایید", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        input.setFocusable(false);
//        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
//    }
//
//    private void initializeUI() {
//        status1 = findViewById(R.id.status);
//        text1 = findViewById(R.id.text1);
//        text11 = findViewById(R.id.text11);
//        text2 = findViewById(R.id.text2);
//        text3 = findViewById(R.id.text3);
//        text4 = findViewById(R.id.text4);
//        text5 = findViewById(R.id.text5);
//        text6 = findViewById(R.id.text6);
//        card = findViewById(R.id.card);
//        card2 = findViewById(R.id.card2);
//        card3 = findViewById(R.id.card3);
//        input = findViewById(R.id.input);
//        text = findViewById(R.id.textstart);
//        lottieAnimationView = findViewById(R.id.lottie);
//        relativeLayout = findViewById(R.id.relativeLayout);
//
//        setButtonClickListener(text1, "text");
//        setButtonClickListener(text2, "link");
//        setButtonClickListener(text3, "contacts");
//        setButtonClickListener(text4, "email");
//        setButtonClickListener(text5, "number");
//        setButtonClickListener(text6, "location");
//
//        applyCardStyles();
//
//        status1.setOnClickListener(v -> {
//            String textToCopy = status1.getText().toString();
//            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("کپی شده", textToCopy);
//            clipboard.setPrimaryClip(clip);
//            Toast.makeText(this, "متن کپی شد!", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    private void applyCardStyles() {
//        card2.setBackgroundResource(R.drawable.box_ren);
//    }
//
//    private void setButtonClickListener(Button button, String type) {
//        button.setOnClickListener(v -> {
//            if (lastSelectedButton != null) {
//                lastSelectedButton.setBackgroundResource(R.drawable.button_background);
//                lastSelectedButton.setTextColor(getResources().getColor(R.color.def));
//            }
//
//            status = true;
//            currentDataType = type;
//
//            if (type.equals("text")) {
//                edit = true;
//                input.setFocusable(true);
//                input.setFocusableInTouchMode(true);
//                input.setText("");
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
//                input.setHint("فقط متن بنویسید");
//                email = false;
//                number = false;
//
//                InputFilter filter = new InputFilter() {
//                    @Override
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        String inputText = dest.toString() + source.toString();
//                        if (containsPersian(inputText)) {
//                            if (inputText.length() >= 24) {
//                                Toast.makeText(tsts.this, "حداکثر کاراکتر مجاز", Toast.LENGTH_SHORT).show();
//                                return "";
//                            }
//                        } else if (containsEnglish(inputText)) {
//                            if (inputText.length() > 47) {
//                                Toast.makeText(tsts.this, "حداکثر کاراکتر مجاز", Toast.LENGTH_SHORT).show();
//                                return "";
//                            }
//                        }
//                        return null;
//                    }
//
//                    private boolean containsPersian(String inputText) {
//                        return inputText.matches("[\u0600-\u06FF]+");
//                    }
//
//                    private boolean containsEnglish(String inputText) {
//                        return inputText.matches("[a-zA-Z]+");
//                    }
//                };
//                input.setFilters(new InputFilter[]{filter});
//
//            } else if (type.equals("link")) {
//                input.setText("");
//                edit = true;
//                input.setFocusable(true);
//                input.setFocusableInTouchMode(true);
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
//                input.setHint("فقط لینک وارد کنید");
//                email = false;
//                number = false;
//                input.setFilters(new InputFilter[]{new NonPersianInputFilter(tsts.this)});
//
//            } else if (type.equals("contacts")) {
//                edit = true;
//                input.setFilters(new InputFilter[]{});
//                checkAndRequestReadContactsPermission();
//                input.setText("");
//                input.setFocusable(true);
//                input.setFocusableInTouchMode(true);
//                email = false;
//                number = false;
//            } else if (type.equals("number")) {
//                edit = true;
//                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
//                input.setFocusable(true);
//                input.setFocusableInTouchMode(true);
//                number = true;
//                input.setText("");
//                email = false;
//                input.setInputType(InputType.TYPE_CLASS_NUMBER);
//                input.setHint("شماره را وارد کنید");
//            } else if (type.equals("location")) {
//                edit = true;
//                input.setFilters(new InputFilter[]{});
//                input.setText("");
//                email = false;
//                number = false;
//                input.setFocusable(true);
//                input.setFocusableInTouchMode(true);
//                checkLocationPermission();
//            } else if (type.equals("email")) {
//                input.setText("");
//                edit = true;
//                email = true;
//                input.setFocusable(true);
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
//                input.setHint("ایمیل را وارد کنید");
//                input.setFilters(new InputFilter[]{new NonPersianInputFilter(tsts.this)});
//                number = false;
//                input.setFocusableInTouchMode(true);
//            }
//
//            button.setBackgroundResource(R.drawable.button_selected);
//            button.setTextColor(getResources().getColor(R.color.white));
//            lastSelectedButton = button;
//        });
//    }
//
//    private void pickContact() {
//        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        startActivityForResult(intent, PICK_CONTACT_REQUEST);
//    }
//
//    private boolean containsPersian(String text) {
//        return text.matches(".*[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF].*");
//    }
//
//    private void checkAndRequestReadContactsPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_CONTACTS},
//                    READ_CONTACTS_PERMISSION_REQUEST_CODE);
//        } else {
//            pickContact();
//        }
//    }
//
//    private void typeNewText(final String newText) {
//        currentIndex = 0;
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (currentIndex < newText.length()) {
//                    text.setText(newText.substring(0, currentIndex + 1));
//                    currentIndex++;
//                    handler.postDelayed(this, 30);
//                }
//            }
//        }, 200);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        if (isButtonClicked) {
//            currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            if (currentTag != null) {
//                writeToCard(currentTag);
//            } else {
//                Toast.makeText(this, "هیچ کارتی شناسایی نشد", Toast.LENGTH_SHORT).show();
//            }
//            isButtonClicked = false;
//        } else {
//            handleNFCIntent(intent);
//        }
//    }
//
//    private void writeToCard(Tag tag) {
//        String rawData = input.getText().toString().trim();
//        String dataToWrite = (currentDataType.equals("text")) ? sanitizeInput(rawData) : rawData;
//
//        Ndef ndef = Ndef.get(tag);
//        if (ndef == null) {
//            Toast.makeText(this, "تگ از NDEF پشتیبانی نمی‌کند", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            ndef.connect();
//            if (!ndef.isWritable()) {
//                Toast.makeText(this, "تگ قابل نوشتن نیست", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            NdefRecord record;
//            switch (currentDataType) {
//                case "link":
//                    String url = dataToWrite;
//                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
//                        url = "https://" + url;
//                    }
//                    record = NdefRecord.createUri(url);
//                    break;
//                case "email":
//                    record = NdefRecord.createUri("mailto:" + dataToWrite);
//                    break;
//                case "number":
//                    record = NdefRecord.createUri("tel:" + dataToWrite);
//                    break;
//                default: // برای text, contacts, location فعلاً متن ساده مینویسیم
//                    record = NdefRecord.createTextRecord("fa", dataToWrite);
//                    break;
//            }
//
//            NdefMessage message = new NdefMessage(record);
//            ndef.writeNdefMessage(message);
//
//            Toast.makeText(this, "داده‌ها با موفقیت ذخیره شدند", Toast.LENGTH_SHORT).show();
//            status1.setText(dataToWrite);
//        } catch (IOException e) {
//            Toast.makeText(this, "خطا در نوشتن داده‌ها: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "فرمت تگ نامعتبر است", Toast.LENGTH_SHORT).show();
//        } finally {
//            try {
//                if (ndef != null) {
//                    ndef.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void handleNFCIntent(Intent intent) {
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        if (tag != null) {
//            readFromNFCCard(tag);
//        }
//    }
//
//    public String sanitizeInput(String input) {
//        if (input == null || input.isEmpty()) {
//            return "";
//        }
//        // اجازه دادن به کاراکترهای آدرس اینترنتی و ایمیل و برداشتن محدودیت طول
//        return input.replaceAll("[^\\p{L}\\p{N}\\s@./:_-]", "").trim();
//    }
//
//    private String readFromNFCCard(Tag tag) {
//        Ndef ndef = Ndef.get(tag);
//        if (ndef == null) {
//            Toast.makeText(this, "تگ از NDEF پشتیبانی نمی‌کند", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//
//        try {
//            ndef.connect();
//            NdefMessage message = ndef.getNdefMessage();
//            if (message != null) {
//                NdefRecord record = message.getRecords()[0];
//                String data = "";
//
//                // تشخیص اینکه آیا رکورد لینک/URI است یا متن
//                if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && java.util.Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
//                    data = record.toUri().toString();
//                } else {
//                    // فرض بر اینکه متن است
//                    byte[] payload = record.getPayload();
//                    if (payload.length > 0) {
//                        int languageCodeLength = payload[0] & 0063;
//                        if (payload.length > languageCodeLength + 1) {
//                            data = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, StandardCharsets.UTF_8);
//                        } else {
//                            data = new String(payload, StandardCharsets.UTF_8);
//                        }
//                    }
//                }
//                updateUI(data);
//                return data;
//            } else {
//                if (m == 3) {
//                    Toast.makeText(this, "کارت را به طرف دیگه بگیرید و کمی فاصله دهید", Toast.LENGTH_LONG).show();
//                    m = 0;
//                } else {
//                    Toast.makeText(this, "اطلاعات درست خوانده نشد مجدد تلاش فرمایید", Toast.LENGTH_SHORT).show();
//                    m++;
//                }
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "خطا در خواندن داده‌ها", Toast.LENGTH_SHORT).show();
//        } finally {
//            try {
//                if (ndef != null) ndef.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    private void updateUI(String data) {
//        status1.setText(data);
//        lottieAnimationView.setVisibility(View.GONE);
//        text.setVisibility(View.GONE);
//        text11.setVisibility(View.VISIBLE);
//        card.setVisibility(View.VISIBLE);
//
//        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
//        layoutParams.height = convertDpToPx(110);
//        relativeLayout.setLayoutParams(layoutParams);
//
//        ViewGroup.LayoutParams layoutParams2 = card.getLayoutParams();
//        layoutParams2.height = convertDpToPx(500);
//        card.setLayoutParams(layoutParams2);
//
//        ViewGroup.MarginLayoutParams layoutParams3 = (ViewGroup.MarginLayoutParams) card3.getLayoutParams();
//        layoutParams3.topMargin = convertDpToPx(60);
//        card3.setLayoutParams(layoutParams3);
//
//        ViewGroup.MarginLayoutParams layoutParams4 = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
//        layoutParams4.topMargin = convertDpToPx(100);
//        card.setLayoutParams(layoutParams4);
//    }
//
//    public int convertDpToPx(int dp) {
//        float density = getResources().getDisplayMetrics().density;
//        return (int) (dp * density);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        typeNewText("کارت را پشت دستگاه قرار دهید");
//        Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);
//        IntentFilter[] intentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)};
//        if (nfcAdapter != null) {
//            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (nfcAdapter != null) {
//            nfcAdapter.disableForegroundDispatch(this);
//        }
//    }
//
//    @SuppressLint("WrongConstant")
//    private void setLightStatusBarIconColor(boolean isLightTheme) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            View decorView = getWindow().getDecorView();
//            int flags = decorView.getSystemUiVisibility();
//            if (isLightTheme) {
//                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            } else {
//                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            }
//            decorView.setSystemUiVisibility(flags);
//        }
//    }
//
//    private void showMapDialog() {
//        Dialog mapDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        mapDialog.setContentView(R.layout.dialog_map);
//
//        mapView = mapDialog.findViewById(R.id.mapView);
//        Button btnCurrentLocation = mapDialog.findViewById(R.id.btnCurrentLocation);
//
//        mapView.setBuiltInZoomControls(true);
//        mapView.setMultiTouchControls(true);
//
//        GeoPoint qomPoint = new GeoPoint(34.639944, 50.875942);
//        mapView.getController().setCenter(qomPoint);
//        mapView.getController().setZoom(14.0);
//
//        locationOverlay = new MyLocationNewOverlay(mapView);
//        locationOverlay.enableMyLocation();
//        mapView.getOverlays().add(locationOverlay);
//
//        mapView.getOverlays().add(new Overlay() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent event, MapView mapView) {
//                Projection projection = mapView.getProjection();
//                GeoPoint clickedPoint = (GeoPoint) projection.fromPixels((int) event.getX(), (int) event.getY());
//                input.setText(String.format("Lat: %.6f, Lon: %.6f",
//                        clickedPoint.getLatitude(), clickedPoint.getLongitude()));
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                mapDialog.dismiss();
//                return true;
//            }
//        });
//
//        btnCurrentLocation.setOnClickListener(view -> {
//            moveToCurrentLocation(mapDialog);
//            if (dialog != null && dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        });
//        mapDialog.show();
//    }
//
//    private void moveToCurrentLocation(Dialog mapDialog) {
//        GeoPoint location = locationOverlay.getMyLocation();
//        if (location != null) {
//            GeoPoint currentPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//            mapView.getController().setCenter(currentPoint);
//            mapView.getController().setZoom(18.0);
//            mapDialog.dismiss();
//            input.setText(String.format("Lat: %.6f, Lon: %.6f", location.getLatitude(), location.getLongitude()));
//        } else {
//            Toast.makeText(this, "موقعیت مکانی شما پیدا نشد مجدد تلاش کنید !", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            checkAndEnableGPS();
//        }
//    }
//
//    private void checkAndEnableGPS() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
//            View customView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null);
//
//            TextView titleText = customView.findViewById(R.id.dialog_title);
//            TextView messageText = customView.findViewById(R.id.dialog_message);
//            ImageView locationIcon = customView.findViewById(R.id.location_icon);
//            Button nextButton = customView.findViewById(R.id.next_button);
//
//            nextButton.setBackgroundResource(R.drawable.button_background2);
//
//            titleText.setText("کاربر گرامی!");
//            messageText.setText("برنامه، برای استفاده بهتر از نقشه، به اجازه دسترسی مکان شما نیاز دارد.");
//            nextButton.setText("بعدی");
//
//            nextButton.setOnClickListener(v -> {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE);
//            });
//
//            builder.setView(customView);
//            dialog = builder.create();
//            dialog.show();
//        } else {
//            showMapDialog();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                pickContact();
//            } else {
//                Toast.makeText(this, "مجوز رد شد عملیات لغو شد", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                checkAndEnableGPS();
//            } else {
//                Toast.makeText(this, "عملیات لغو شد", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                showMapDialog();
//            } else {
//                Toast.makeText(this, "موقعیت مکانی روشن نیست", Toast.LENGTH_SHORT).show();
//            }
//        } else if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK && data != null) {
//            Uri contactUri = data.getData();
//            if (contactUri != null) {
//                retrieveContact(contactUri);
//            }
//        }
//    }
//
//    private void retrieveContact(Uri contactUri) {
//        String name = null;
//        String phoneNumber = null;
//
//        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//                    name = cursor.getString(nameIndex);
//
//                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    @SuppressLint("Range") String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
//                    if (hasPhone != null && hasPhone.equals("1")) {
//                        try (Cursor phoneCursor = getContentResolver().query(
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                null,
//                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                                new String[]{id},
//                                null)) {
//                            if (phoneCursor != null && phoneCursor.moveToFirst()) {
//                                int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                                phoneNumber = phoneCursor.getString(phoneIndex);
//                                if (phoneNumber != null) {
//                                    phoneNumber = phoneNumber.replaceAll("\\s+", "");
//                                }
//                            }
//                        }
//                    }
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        String r = "اطلاعات موجود نیست";
//        String rb = "شماره موجود نیست";
//        String contactInfo = "" + (name != null ? name : r) + " " + (phoneNumber != null ? phoneNumber : rb);
//        input.setText(contactInfo);
//    }
//
//    //  توابع کمکی استاتیک اضافه شده برای استفاده در MainActivity
//
//    public static String cleanUriData(String uriData) {
//        if (uriData == null) return "";
//        if (uriData.startsWith("tel:")) return uriData.substring(4);
//        if (uriData.startsWith("mailto:")) return uriData.substring(7);
//        if (uriData.startsWith("geo:")) return uriData.substring(4);
//        if (uriData.startsWith("http://")) return uriData.substring(7);
//        if (uriData.startsWith("https://")) return uriData.substring(8);
//        return uriData;
//    }
//
//    public static String parseVCard(String data) {
//        String name = "";
//        String phone = "";
//        String[] lines = data.split("\n");
//        for (String line : lines) {
//            if (line.startsWith("FN:")) {
//                name = line.substring(3).trim();
//            } else if (line.startsWith("TEL;")) {
//                int colonIndex = line.indexOf(':');
//                if (colonIndex != -1) {
//                    phone = line.substring(colonIndex + 1).trim();
//                }
//            }
//        }
//        if (!name.isEmpty() && !phone.isEmpty()) {
//            return "مخاطب: " + name + "\nشماره: " + phone;
//        } else if (!name.isEmpty()) {
//            return "مخاطب: " + name;
//        } else if (!phone.isEmpty()) {
//            return "شماره: " + phone;
//        }
//        return data;
//    }
//
//    public static String convertToGeoUri(String input) {
//        try {
//            String[] parts = input.replace("Lat:", "").replace("Lon:", "").split(",");
//            if (parts.length == 2) {
//                String lat = parts[0].trim();
//                String lon = parts[1].trim();
//                return "geo:" + lat + "," + lon;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return input;
//    }
//
//    public static String createVCardString(String name, String phoneNumber) {
//        return "BEGIN:VCARD\n" +
//                "VERSION:3.0\n" +
//                "FN:" + name + "\n" +
//                "TEL;TYPE=CELL:" + phoneNumber + "\n" +
//                "END:VCARD";
//    }
//    public static String extractPhoneNumber(String input) {
//        String[] parts = input.split(" ");
//        for (String part : parts) {
//            if (part.matches("\\d+")) {
//                return part;
//            }
//        }
//        return input;
//    }
//}
