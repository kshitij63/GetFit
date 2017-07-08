package com.example.user.getfit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by user on 7/7/2017.
 */

public class ExrecisePartFragment extends android.support.v4.app.Fragment {
    GridView view1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.exercisepartfragment,container,false);
        view1=(GridView) view.findViewById(R.id.grid);
        view1.setAdapter(new BodyPartAdapter(getActivity()));

        return view;
    }
}
