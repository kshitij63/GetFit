package com.example.user.getfit;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by user on 7/7/2017.
 */

public class BodyPartAdapter extends ArrayAdapter<BodyPart> {
    ArrayList<BodyPart> bodyPartArrayList;

    public BodyPartAdapter(@NonNull Context context, ArrayList<BodyPart> bodyPartArrayList) {
        super(context, R.layout.bodypart);
        this.bodyPartArrayList = bodyPartArrayList;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bodypart, parent, false);
        }
        if (bodyPartArrayList.get(position).getImage() != null && bodyPartArrayList.get(position).getBody_part_name() != null) {
            View view = convertView.findViewById(R.id.grad);
            view.setVisibility(View.VISIBLE);

            final ImageView imageView = (ImageView) convertView.findViewById(R.id.exrecise_image);
            imageView.setImageResource(bodyPartArrayList.get(position).getImage());
            TextView textView = (TextView) convertView.findViewById(R.id.bodypart_name);
            textView.setText(bodyPartArrayList.get(position).getBody_part_name());
            //final View finalConvertView = convertView;
            //Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "rainbowFont.ttf");

            //textView.setTypeface(custom_font);
        } else {
            View view = convertView.findViewById(R.id.grad);
            view.setVisibility(View.INVISIBLE);
            ImageView imageView2 = (ImageView) convertView.findViewById(R.id.play_but);
            imageView2.setVisibility(View.VISIBLE);
            TextView textView = (TextView) convertView.findViewById(R.id.bodypart_name);
            textView.setVisibility(View.GONE);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.exrecise_image);
            Picasso.with(getContext()).load(bodyPartArrayList.get(position).getImage2()).into(imageView);

        }
        return convertView;
    }

    @Override
    public int getCount() {
        return bodyPartArrayList.size();
    }

    private void setInitialUISetup() {
        //final ImageView imageView=(ImageView) findViewById(R.id.back);

        //TextView tx = (TextView)findViewById(R.id.appname);


    }
}
