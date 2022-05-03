package com.example.havefun2_mobile_java.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.models.Account;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserHostFragment extends Fragment {
    private Context context;
    private  LinearLayout AccountLayout;
    public UserHostFragment() {
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
        return inflater.inflate(R.layout.fragment_user_host, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccountLayout = view.findViewById(R.id.list_account);
        LoadAccountItem();
    }
    private void LoadAccountItem() {


        ArrayList<Account> Accountlist = new ArrayList<Account>();
        Accountlist.add(new Account("ID01", "Nguyễn Văn Tèo", "999 Roses", true));
        Accountlist.add(new Account("ID02", "Đào Quốc Tuấn", "999 Roses", false));
        Accountlist.add(new Account("ID03", "Lưu Minh Tài", "Vũ Điệu Xanh", true));
        Accountlist.add(new Account("ID04", "Nguyễn Minh Thư", "Vũ Điệu Xanh", false));
        Accountlist.add(new Account("ID05", "Lê Anh Vũ", "Mèo Con", true));
        LayoutInflater inflater = LayoutInflater.from(this.context);
        for (Account i : Accountlist) {
            View account_layout = inflater.inflate(R.layout.item_account, AccountLayout, false);
            SetItem(account_layout, i);
            AccountLayout.addView(account_layout);
        }
    }

    private void SetItem(View view, Account a) {
        TextView accID = view.findViewById(R.id.tv_name);
        TextView hotelName = view.findViewById(R.id.tv_hotel);
        TextView isMng = view.findViewById(R.id.tv_pos);
        accID.setText(a.getFullName());
        hotelName.setText(a.getHotelName());
        if(a.isManager()){
            isMng.setText("Quản lý");
        }else{
            isMng.setText("Nhân viên");
        }
    }
}