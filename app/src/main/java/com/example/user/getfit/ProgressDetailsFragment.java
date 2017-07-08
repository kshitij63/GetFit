package com.example.user.getfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryApi;

/**
 * Created by user on 7/7/2017.
 */

public class ProgressDetailsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {
private ArcProgress progress;
    //private static final int GOAL_CALORIES=3000;
    GoogleApiClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.progressdetailfragment,container,false);
        progress=(ArcProgress) view.findViewById(R.id.custom_progress);
        Toast.makeText(getContext(),getArguments().getString("goal"),Toast.LENGTH_SHORT).show();
        progress.setProgress(50);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client=new GoogleApiClient.Builder(getContext()).addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addApi(Fitness.GOALS_API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
