package com.example.havefun2_mobile_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.havefun2_mobile_java.R;

public class PhotoFragment extends Fragment {

    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_photo,container,false);

        Bundle bundle = getArguments();
//        Photo photo = (Photo) bundle.get("object_photo");

        ImageView imgPhoto= mView.findViewById(R.id.img_photo);
//        imgPhoto.setImageResource(photo.getResourceid());

        return mView;
    }
}
