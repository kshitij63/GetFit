package com.example.user.getfit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 7/7/2017.
 */

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymHolder> {
Context context;
    ArrayList<Gym> gymArrayList;

    GymAdapter(Context context, ArrayList<Gym> gymArrayList){
        this.gymArrayList=gymArrayList;
        this.context=context;
    }

    @Override
    public GymHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.gymlayout,parent,false);
        GymHolder holder =new GymHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GymHolder holder, int position) {
holder.name.setText(gymArrayList.get(position).getName());
if(gymArrayList.get(position).getStatus()!=null){
    if(gymArrayList.get(position).getStatus()){
        holder.status.setText("open");
        holder.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));

    }
    else{
        holder.status.setText("close");
    }

}
else {
    holder.status.setText("N/A");
}
        holder.vic.setText(gymArrayList.get(position).getVicinity());
        //Picasso.with(context).load(gymArrayList.get(position).getPhot_ref()).into(holder.imageView);
        setImage(gymArrayList.get(position).getPhot_ref(),holder.imageView);
        holder.rating.setText(gymArrayList.get(position).getRating());
        if(!gymArrayList.get(position).getRating().equals("N/A")) {
            Float rate = Float.valueOf(gymArrayList.get(position).getRating());

            holder.bar.setRating(rate);
        }

    }

    @Override
    public int getItemCount() {
        return gymArrayList.size();
    }

     class GymHolder extends RecyclerView.ViewHolder{
ImageView imageView;
         TextView name;
         TextView rating;
         RatingBar bar;
         TextView vic;
         TextView status;

        public GymHolder(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.photo);
            name=(TextView) itemView.findViewById(R.id.name_gym);
            rating=(TextView) itemView.findViewById(R.id.text_rating);
            bar=(RatingBar) itemView.findViewById(R.id.rating_bar);
            vic=(TextView) itemView.findViewById(R.id.vicinity);
            status=(TextView) itemView.findViewById(R.id.status);


        }
    }
    public void setImage(String ref,ImageView view){
        if(ref!="N/A") {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + ref + "&key=" + context.getResources().getString(R.string.map_api_key);
        Picasso.with(context).load(url).into(view);
        }
        }


}
