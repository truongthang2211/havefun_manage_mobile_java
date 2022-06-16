package com.example.havefun2_mobile_java.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.databinding.FragmentMyroomsBinding;
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


public class MyRoomsFragment extends Fragment {
    String ServerURL ;
    String HotelID = "NfmlyyCa26QE0dtvdwmr";
    LinearLayout listRoomLinear ;
    Context context;
    ArrayList<Room> listroom;
    MaterialCardView addroom_card;
    private FragmentMyroomsBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.context = container.getContext();
        binding = FragmentMyroomsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ServerURL= getString(R.string.server_address);
        listRoomLinear = view.findViewById(R.id.myroom_listroom_linear);
        addroom_card = view.findViewById(R.id.myroom_addroom_card);
        addroom_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EnterRoomActivity.class);
                startActivityForResult(intent,1);
            }
        });
        FetchData();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
    private void AddRoomToCardd(View view, Room room){
        TextView name = view.findViewById(R.id.room_norcard_name_Tv);
        TextView roomtype = view.findViewById(R.id.room_norcard_roomtpe_Tv);
        TextView roomid = view.findViewById(R.id.room_norcard_roomid_Tv);
        TextView hour = view.findViewById(R.id.room_norcard_hour_Tv);
        TextView overnight = view.findViewById(R.id.room_norcard_overnight_Tv);
        TextView daily = view.findViewById(R.id.room_norcard_daily_Tv);
        ImageView img = view.findViewById(R.id.room_norcard_img);
        NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("en", "EN"));
        if (room.getImgs()!=null && room.getImgs().length>0){
            Picasso.get().load(room.getImgs()[0]).into(img);
        }
        name.setText(room.getName());
        roomtype.setText(room.getRoom_type());
        roomid.setText(room.getRoom_id());
        hour.setText(currencyFormatter.format(room.getHour_price())+ " đ");
        overnight.setText(currencyFormatter.format(room.getOvernight_price())+ " đ");
        daily.setText(currencyFormatter.format(room.getDaily_price())+ " đ");
    }
    private void FetchData(){
        listRoomLinear.removeAllViews();
        String getHotelUrl = ServerURL+ "/api/hotels/" + HotelID;
        LayoutInflater inflater = LayoutInflater.from(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getHotelUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        JSONObject HotelData = response.getJSONObject("data");
                        JSONArray roomsData = HotelData.getJSONArray("rooms");
                        listroom = new ArrayList<>();
                        for (int i = 0 ; i< roomsData.length();i++){
                            Room r = new Gson().fromJson(roomsData.get(i).toString(),Room.class);
                            listroom.add(r);
                        }
                        for (Room r : listroom){
                            View view = inflater.inflate(R.layout.room_normal_card, listRoomLinear, false);
                            AddRoomToCardd(view,r);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(),EnterRoomActivity.class);
                                    intent.putExtra("room",new Gson().toJson(r));
                                    startActivityForResult(intent,1);
                                }
                            });
                            view.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Bạn có muốn xóa phòng này?")
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
                            listRoomLinear.addView(view);
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
    private void DeleteRoom (String roomID){

        String getHotelUrl = ServerURL+ "/api/hotels/deleteroom/"+ HotelID + "/" + roomID;

        JSONObject reqBody = new JSONObject();
        try {
            reqBody.put("hotel_id",HotelID);
            reqBody.put("room_id",roomID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, getHotelUrl, null, new Response.Listener<JSONObject>() {
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
}