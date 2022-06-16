package com.example.havefun2_mobile_java.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.databinding.FragmentMyhotelBinding;
import com.example.havefun2_mobile_java.models.Hotel;
import com.example.havefun2_mobile_java.models.Location;
import com.example.havefun2_mobile_java.utils.PathUtil;
import com.example.havefun2_mobile_java.utils.VolleyMultipartRequest;
import com.example.havefun2_mobile_java.utils.WebServiceAPI;
import com.example.havefun2_mobile_java.viewmodels.HomeViewModel;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import me.relex.circleindicator.CircleIndicator3;

public class MyHotelFragment extends Fragment {
    public static final int PICK_IMAGE = 1;
    private EditText hotelName,hotelDesc,hotelAddress,hotelCity,hotelDistrict;
    private ArrayList<Uri> imageUris;
    private Button save;
    private ViewPager2 mviewPager2;
    //    private CircleIndicator3 mcircleIndicator3;
    private FragmentMyhotelBinding binding;
    ImageView hotel_img;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentMyhotelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hotelName = view.findViewById(R.id.hotel_name_mng);
        hotelAddress = view.findViewById(R.id.hotel_addr_mng);
        hotelDistrict = view.findViewById(R.id.hotel_district_mng);
        hotelCity = view.findViewById(R.id.hotel_city_mng);
        hotelDesc = view.findViewById(R.id.hotel_desc_mng);


        imageUris = new ArrayList<>();
        save = view.findViewById(R.id.luu_btn);
        hotel_img = view.findViewById(R.id.hotel_pic_iv);
        hotel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ServerURL = getString(R.string.server_address);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerURL).addConverterFactory(GsonConverterFactory.create()).build();
                WebServiceAPI uploadService = retrofit.create(WebServiceAPI.class);
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                Hotel postHotel = new Hotel();
                postHotel.setName(hotelName.getText().toString());
                postHotel.setDescription(hotelDesc.getText().toString());
                Location hotelLocation = new Location();
                hotelLocation.setAddress(hotelAddress.getText().toString());
                hotelLocation.setCity(hotelCity.getText().toString());
                hotelLocation.setDistrict(hotelDistrict.getText().toString());
                postHotel.setLocation(hotelLocation);
                builder.addFormDataPart("data", new Gson().toJson(postHotel));

                for (int i = 0; i < imageUris.size(); i++) {
                    try {
                        String stringPath = PathUtil.getPath(getContext(),imageUris.get(i));
                        File file = new File(stringPath);
                        builder.addFormDataPart("imgs", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                MultipartBody requestBody = builder.build();
                Call call = uploadService.CreateHotel(requestBody);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, retrofit2.Response response) {
                        try {
                            JSONObject respondOb = new JSONObject(new Gson().toJson(response.body()));
                            JSONObject hotelJs = respondOb.getJSONObject("data");
                            Hotel t = new Gson().fromJson(hotelJs.toString(),Hotel.class);

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
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
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
            hotel_img.setImageURI(imageUris.get(0));
        }
    }

}