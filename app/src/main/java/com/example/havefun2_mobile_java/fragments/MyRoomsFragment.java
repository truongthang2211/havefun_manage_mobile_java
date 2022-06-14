package com.example.havefun2_mobile_java.fragments;

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
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.databinding.FragmentMyroomsBinding;
import com.example.havefun2_mobile_java.models.Room;
import com.example.havefun2_mobile_java.utils.MySingleton;
import com.example.havefun2_mobile_java.viewmodels.DashboardViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MyRoomsFragment extends Fragment {
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
        listRoomLinear = view.findViewById(R.id.myroom_listroom_linear);
        addroom_card = view.findViewById(R.id.myroom_addroom_card);
        addroom_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EnterRoomActivity.class);
                startActivity(intent);
            }
        });
        String ServerURL = getString(R.string.server_address);
        String HotelID = "NfmlyyCa26QE0dtvdwmr";
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        Picasso.get().load(room.getImgs()[0]).into(img);
        name.setText(room.getName());
        roomtype.setText(room.getRoom_type());
        roomid.setText(room.getRoom_id());
        hour.setText(currencyFormatter.format(room.getHour_price())+ " đ");
        overnight.setText(currencyFormatter.format(room.getOvernight_price())+ " đ");
        daily.setText(currencyFormatter.format(room.getDaily_price())+ " đ");
    }
}