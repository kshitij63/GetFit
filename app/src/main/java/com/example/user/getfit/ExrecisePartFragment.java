package com.example.user.getfit;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 7/7/2017.
 */

public class ExrecisePartFragment extends android.support.v4.app.Fragment {
    ListView view1;
    ArrayList<BodyPart> bodyPartArrayList;
GetNameListener getNameListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.exercisepartfragment,container,false);
        bodyPartArrayList=new ArrayList<>();
        bodyPartArrayList.add(new BodyPart("BICEP",R.drawable.bicep));
        bodyPartArrayList.add(new BodyPart("TRICEP",R.drawable.tricep));
        bodyPartArrayList.add(new BodyPart("BACK",R.drawable.back));
        bodyPartArrayList.add(new BodyPart("SHOULDER",R.drawable.shoulder));
        bodyPartArrayList.add(new BodyPart("ABS",R.drawable.abs));
        bodyPartArrayList.add(new BodyPart("BODYWEIGHT",R.drawable.boyweight));
        bodyPartArrayList.add(new BodyPart("CHEST",R.drawable.chest));
        bodyPartArrayList.add(new BodyPart("FOREARMS",R.drawable.forearm));
        bodyPartArrayList.add(new BodyPart("TRAPS",R.drawable.tarps));
        bodyPartArrayList.add(new BodyPart("LEGS",R.drawable.legs));

        view1=(ListView) view.findViewById(R.id.grid);
        view1.setAdapter(new BodyPartAdapter(getActivity(),bodyPartArrayList));

        view1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),ExerciseDetailActivity.class);
                intent.putExtra("name",bodyPartArrayList.get(position).getBody_part_name());
                startActivity(intent);

            }
        });

        return view;
    }


    public interface GetNameListener{
        void getName(String name);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getNameListener=(GetNameListener) getActivity();
    }
}
