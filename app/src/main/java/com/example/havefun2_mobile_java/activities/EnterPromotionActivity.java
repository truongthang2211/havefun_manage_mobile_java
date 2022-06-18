package com.example.havefun2_mobile_java.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.models.HostUser;
import com.example.havefun2_mobile_java.models.Promotion;
import com.example.havefun2_mobile_java.models.Timestamp;
import com.example.havefun2_mobile_java.utils.PathUtil;
import com.example.havefun2_mobile_java.utils.WebServiceAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnterPromotionActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // the activity result code
    int SELECT_PICTURE = 200;
    String ServerURL ;
    String HotelID ;
    MaterialButton btnSaveVoucher;
    MaterialButton btnCancelVoucher;
    ImageView imgPreviewImage;
    Uri uriImagePromotion;
    Bitmap bitmap;


    long lTimeStart, lTimeEnd;

    final Calendar myCalendar = Calendar.getInstance();
    EditText editTextTitle;
    EditText editTextDescription;
    Slider editTextDiscount;
    EditText editTextStart;
    EditText editTextEnd;
    AutoCompleteTextView autoCompleteTextViewPromotionType;

    CheckBox enterpro_hour_chk;
    CheckBox enterpro_day_chk;
    CheckBox enterpro_overnight_chk;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_promotion);
        SharedPreferences pref =getApplicationContext().getSharedPreferences("User", 0);
        String name = pref.getString("hostuserObject", "undefined");
        ServerURL= getString(R.string.server_address);
        if (!name.equals("undefined")) {
            HostUser hostuser = new Gson().fromJson(name, HostUser.class);
            HotelID = hostuser.getHotel_id();
            if (HotelID==null){
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Hãy nhập thông tin khánh sạn của bạn trước!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                })
                        .show();
            }
        } else {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went wrong!").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    finish();
                }
            })
                    .show();
        }
        Intent intent = getIntent();
        String proStr = intent.getStringExtra("promotion");
        btnSaveVoucher = findViewById(R.id.btnSaveVoucher);


        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        editTextTitle = findViewById(R.id.etVoucherTitleName);
        editTextDescription = findViewById(R.id.etVoucherDescriptionName);
        editTextStart = (EditText) findViewById(R.id.etDateStart);
        btnCancelVoucher = findViewById(R.id.btnCancelVoucher);
        editTextDiscount = findViewById(R.id.enterroom_slider_slider);
        enterpro_hour_chk = findViewById(R.id.enterpro_hour_chk);
        enterpro_day_chk = findViewById(R.id.enterpro_day_chk);
        enterpro_overnight_chk = findViewById(R.id.enterpro_overnight_chk);
        TimePickerDialog.OnTimeSetListener timeStart = (timePicker, i, i1) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, i);
            myCalendar.set(Calendar.MINUTE, i1);
            myCalendar.set(Calendar.SECOND, 0);
            updateLabel(editTextStart);
            lTimeStart = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
        };
        DatePickerDialog.OnDateSetListener dateStart = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            new TimePickerDialog(EnterPromotionActivity.this, timeStart, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
        };

        editTextStart.setOnClickListener(view -> new DatePickerDialog(EnterPromotionActivity.this, dateStart, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        editTextEnd = (EditText) findViewById(R.id.etDateEnd);
        TimePickerDialog.OnTimeSetListener timeEnd = (timePicker, i, i1) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, i);
            myCalendar.set(Calendar.MINUTE, i1);
            myCalendar.set(Calendar.SECOND, 0);
            updateLabel(editTextEnd);
            lTimeEnd = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();

        };
        DatePickerDialog.OnDateSetListener dateEnd = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            new TimePickerDialog(EnterPromotionActivity.this, timeEnd, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
        };
        editTextEnd.setOnClickListener(view -> new DatePickerDialog(EnterPromotionActivity.this, dateEnd, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        imgPreviewImage = findViewById(R.id.imgPreviewImageVoucher);
        imgPreviewImage.setOnClickListener(v -> imageChooser());
        btnCancelVoucher.setOnClickListener(view -> finish());
        if (proStr != null) {
            Promotion p = new Gson().fromJson(proStr, Promotion.class);
            RenderPromotion(p);
            btnSaveVoucher.setText("Lưu");

            btnSaveVoucher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Promotion editp = GetPromotion();
                    editp.setId(p.getId());
                    onSubmit("edit",editp);
                }
            });

        } else {
            btnSaveVoucher.setText("Thêm");
            btnSaveVoucher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSubmit("addnew",GetPromotion());
                }
            });

        }
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(i, SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                uriImagePromotion = selectedImageUri;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (null != selectedImageUri) {
                    imgPreviewImage.setImageBitmap(bitmap);
                }

            }
        }
    }


    private void updateLabel(EditText editText) {
//        String myFormat = "MM/dd/yy";
        String myFormat = "dd-MM-yyyy\nHH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(dateFormat.format(myCalendar.getTime()));
    }


    // Post Request For JSONObject
    public void onSubmit(String option, Promotion pro) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",option);
        setResult(Activity.RESULT_OK,returnIntent);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerURL).addConverterFactory(GsonConverterFactory.create()).build();
        WebServiceAPI uploadService = retrofit.create(WebServiceAPI.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("data", new Gson().toJson(pro));

        if (uriImagePromotion!=null){
            String stringPath = null;
            try {
                stringPath = PathUtil.getPath(EnterPromotionActivity.this, uriImagePromotion);
                File file = new File(stringPath);
                builder.addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }



        MultipartBody requestBody = builder.build();
        Call call = option.equals("addnew") ? uploadService.CreatePromotion(requestBody) : uploadService.EditPromotion(requestBody);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                try {
                    JSONObject respondOb = new JSONObject(new Gson().toJson(response.body()));
                    int status = respondOb.getInt("status");
                    if (status == 200){
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Throwable e = t;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RenderPromotion(Promotion p) {
        Picasso.get().load(p.getImg()).into(imgPreviewImage);
        editTextTitle.setText(p.getName());
        enterpro_hour_chk.setChecked(p.getOrder_type().contains("hour"));
        enterpro_overnight_chk.setChecked(p.getOrder_type().contains("overnight"));
        enterpro_day_chk.setChecked(p.getOrder_type().contains("daily"));
        editTextDescription.setText(p.getDescription());
        editTextDiscount.setValue(p.getDiscount_ratio() * 100);
        setCalendar(p.getTime_start().toLocalDateTime());
        updateLabel(editTextStart);
        lTimeStart = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
        setCalendar(p.getTime_end().toLocalDateTime());
        updateLabel(editTextEnd);
        lTimeEnd = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setCalendar(LocalDateTime d) {
        myCalendar.set(Calendar.YEAR, d.getYear());
        myCalendar.set(Calendar.MONTH, d.getMonthValue());
        myCalendar.set(Calendar.DAY_OF_MONTH, d.getDayOfMonth());
        myCalendar.set(Calendar.HOUR_OF_DAY, d.getHour());
        myCalendar.set(Calendar.MINUTE, d.getMinute());
        myCalendar.set(Calendar.SECOND, 0);
    }

    private Promotion GetPromotion() {
        Promotion postPromotion = new Promotion();
        postPromotion.setHotel_id(HotelID);
        postPromotion.setName(editTextTitle.getText().toString());
        ArrayList<String> order_type = new ArrayList<>();
        if (enterpro_day_chk.isChecked())
            order_type.add("daily");
        if (enterpro_overnight_chk.isChecked())
            order_type.add("overnight");
        if (enterpro_hour_chk.isChecked())
            order_type.add("hour");
        postPromotion.setOrder_type(order_type);
        postPromotion.setDescription(editTextDescription.getText().toString());
        postPromotion.setDiscount_ratio(Float.parseFloat(String.valueOf(editTextDiscount.getValue() / 100)));

        postPromotion.setTime_start(new Timestamp(lTimeStart, 0));
        postPromotion.setTime_end(new Timestamp(lTimeEnd, 0));
        return postPromotion;
    }

}