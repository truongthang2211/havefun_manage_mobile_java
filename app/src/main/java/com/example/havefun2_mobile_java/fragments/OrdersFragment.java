package com.example.havefun2_mobile_java.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.models.Order;
import com.example.havefun2_mobile_java.models.Room;
import com.example.havefun2_mobile_java.utils.MySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class OrdersFragment extends Fragment {
    String ServerURL ;
    String HotelID = "NfmlyyCa26QE0dtvdwmr";

    private Context context;
    private LinearLayout OrderLayout;
    private LinearLayout DayOfWeekLayout;
    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = container.getContext();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OrderLayout = view.findViewById(R.id.Order_Linear_Orderlist);
        DayOfWeekLayout = view.findViewById(R.id.Order_Linear_DayOfMonth);
        ServerURL = getString(R.string.server_address);
        FetchData();
        LoadDayOfMonth();
    }
    public LocalDate GetFirstDayOfWeek(){
        LocalDate t = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            t = LocalDate.now();
            if (t.getDayOfWeek().getValue() != 7)
                t=t.minusDays(t.getDayOfWeek().getValue());
        }

        return t;
    }
    private void LoadDayOfMonth(){
        LocalDate ThisSunday = GetFirstDayOfWeek();
        LayoutInflater inflater = LayoutInflater.from(this.context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            for (int i = 0; i<7;++i){
                View dayofmonth_layout = inflater.inflate(R.layout.order_dayofmonth_item, DayOfWeekLayout, false);
                LocalDate nextDate = ThisSunday.plusDays(i);
                MaterialButton DayBtn = dayofmonth_layout.findViewById(R.id.Order_Day_Button);
                String dayOfweek = String.valueOf(nextDate.getDayOfMonth());
                DayBtn.setText(dayOfweek);
                if (nextDate.getDayOfMonth() == LocalDate.now().getDayOfMonth()){
                    DayBtn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    DayBtn.setStrokeWidth(4);
                    DayBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

                    DayBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));

                }
                DayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClearBackgroundBtnDate();
                        DayBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        DayBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
                    }
                });
                DayOfWeekLayout.addView(dayofmonth_layout);
            }

        }


    }
    private void ClearBackgroundBtnDate(){
        int numChild = DayOfWeekLayout.getChildCount();
        for (int i = 0;i<numChild;++i){
            View v = DayOfWeekLayout.getChildAt(i);
            MaterialButton Daybtn = v.findViewById(R.id.Order_Day_Button);
            Daybtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.lightBlack)));

            Daybtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetItem(View view, Order o) {
        TextView roomId = view.findViewById(R.id.Order_Room_Tv_e);
        TextView orderTime = view.findViewById(R.id.Order_orderTime_Tv_e);
        TextView joinTime = view.findViewById(R.id.Order_Jointime_Tv_e);
        TextView leftTime = view.findViewById(R.id.Order_Leftime_Tv_e);
        TextView user = view.findViewById(R.id.Order_user_Tv_e);
        TextView status = view.findViewById(R.id.Order_status_Tv);
        MaterialButton Order_pay_Button = view.findViewById(R.id.Order_pay_Button);
        MaterialButton Order_Cancel_Button = view.findViewById(R.id.Order_Cancel_Button);

        if (o.getOrder_status().equals("complete")){
            status.setVisibility(View.VISIBLE);
            Order_pay_Button.setVisibility(View.INVISIBLE);
            Order_Cancel_Button.setVisibility(View.INVISIBLE);
        }else if (o.getOrder_status().equals("canceled")){
            status.setVisibility(View.VISIBLE);
            status.setText("Đã bị hủy");
            status.setTextColor(Color.parseColor("#8b0000"));
            Order_pay_Button.setVisibility(View.INVISIBLE);
            Order_Cancel_Button.setVisibility(View.INVISIBLE);
        }
        roomId.setText(o.getRoom().getRoom_id());
        orderTime.setText(o.getCreated_at().toString());
        joinTime.setText(o.getOrder_start().toString());
        leftTime.setText(o.getOrder_end().toString());
        user.setText(o.getUser().getPhone());


    }
    private void FetchData(){
        String orderURL = ServerURL + "/api/orders/hotel/" + HotelID;
        LayoutInflater inflater = LayoutInflater.from(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, orderURL, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        OrderLayout.removeAllViews();
                        JSONArray ordersJs = response.getJSONArray("data");
                        ArrayList<Order> Orderlist = new ArrayList<Order>();
                        for (int i= 0 ; i< ordersJs.length();++i){
                            Order o = new Gson().fromJson(ordersJs.get(i).toString(),Order.class);
                            Orderlist.add(o);
                        }
                        LayoutInflater inflater = LayoutInflater.from(context);
                        for (Order i : Orderlist) {
                            View oder_layout = inflater.inflate(R.layout.order_card_item, OrderLayout, false);
                            SetItem(oder_layout, i);
                            OrderLayout.addView(oder_layout);
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
}