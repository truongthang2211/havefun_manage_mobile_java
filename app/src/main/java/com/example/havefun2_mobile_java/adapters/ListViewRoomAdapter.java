package com.example.havefun2_mobile_java.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.havefun2_mobile_java.R;
import com.example.havefun2_mobile_java.activities.EnterRoomActivity;
import com.example.havefun2_mobile_java.models.Room;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ListViewRoomAdapter extends ArrayAdapter<Room> {
    private int resourceLayout;
    private Context mContext;
    private Activity activity;

    public ListViewRoomAdapter(Activity activity,Context context, int resource, ArrayList<Room> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(resourceLayout, null);
        }

        Room room = getItem(position);

        if (room != null) {
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,EnterRoomActivity.class);
                intent.putExtra("room",new Gson().toJson(room));
                activity.startActivityForResult(intent,1);
            }
        });
        return view;
    }
}
