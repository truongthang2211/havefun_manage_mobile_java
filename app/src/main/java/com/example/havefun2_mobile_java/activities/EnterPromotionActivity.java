package com.example.havefun2_mobile_java.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.models.Promotion;
import com.example.havefun2_mobile_java.models.Timestamp;
import com.example.havefun2_mobile_java.utils.PathUtil;
import com.example.havefun2_mobile_java.utils.WebServiceAPI;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnterPromotionActivity extends AppCompatActivity {
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // the activity result code
    int SELECT_PICTURE = 200;

    Button btnSaveVoucher;
    ImageView imgPreviewImage;
    Uri uriImagePromotion;
    Bitmap bitmap;

    String filePath;
    Date timestart;
    Date timeend;

    long lTimeStart, lTimeEnd;

    final Calendar myCalendar = Calendar.getInstance();
    EditText editTextTitle;
    EditText editTextDescription;
    EditText editTextDiscount;
    EditText editTextStart;
    EditText editTextEnd;
    AutoCompleteTextView autoCompleteTextViewPromotionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        setContentView(R.layout.activity_enter_promotion);
        editTextTitle = findViewById(R.id.etVoucherTitleName);
        editTextDescription = findViewById(R.id.etVoucherDescriptionName);
        editTextDiscount = findViewById(R.id.etDiscountVoucher);
        autoCompleteTextViewPromotionType = findViewById(R.id.edit_ip);
        editTextStart = (EditText) findViewById(R.id.etDateStart);
        btnSaveVoucher = findViewById(R.id.btnSaveVoucher);
        DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                timestart  = myCalendar.getTime();
                java.sql.Timestamp timestampstart = new java.sql.Timestamp(timestart.getTime());
//                java.sql.Timestamp ggg;
//                ggg = java.sql.Timestamp.valueOf(sdf3.format(timestampstart));
                lTimeStart = (timestampstart.getTime()/1000);
//                lTimeStart = ggg.getTime();

                Log.e("npm", String.valueOf(lTimeStart));

//                Timestamp timestamp = new Timestamp(myCalendar);
                updateLabel(editTextStart);
            }
        };
        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EnterPromotionActivity.this, dateStart, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                Timestamp timestamp = new Timestamp(my.getTime());
//                timestart = dateStart.myCalendar.getTime();
            }
        });

        editTextEnd = (EditText) findViewById(R.id.etDateEnd);
        DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                timeend  = myCalendar.getTime();
                java.sql.Timestamp timestampend = new java.sql.Timestamp(timeend.getTime());
                lTimeEnd = (timestampend.getTime()/1000);
                updateLabel(editTextEnd);
            }
        };
        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EnterPromotionActivity.this, dateEnd, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        btnSelectImage = findViewById(R.id.btnSelectImageVoucher);
        imgPreviewImage = findViewById(R.id.imgPreviewImageVoucher);

        imgPreviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });


        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.edit_ip);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.myarray));
        textView.setAdapter(adapter);
        //set spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_ip);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView.setText(spinner.getSelectedItem().toString());
                textView.dismissDropDown();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView.setText(spinner.getSelectedItem().toString());
                textView.dismissDropDown();
            }
        });

        btnSaveVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postData();
            }
        });

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
                    // update the preview image in the layout
//                    imgPreviewImage.setImageURI(selectedImageUri);
                    imgPreviewImage.setImageBitmap(bitmap);
                }
//                Log.e("ebc", String.valueOf(imgPreviewImage.getTag()));

            }
        }
    }



    private void updateLabel(EditText editText) {
        String myFormat = "MM/dd/yy";
//        String myFormat = "dd-M-yyyy hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(dateFormat.format(myCalendar.getTime()));
    }


    // Post Request For JSONObject
    public void postData() {

        String url = "http://172.16.7.124:3000";


        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        WebServiceAPI uploadService = retrofit.create(WebServiceAPI.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Promotion postPromotion = new Promotion();
        postPromotion.setHotel_id("k0N8OV6408ddA1ktDSdg");
        postPromotion.setName(editTextTitle.getText().toString());
        postPromotion.setDescription(editTextDescription.getText().toString());
        postPromotion.setDiscount_ratio(Float.parseFloat(editTextDiscount.getText().toString()));

        Timestamp promotionTimestampStart = new Timestamp();
        promotionTimestampStart.setSeconds(lTimeStart);
        promotionTimestampStart.setNanoseconds(0);
        postPromotion.setTime_start(promotionTimestampStart);

        Timestamp promotionTimestampEnd = new Timestamp();
        promotionTimestampEnd.setSeconds(lTimeEnd);
        promotionTimestampEnd.setNanoseconds(0);
        postPromotion.setTime_end(promotionTimestampEnd);

        builder.addFormDataPart("data", new Gson().toJson(postPromotion));


        String stringPath = null;
        try {
            stringPath = PathUtil.getPath(EnterPromotionActivity.this,uriImagePromotion);
            File file = new File(stringPath);
            builder.addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        MultipartBody requestBody = builder.build();
        Call call = uploadService.CreatePromotion(requestBody);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                try {
                    JSONObject respondOb = new JSONObject(response.body().toString());
                    JSONObject hotelJs = respondOb.getJSONObject("data");
                    Log.e("aqs", String.valueOf(hotelJs));
                    Promotion p = new Gson().fromJson(hotelJs.toString(),Promotion.class);

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



}