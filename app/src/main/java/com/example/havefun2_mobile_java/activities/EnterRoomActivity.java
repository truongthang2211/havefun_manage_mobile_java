package com.example.havefun2_mobile_java.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.adapters.SliderImgsAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.AbstractList;
import java.util.ArrayList;

public class EnterRoomActivity extends AppCompatActivity {
    Context context;
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

    Button btn_Them;
    Button btn_Huy;

    ViewPager2 myroom_imgs_viewpager;
    WormDotsIndicator worm_dots_indicator;
    ImageView myroom_chooseimg_ImV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_room);

        context=this;
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
        chk_30m2 = findViewById(R.id.chk_30m2);
        chk_CuaSo = findViewById(R.id.chk_CuaSo);
        chk_SanGo = findViewById(R.id.chk_SanGo);
        chk_Wifi = findViewById(R.id.chk_Wifi);
        chk_TV = findViewById(R.id.chk_TV);
        chk_LeTan = findViewById(R.id.chk_LeTan);
        chk_MayLanh = findViewById(R.id.chk_MayLanh);
        chk_ThangMay = findViewById(R.id.chk_ThangMay);
        chk_TruyenHinhCap = findViewById(R.id.chk_TruyenHinhCap);

        btn_Them= findViewById(R.id.btn_Them);
        btn_Huy= findViewById(R.id.btn_Huy);
        btn_Huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        worm_dots_indicator = findViewById(R.id.worm_dots_indicator);
        myroom_imgs_viewpager= findViewById(R.id.myroom_imgs_viewpager);
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

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data!=null){
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
                SliderImgsAdapter adapter = new SliderImgsAdapter(context,imageUris);
                myroom_imgs_viewpager.setAdapter(adapter);
                worm_dots_indicator.setViewPager2(myroom_imgs_viewpager);
                if (imageUris.size()!=0){
                    myroom_chooseimg_ImV.setVisibility(View.INVISIBLE);
                }
            }

        }
    }
}