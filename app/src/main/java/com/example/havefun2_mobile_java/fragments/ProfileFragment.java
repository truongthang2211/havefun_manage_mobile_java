package com.example.havefun2_mobile_java.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.LoginActivity;
import com.example.havefun2_mobile_java.activities.MainActivity;
import com.example.havefun2_mobile_java.databinding.FragmentProfileBinding;
import com.example.havefun2_mobile_java.models.HostUser;
import com.google.gson.Gson;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Context context;
    TextView usermail_txt;
    Button logout_btn;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.context = container.getContext();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usermail_txt = view.findViewById(R.id.usermail_txt);
        logout_btn = view.findViewById(R.id.logout_btn);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("User", 0);
        String name = pref.getString("hostuserObject", "undefined");
        if (!name.equals("undefined")){
            HostUser hostuser = new Gson().fromJson(name,HostUser.class);

            usermail_txt.setText(hostuser.getEmail());
            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pref.edit().remove("hostuserObject").commit();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }else{
            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Có lỗi xảy ra, hãy thử đăng xuất và đăng nhập lại")
                    .show();
        }


    }
}