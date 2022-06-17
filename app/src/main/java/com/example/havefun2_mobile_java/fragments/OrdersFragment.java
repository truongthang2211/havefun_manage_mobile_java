package com.example.havefun2_mobile_java.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterPromotionActivity;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.models.Order;
import com.example.havefun2_mobile_java.models.Room;
import com.example.havefun2_mobile_java.models.Timestamp;
import com.example.havefun2_mobile_java.utils.MySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class OrdersFragment extends Fragment {
    String ServerURL;
    String HotelID = "NfmlyyCa26QE0dtvdwmr";
    ArrayList<Order> Orderlist;
    private Context context;
    private LinearLayout OrderLayout;
    private LinearLayout DayOfWeekLayout;
    private MaterialCardView Order_DatePicker_CardView;
    private TextView Order_DatePicker_Date_Tv;
    final Calendar myCalendar = Calendar.getInstance();

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
        Order_DatePicker_Date_Tv = view.findViewById(R.id.Order_DatePicker_Date_Tv);
        Order_DatePicker_CardView = view.findViewById(R.id.Order_DatePicker_CardView);
        OrderLayout = view.findViewById(R.id.Order_Linear_Orderlist);
        DayOfWeekLayout = view.findViewById(R.id.Order_Linear_DayOfMonth);
        ServerURL = getString(R.string.server_address);

        FetchData(LocalDate.now());
        setDatePicker();
    }

    public LocalDate GetFirstDayOfWeek(LocalDate d) {
        LocalDate t = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            t = d;
            if (t.getDayOfWeek().getValue() != 7)
                t = t.minusDays(t.getDayOfWeek().getValue());
        }

        return t;
    }

    private void LoadDayOfMonth(LocalDate d) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate ThisSunday = GetFirstDayOfWeek(d);
            DayOfWeekLayout.removeAllViews();
            for (int i = 0; i < 7; ++i) {
                View dayofmonth_layout = inflater.inflate(R.layout.order_dayofmonth_item, DayOfWeekLayout, false);
                LocalDate nextDate = ThisSunday.plusDays(i);
                MaterialButton DayBtn = dayofmonth_layout.findViewById(R.id.Order_Day_Button);
                String dayOfweek = String.valueOf(nextDate.getDayOfMonth());
                DayBtn.setText(dayOfweek);
                if (nextDate.getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                    DayBtn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    DayBtn.setStrokeWidth(4);
                }
                if (nextDate.getDayOfMonth() == myCalendar.get(Calendar.DAY_OF_MONTH)) {
                    ClearBackgroundBtnDate();
                    DayBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    DayBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
                    RenderOrders(nextDate);
                }
                DayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClearBackgroundBtnDate();
                        DayBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        DayBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
                        RenderOrders(nextDate);
                    }
                });
                DayOfWeekLayout.addView(dayofmonth_layout);
            }

        }


    }

    private void ClearBackgroundBtnDate() {
        int numChild = DayOfWeekLayout.getChildCount();
        for (int i = 0; i < numChild; ++i) {
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

        if (o.getOrder_status().equals("complete")) {
            status.setVisibility(View.VISIBLE);
            Order_pay_Button.setVisibility(View.INVISIBLE);
            Order_Cancel_Button.setVisibility(View.INVISIBLE);
        } else if (o.getOrder_status().equals("canceled")) {
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
        Order_Cancel_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn có muốn hủy đơn đặt phòng này?")
                        .setConfirmText("Hủy!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                UpdateOrder("canceled", o.getId());

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("en", "EN"));

        Order_pay_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn có muốn thanh toán đơn đặt phòng này với giá "+ currencyFormatter.format(o.getTotal_price_estimate()) + " đ")
                        .setConfirmText("Thanh toán!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                UpdateOrder("complete", o.getId());
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
    }

    private void UpdateOrder(String status, String orderId) {
        String orderURL = ServerURL + "/api/orders/update";
        JSONObject putOrder = new JSONObject();
        try {
            putOrder.put("order_status", status);
            putOrder.put("id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, orderURL, putOrder, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        long SecondPicked = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
                        LocalDate localDatePicked = Instant.ofEpochSecond(SecondPicked).atZone(ZoneId.systemDefault()).toLocalDate();
                        FetchData(localDatePicked);


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

    private void FetchData(LocalDate d) {
        String orderURL = ServerURL + "/api/orders/hotel/" + HotelID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, orderURL, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int status = response.getInt("status");

                    if (status == 200) {
                        OrderLayout.removeAllViews();
                        JSONArray ordersJs = response.getJSONArray("data");
                        Orderlist = new ArrayList<Order>();
                        for (int i = 0; i < ordersJs.length(); ++i) {
                            Order o = new Gson().fromJson(ordersJs.get(i).toString(), Order.class);
                            Orderlist.add(o);
                        }
                        LoadDayOfMonth(d);


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
    private void RenderOrders(LocalDate date) {
        OrderLayout.removeAllViews();
        ArrayList<Order> filteredOrder = new ArrayList<>(Orderlist);
        filteredOrder.removeIf(new Predicate<Order>() {
            @Override
            public boolean test(Order order) {
                boolean check1 = order.getOrder_end().toLocalDateTime().getDayOfMonth() != date.getDayOfMonth();
                boolean check2 = order.getOrder_end().toLocalDateTime().getMonthValue() != date.getMonthValue();
                boolean check3 = order.getOrder_end().toLocalDateTime().getYear() != date.getYear();
                return check1 || check2 || check3;
            }
        });
        LayoutInflater inflater = LayoutInflater.from(context);
        for (Order i : filteredOrder) {
            View oder_layout = inflater.inflate(R.layout.order_card_item, OrderLayout, false);
            SetItem(oder_layout, i);
            OrderLayout.addView(oder_layout);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDatePicker() {
        DatePickerDialog.OnDateSetListener setDateWeek = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            long SecondPicked = LocalDateTime.ofInstant(myCalendar.toInstant(), myCalendar.getTimeZone().toZoneId()).atZone(ZoneId.systemDefault()).toEpochSecond();
            LocalDate localDatePicked = Instant.ofEpochSecond(SecondPicked).atZone(ZoneId.systemDefault()).toLocalDate();
            Order_DatePicker_Date_Tv.setText(new Timestamp(SecondPicked, 0).toString().split(" ")[0]);
            LoadDayOfMonth(localDatePicked);

        };
        Order_DatePicker_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, setDateWeek, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Order_DatePicker_Date_Tv.setText(new Timestamp(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), 0).toString().split(" ")[0]);
    }
}