package com.example.user.getfit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerFragment;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by user on 7/7/2017.
 */

public class ProgressFragment extends Fragment {
    getTonext gettonext;
    EditText editText;
    Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.progressfragment, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.prog_backdrop);
        editText = (EditText) view.findViewById(R.id.calorie);
        button = (Button) view.findViewById(R.id.button_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettonext.YourCalorie(editText.getText().toString());
            }
        });
        imageView.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(getContext())
                        .radius(15)
                        .sampling(2).capture(view.findViewById(R.id.prog_backdrop)).into(imageView);
                TextView tx = (TextView) view.findViewById(R.id.track_progress);

                Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "rainbowFont.ttf");

                tx.setTypeface(custom_font);


            }
        });
        return view;
    }

    public interface getTonext {
        void YourCalorie(String calorie);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            gettonext = (getTonext) getActivity();
        } catch (Exception e) {
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
