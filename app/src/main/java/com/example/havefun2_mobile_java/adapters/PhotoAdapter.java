package com.example.havefun2_mobile_java.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.models.Photo;

import java.util.List;

public class PhotoAdapter  extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Photo> listPhoto;

    public PhotoAdapter(List<Photo> listPhoto) {
        this.listPhoto = listPhoto;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo =listPhoto.get(position);
        if(photo ==null){
            return;
        }
        holder.imageView.setImageResource(photo.getResourceid());
    }

    @Override
    public int getItemCount() {
        if(listPhoto!= null){
            return listPhoto.size();
        }
        return 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public PhotoViewHolder (@NonNull View itemView){
            super(itemView);
            imageView= itemView.findViewById(R.id.photo_item);
        }
    }
}
