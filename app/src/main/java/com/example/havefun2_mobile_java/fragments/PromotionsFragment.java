package com.example.havefun2_mobile_java.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterPromotionActivity;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.databinding.FragmentMyroomsBinding;
import com.example.havefun2_mobile_java.databinding.FragmentPromotionsBinding;
import com.example.havefun2_mobile_java.models.Promotion;
import com.example.havefun2_mobile_java.models.Room;
import com.example.havefun2_mobile_java.utils.MySingleton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PromotionsFragment extends Fragment {
    MaterialCardView promotion_addroom_card;
    String ServerURL ;
    String HotelID = "NfmlyyCa26QE0dtvdwmr";
    LinearLayout listProLinear ;
    ArrayList<Promotion> listpro;
    private FragmentPromotionsBinding binding;
    Context context;
    public PromotionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = container.getContext();
        binding = FragmentPromotionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ServerURL= getString(R.string.server_address);
        listProLinear = view.findViewById(R.id.mypromotion_listpro_linear);

        promotion_addroom_card = view.findViewById(R.id.promotion_addroom_card);
        promotion_addroom_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EnterPromotionActivity.class);
                startActivityForResult(intent,1);

            }
        });
        FetchData();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                if (result.equals("addnew")||result.equals("edit")){
                    FetchData();
                }
            }

        }
    }
    private void FetchData(){
        listProLinear.removeAllViews();
        String getHotelUrl = ServerURL+ "/api/promotions/" + HotelID;
        LayoutInflater inflater = LayoutInflater.from(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getHotelUrl, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        JSONArray promotionsData = response.getJSONArray("data");
                        listpro = new ArrayList<>();
                        for (int i = 0 ; i< promotionsData.length();i++){
                            Promotion r = new Gson().fromJson(promotionsData.get(i).toString(),Promotion.class);
                            listpro.add(r);
                        }
                        for (Promotion r : listpro){
                            View view = inflater.inflate(R.layout.promotion_normal_card, listProLinear, false);
                            AddProToCardd(view,r);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), EnterPromotionActivity.class);
                                    intent.putExtra("promotion",new Gson().toJson(r));
                                    startActivityForResult(intent,1);
                                }
                            });
                            view.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Bạn có muốn xóa ưu đãi này?")
                                            .setConfirmText("Xóa!")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    DeleteRoom(r.getId());
                                                    sDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();
                                    return true;
                                }
                            });
                            listProLinear.addView(view);
                        }
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

    private void DeleteRoom(String promotionID) {
        String getProURL = ServerURL+ "/api/promotions/delete/"+ HotelID + "/" + promotionID;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, getProURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        FetchData();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void AddProToCardd(View view, Promotion r) {
        ImageView promotion_norcard_img = view.findViewById(R.id.promotion_norcard_img);
        TextView promotion_norcard_name_Tv = view.findViewById(R.id.promotion_norcard_name_Tv);
        TextView promotion_norcard_ratio_Tv = view.findViewById(R.id.promotion_norcard_ratio_Tv);
        TextView promotion_norcard_timestart_Tv = view.findViewById(R.id.promotion_norcard_timestart_Tv);
        TextView promotion_norcard_timeend_Tv = view.findViewById(R.id.promotion_norcard_timeend_Tv);
        NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("en", "EN"));
        if (r.getImg()!=null){
            Picasso.get().load(r.getImg()).into(promotion_norcard_img);
        }
        promotion_norcard_name_Tv.setText(r.getName());
        promotion_norcard_ratio_Tv.setText(currencyFormatter.format(r.getDiscount_ratio()*100)+"%");
        promotion_norcard_timestart_Tv.setText(r.getTime_start().toString());
        promotion_norcard_timeend_Tv.setText(r.getTime_end().toString());
    }
}
