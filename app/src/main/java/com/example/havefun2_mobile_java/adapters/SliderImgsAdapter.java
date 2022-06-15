package com.example.havefun2_mobile_java.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.havefun2_mobile_java.R;

import java.util.ArrayList;

public class SliderImgsAdapter extends RecyclerView.Adapter<SliderImgsAdapter.ViewHolder> {
    ArrayList<Uri> imgUris;
    Context context;

    public SliderImgsAdapter( Context context,ArrayList<Uri> imgUris) {
        this.imgUris = imgUris;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_imgs_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = imgUris.get(position);
        holder.slide_img.setImageURI(uri);

    }

    @Override
    public int getItemCount() {
        if (imgUris == null)
            return 0;
        return imgUris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView slide_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slide_img = itemView.findViewById(R.id.slider_img);

        }
    }
}
