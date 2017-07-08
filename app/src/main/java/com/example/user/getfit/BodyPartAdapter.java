package com.example.user.getfit;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by user on 7/7/2017.
 */

public class BodyPartAdapter extends ArrayAdapter<BodyPart> {
ArrayList<BodyPart> bodyPartArrayList;

    public BodyPartAdapter(@NonNull Context context) {
        super(context, R.layout.bodypart);
    bodyPartArrayList=new ArrayList<>();
        bodyPartArrayList.add(new BodyPart("BICEPS",R.drawable.bicep));
        bodyPartArrayList.add(new BodyPart("TRICEPS",R.drawable.tricep));
        bodyPartArrayList.add(new BodyPart("BACK",R.drawable.back));
        bodyPartArrayList.add(new BodyPart("SHOULDERS",R.drawable.shoulder));
        bodyPartArrayList.add(new BodyPart("ABS",R.drawable.abs));
        bodyPartArrayList.add(new BodyPart("BODYWEIGHT",R.drawable.boyweight));
        bodyPartArrayList.add(new BodyPart("CHEST",R.drawable.boyweight));
        bodyPartArrayList.add(new BodyPart("FOREARMS",R.drawable.boyweight));
        bodyPartArrayList.add(new BodyPart("TRAPS",R.drawable.boyweight));
        bodyPartArrayList.add(new BodyPart("LEGS",R.drawable.legs));


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.bodypart,parent,false);
        }

        final ImageView imageView=(ImageView) convertView.findViewById(R.id.exrecise_image);
        imageView.setImageResource(bodyPartArrayList.get(position).getImage());
        TextView textView=(TextView) convertView.findViewById(R.id.bodypart_name);
        textView.setText(bodyPartArrayList.get(position).getBody_part_name());
        final View finalConvertView = convertView;
        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(),  "rainbowFont.ttf");

        textView.setTypeface(custom_font);
        return convertView;
    }

    @Override
    public int getCount() {
        return bodyPartArrayList.size();
    }

    private void setInitialUISetup(){
        //final ImageView imageView=(ImageView) findViewById(R.id.back);

        //TextView tx = (TextView)findViewById(R.id.appname);



    }
}
