package com.example.havefun2_mobile_java.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.adapters.SliderImgsAdapter;
import com.example.havefun2_mobile_java.models.Facility;
import com.example.havefun2_mobile_java.models.HostUser;
import com.example.havefun2_mobile_java.models.Hotel;
import com.example.havefun2_mobile_java.models.Room;
import com.example.havefun2_mobile_java.models.RoomCondition;
import com.example.havefun2_mobile_java.utils.PathUtil;
import com.example.havefun2_mobile_java.utils.WebServiceAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnterRoomActivity extends AppCompatActivity {
    Context context;
    String ServerURL;
    String HotelID;

    public static final int PICK_IMAGE = 1;
    ArrayList<Uri> imageUris;
    TextInputEditText txt_TenPhong;
    TextInputEditText txt_MaPhong;
    TextInputEditText txt_LoaiPhong;
    TextInputEditText txt_MoTaPhong;
    TextInputEditText txt_GioDauTien;
    TextInputEditText txt_GioTiepTheo;
    TextInputEditText txt_GGQuaDem;
    TextInputEditText txt_GiaNgay;

    CheckBox chk_GiuongDoi;
    CheckBox chk_30m2;
    CheckBox chk_CuaSo;
    CheckBox chk_SanGo;
    CheckBox chk_Wifi;
    CheckBox chk_TV;
    CheckBox chk_LeTan;
    CheckBox chk_MayLanh;
    CheckBox chk_ThangMay;
    CheckBox chk_TruyenHinhCap;

    MaterialButton btn_Them;
    MaterialButton btn_Huy;
    MaterialButton enterroom_edit_Mbtn;

    ViewPager2 myroom_imgs_viewpager;
    WormDotsIndicator worm_dots_indicator;
    ImageView myroom_chooseimg_ImV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", 0);
        String name = pref.getString("hostuserObject", "undefined");
        ServerURL = getString(R.string.server_address);
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
        context = this;
        myroom_chooseimg_ImV = findViewById(R.id.myroom_chooseimg_ImV);
        txt_TenPhong = findViewById(R.id.txt_TenPhong);
        txt_MaPhong = findViewById(R.id.txt_MaPhong);
        txt_LoaiPhong = findViewById(R.id.txt_LoaiPhong);
        txt_MoTaPhong = findViewById(R.id.txt_MoTaPhong);
        txt_GioDauTien = findViewById(R.id.txt_GioDauTien);
        txt_GioTiepTheo = findViewById(R.id.txt_GioTiepTheo);
        txt_GGQuaDem = findViewById(R.id.txt_GGQuaDem);
        txt_GiaNgay = findViewById(R.id.txt_GiaNgay);

        chk_GiuongDoi = findViewById(R.id.chk_GiuongDoi);
        chk_30m2 = findViewById(R.id.chk_20m2);
        chk_CuaSo = findViewById(R.id.chk_CuaSo);
        chk_SanGo = findViewById(R.id.chk_SanGo);
        chk_Wifi = findViewById(R.id.chk_Wifi);
        chk_TV = findViewById(R.id.chk_TV);
        chk_LeTan = findViewById(R.id.chk_LeTan);
        chk_MayLanh = findViewById(R.id.chk_MayLanh);
        chk_ThangMay = findViewById(R.id.chk_ThangMay);
        chk_TruyenHinhCap = findViewById(R.id.chk_TruyenHinhCap);

        btn_Them = findViewById(R.id.btn_Them);
        btn_Huy = findViewById(R.id.btn_Huy);
        enterroom_edit_Mbtn = findViewById(R.id.enterroom_edit_Mbtn);
        btn_Huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        worm_dots_indicator = findViewById(R.id.worm_dots_indicator);
        myroom_imgs_viewpager = findViewById(R.id.myroom_imgs_viewpager);
        myroom_chooseimg_ImV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE);
            }
        });
        enterroom_edit_Mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE);
            }
        });
        Intent intent = getIntent();
        String roomStr = intent.getStringExtra("room");
        if (roomStr != null) {
            Room r = new Gson().fromJson(roomStr, Room.class);
            RenderRoom(r);
            btn_Them.setText("Lưu");
            btn_Them.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Room editRoom = getRoom();
                    editRoom.setId(r.getId());
                    onSubmit("edit", editRoom);
                }
            });
        } else {
            btn_Them.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSubmit("addnew", new Room());
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                imageUris = new ArrayList<>();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; ++i) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                }
                SliderImgsAdapter adapter = new SliderImgsAdapter(context, imageUris);
                myroom_imgs_viewpager.setAdapter(adapter);
                worm_dots_indicator.setViewPager2(myroom_imgs_viewpager);
                if (imageUris.size() != 0) {
                    myroom_chooseimg_ImV.setVisibility(View.INVISIBLE);
                    enterroom_edit_Mbtn.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    private void onSubmit(String option, Room r) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", option);
        setResult(Activity.RESULT_OK, returnIntent);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerURL).addConverterFactory(GsonConverterFactory.create()).build();
        WebServiceAPI uploadService = retrofit.create(WebServiceAPI.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("hotel_id", HotelID);
            postObject.put("room", new JSONObject(new Gson().toJson(getRoom())));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (option.equals("addnew")) {
            builder.addFormDataPart("data", postObject.toString());

        } else {
            builder.addFormDataPart("data", new Gson().toJson(r));

        }

        for (int i = 0; i < imageUris.size(); i++) {
            try {
                String stringPath = PathUtil.getPath(context, imageUris.get(i));
                File file = new File(stringPath);
                builder.addFormDataPart("imgs", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        MultipartBody requestBody = builder.build();

        Call call = option.equals("addnew") ? uploadService.AddRoom(requestBody) : uploadService.EditRoom(requestBody);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    JSONObject respondOb = new JSONObject(new Gson().toJson(response.body()));
                    int status = respondOb.getInt("status");
                    if (status == 200) {
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

    private Room getRoom() {
        Room r = new Room();
        r.setName(String.valueOf(txt_TenPhong.getText()));
        r.setDescription(String.valueOf(txt_MoTaPhong.getText()));
        r.setRoom_type(String.valueOf(txt_LoaiPhong.getText()));
        r.setRoom_id(String.valueOf(txt_MaPhong.getText()));
        r.setHour_price(Float.valueOf(String.valueOf(txt_GioDauTien.getText())));
        r.setHour_price_bonus(Float.valueOf(String.valueOf(txt_GioTiepTheo.getText())));
        r.setDaily_price(Float.valueOf(String.valueOf(txt_GiaNgay.getText())));
        r.setOvernight_price(Float.valueOf(String.valueOf(txt_GGQuaDem.getText())));

        RoomCondition condition = new RoomCondition();
        condition.setArea_20m2(chk_30m2.isChecked());
        condition.setDouble_bed(chk_GiuongDoi.isChecked());
        condition.setWindow(chk_CuaSo.isChecked());

        r.setRoom_conditions(condition);
        Facility fac = new Facility();
        fac.setAir_conditioning(chk_MayLanh.isChecked());
        fac.setCable_tv(chk_TruyenHinhCap.isChecked());
        fac.setElevator(chk_ThangMay.isChecked());
        fac.setReception24(chk_LeTan.isChecked());
        fac.setWifi(chk_Wifi.isChecked());
        fac.setWood_floor(chk_SanGo.isChecked());
        fac.setTv(chk_TV.isChecked());
        r.setFacilities(fac);
        return r;
    }

    private void RenderRoom(Room r) {
        txt_TenPhong.setText(r.getName());
        txt_MoTaPhong.setText(r.getDescription());
        txt_LoaiPhong.setText(r.getRoom_type());
        txt_MaPhong.setText(r.getRoom_id());
        txt_GioDauTien.setText(new java.text.DecimalFormat("#").format(r.getHour_price()));
        txt_GioTiepTheo.setText(new java.text.DecimalFormat("#").format(r.getHour_price_bonus()));
        txt_GiaNgay.setText(new java.text.DecimalFormat("#").format(r.getDaily_price()));
        txt_GGQuaDem.setText(new java.text.DecimalFormat("#").format(r.getOvernight_price()));
        chk_30m2.setChecked(r.getRoom_conditions().isArea_20m2());
        chk_GiuongDoi.setChecked(r.getRoom_conditions().isDouble_bed());
        chk_CuaSo.setChecked(r.getRoom_conditions().isWindow());
        chk_MayLanh.setChecked(r.getFacilities().isAir_conditioning());

        chk_TruyenHinhCap.setChecked(r.getFacilities().isAir_conditioning());
        chk_ThangMay.setChecked(r.getFacilities().isElevator());
        chk_LeTan.setChecked(r.getFacilities().isReception24());
        chk_Wifi.setChecked(r.getFacilities().isWifi());
        chk_SanGo.setChecked(r.getFacilities().isWood_floor());
        chk_TV.setChecked(r.getFacilities().isTv());

        ArrayList<Uri> uris = new ArrayList<>();
        for (String url : r.getImgs()) {
            uris.add(Uri.parse(url));
        }
        SliderImgsAdapter adapter = new SliderImgsAdapter(new ArrayList<String>(Arrays.asList(r.getImgs())), context);
        myroom_imgs_viewpager.setAdapter(adapter);
        worm_dots_indicator.setViewPager2(myroom_imgs_viewpager);
        if (r.getImgs().length != 0) {
            myroom_chooseimg_ImV.setVisibility(View.INVISIBLE);
            enterroom_edit_Mbtn.setVisibility(View.VISIBLE);
        }
        imageUris = new ArrayList<>();
    }
}