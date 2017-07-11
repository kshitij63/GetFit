package com.example.user.getfit;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Value;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 7/7/2017.
 */

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymHolder> {
    Context context;
    AlertDialog dialog;
    int code;
    ArrayList<Gym> gymArrayList;

    GymAdapter(Context context, ArrayList<Gym> gymArrayList,int code) {
        this.gymArrayList = gymArrayList;
        this.context = context;
        this.code=code;
    }

    @Override
    public GymHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gymlayout, parent, false);
        GymHolder holder = new GymHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GymHolder holder, int position) {
        final ContentValues values = new ContentValues();

        holder.name.setText(gymArrayList.get(position).getName());
        if (code == 1008) {
            values.put(GymContract.SavedGyms.NAME, gymArrayList.get(position).getName());
        }
        if (code == 1008) {

            if (gymArrayList.get(position).getStatus() != null) {
                if (gymArrayList.get(position).getStatus()) {
                    holder.status.setText(context.getResources().getString(R.string.open));
                    values.put(GymContract.SavedGyms.STATUS, context.getResources().getString(R.string.open));

                    holder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                } else {
                    holder.status.setText(context.getResources().getString(R.string.close));
                    values.put(GymContract.SavedGyms.STATUS, context.getResources().getString(R.string.close));

                }

            } else {
                holder.status.setText(context.getResources().getString(R.string.NA));

                values.put(GymContract.SavedGyms.STATUS, context.getResources().getString(R.string.NA));
            }
        } else {
            holder.status.setText(gymArrayList.get(position).getStatus_string());

        }
        holder.vic.setText(gymArrayList.get(position).getVicinity());
        if (code == 1008) {
            values.put(GymContract.SavedGyms.VICINITY, gymArrayList.get(position).getVicinity());
        }
        //Picasso.with(context).load(gymArrayList.get(position).getPhot_ref()).into(holder.imageView);
        setImage(gymArrayList.get(position).getPhot_ref(), holder.imageView);
        holder.rating.setText(gymArrayList.get(position).getRating());
        if (code == 1008)
            values.put(GymContract.SavedGyms.RATING, gymArrayList.get(position).getRating());


        if (!gymArrayList.get(position).getRating().equals(context.getResources().getString(R.string.NA))) {
            Float rate = Float.valueOf(gymArrayList.get(position).getRating());

            holder.bar.setRating(rate);
        }
        if (code == 1008) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context).setMessage(context.getResources().getString(R.string.Add_to_favourites)).setPositiveButton(context.getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = context.getContentResolver().insert(GymContract.SavedGyms.CONTENT_URI, values);
                            if (!uri.toString().equals("")) {
                                Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                            }
                            Log.e("Uri entry", GymContract.SavedGyms.CONTENT_URI.toString());
                        }
                    }).setNegativeButton(context.getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return gymArrayList.size();
    }

    class GymHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        TextView rating;
        RatingBar bar;
        TextView vic;
        TextView status;

        public GymHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo);
            name = (TextView) itemView.findViewById(R.id.name_gym);
            rating = (TextView) itemView.findViewById(R.id.text_rating);
            bar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            vic = (TextView) itemView.findViewById(R.id.vicinity);
            status = (TextView) itemView.findViewById(R.id.status);


        }
    }

    public void setImage(String ref, ImageView view) {
        if (ref != context.getResources().getString(R.string.NA)) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + ref + "&key=" + context.getResources().getString(R.string.map_api_key);
            Picasso.with(context).load(url).into(view);
        }
    }


}
