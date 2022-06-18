package com.example.havefun2_mobile_java.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.adapters.SliderImgsAdapter;
import com.example.havefun2_mobile_java.databinding.FragmentMyhotelBinding;
import com.example.havefun2_mobile_java.models.HostUser;
import com.example.havefun2_mobile_java.models.Hotel;
import com.example.havefun2_mobile_java.models.Location;
import com.example.havefun2_mobile_java.models.Room;
import com.example.havefun2_mobile_java.utils.MySingleton;
import com.example.havefun2_mobile_java.utils.PathUtil;
import com.example.havefun2_mobile_java.utils.VolleyMultipartRequest;
import com.example.havefun2_mobile_java.utils.WebServiceAPI;
import com.example.havefun2_mobile_java.viewmodels.HomeViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
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
    String ServerURL;
    private ViewPager2 mviewPager2;
    //    private CircleIndicator3 mcircleIndicator3;
    private FragmentMyhotelBinding binding;
    Hotel CurrentHotel;
    HostUser CurrentHostUser;
    ViewPager2 myhotel_imgs_viewpager;
    WormDotsIndicator myhotel_worm_dots_indicator;
    ImageView myhotel_chooseimg_ImV;
    MaterialButton myhotel_edit_Mbtn;
    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentMyhotelBinding.inflate(inflater, container, false);
        context =  container.getContext();;
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ServerURL = getString(R.string.server_address);
        hotelName = view.findViewById(R.id.hotel_name_mng);
        hotelAddress = view.findViewById(R.id.hotel_addr_mng);
        hotelDistrict = view.findViewById(R.id.hotel_district_mng);
        hotelCity = view.findViewById(R.id.hotel_city_mng);
        hotelDesc = view.findViewById(R.id.hotel_desc_mng);

        myhotel_imgs_viewpager = view.findViewById(R.id.myhotel_imgs_viewpager);
        myhotel_worm_dots_indicator = view.findViewById(R.id.myhotel_worm_dots_indicator);
        myhotel_chooseimg_ImV = view.findViewById(R.id.myhotel_chooseimg_ImV);
        myhotel_edit_Mbtn = view.findViewById(R.id.myhotel_edit_Mbtn);

        imageUris = new ArrayList<>();
        save = view.findViewById(R.id.luu_btn);
        myhotel_chooseimg_ImV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE);
            }
        });
        myhotel_edit_Mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE);
            }
        });
        SetOnlickSaveBtn();

    }
    private void SetOnlickSaveBtn(){
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("User", 0);
        String name = pref.getString("hostuserObject", "undefined");
        if (!name.equals("undefined")){
            CurrentHostUser = new Gson().fromJson(name,HostUser.class);
            if (CurrentHostUser.getHotel_id()!=null){
                FetchDataHotel(CurrentHostUser.getHotel_id());
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Hotel editHotel = GetHotelFromUI();
                        editHotel.setId(CurrentHotel.getId());
                        onSubmit("edit",editHotel);
                    }
                });
            }else{
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onSubmit("addnew",GetHotelFromUI());
                    }
                });
            }
        }else{
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Có lỗi xảy ra, hãy thử đăng xuất và đăng nhập lại")
                    .show();
        }
    }
    private void FetchDataHotel(String hotel_id) {
        String HotelURL = ServerURL + "/api/hotels/" + hotel_id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, HotelURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        JSONObject HotelData = response.getJSONObject("data");
                        Hotel hotel = new Gson().fromJson(HotelData.toString(),Hotel.class);
                        RenderHotel(hotel);
                        CurrentHotel = hotel;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.i("apiapi", error.toString());
            }
        });
        MySingleton.getInstance(this.context).addToRequestQueue(request);
    }

    private void RenderHotel(Hotel hotel) {
        hotelName.setText(hotel.getName());
        hotelAddress.setText(hotel.getLocation().getAddress());
        hotelCity.setText(hotel.getLocation().getCity());
        hotelDesc.setText(hotel.getDescription());
        hotelDistrict.setText(hotel.getLocation().getDistrict());

        ArrayList<Uri> uris = new ArrayList<>();
        if (hotel.getImgs() !=  null){
            for (String url : hotel.getImgs()){
                uris.add(Uri.parse(url));
            }
            SliderImgsAdapter adapter = new SliderImgsAdapter(new ArrayList<String>(Arrays.asList(hotel.getImgs())),context);
            myhotel_imgs_viewpager.setAdapter(adapter);
            myhotel_worm_dots_indicator.setViewPager2(myhotel_imgs_viewpager);
            if (hotel.getImgs().length!=0){
                myhotel_chooseimg_ImV.setVisibility(View.INVISIBLE);
                myhotel_edit_Mbtn.setVisibility(View.VISIBLE);
            }
        }



        imageUris = new ArrayList<>();
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
            SliderImgsAdapter adapter = new SliderImgsAdapter(context,imageUris);
            myhotel_imgs_viewpager.setAdapter(adapter);
            myhotel_worm_dots_indicator.setViewPager2(myhotel_imgs_viewpager);
            if (imageUris.size()!=0){
                myhotel_chooseimg_ImV.setVisibility(View.INVISIBLE);
                myhotel_edit_Mbtn.setVisibility(View.VISIBLE);
            }
        }
    }
    private Hotel GetHotelFromUI(){
        Hotel postHotel = new Hotel();
        postHotel.setName(hotelName.getText().toString());
        postHotel.setDescription(hotelDesc.getText().toString());
        Location hotelLocation = new Location();
        hotelLocation.setAddress(hotelAddress.getText().toString());
        hotelLocation.setCity(hotelCity.getText().toString());
        hotelLocation.setDistrict(hotelDistrict.getText().toString());
        postHotel.setLocation(hotelLocation);
        return postHotel;
    }
    private void onSubmit(String option,Hotel h){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ServerURL).addConverterFactory(GsonConverterFactory.create()).build();
        WebServiceAPI uploadService = retrofit.create(WebServiceAPI.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (option.equals("addnew")){
            JSONObject postData = new JSONObject();
            try {
                postData.put("hotel",new JSONObject(new Gson().toJson(h)));
                postData.put("hostuserId",CurrentHostUser.getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            builder.addFormDataPart("data", postData.toString());
        }else {
            builder.addFormDataPart("data", new Gson().toJson(h));

        }

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
        Call call = option.equals("addnew")? uploadService.CreateHotel(requestBody):uploadService.EditHotel(requestBody);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                try {
                    JSONObject respondOb = new JSONObject(new Gson().toJson(response.body()));
                    JSONObject hotelJs = respondOb.getJSONObject("data");
                    Hotel t = new Gson().fromJson(hotelJs.toString(),Hotel.class);
                    RenderHotel(t);

                    CurrentHostUser.setHotel_id(t.getId());
                    SharedPreferences pref =getActivity().getApplicationContext().getSharedPreferences("User", 0);
                    SharedPreferences.Editor edit = pref.edit();
                    String CurrentHostUserObj = pref.getString("hostuserObject", "undefined");
                    if (!CurrentHostUserObj.equals("undefined")){
                        JSONObject obj = new JSONObject(CurrentHostUserObj);
                        obj.put("hotel_id",t.getId());
                        edit.putString("hostuserObject",obj.toString());
                        edit.apply();
                        SetOnlickSaveBtn();
                    }
                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Thành công")
                            .show();

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