package com.example.user.getfit;

import android.content.Intent;
import android.content.IntentSender;
import android.icu.util.TimeUnit;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryApi;
import com.google.android.gms.fitness.SensorsApi;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.service.FitnessSensorServiceRequest;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by user on 7/7/2017.
 */

public class ProgressDetailsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,OnDataPointListener {
    private ArcProgress progress;
    //private static final int GOAL_CALORIES=3000;
    GoogleApiClient client;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progressdetailfragment, container, false);
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        client = new GoogleApiClient.Builder(getContext()).
                addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addApi(Fitness.SENSORS_API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        progress = (ArcProgress) view.findViewById(R.id.custom_progress);
        Toast.makeText(getContext(), getArguments().getString("goal"), Toast.LENGTH_SHORT).show();
        progress.setProgress(50);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getContext(), "connected", Toast.LENGTH_SHORT).show();
        //SensorRequest request=new SensorRequest.Builder().setDataType(DataType.TYPE_STEP_COUNT_DELTA).setSamplingRate(1, java.util.concurrent.TimeUnit.SECONDS).build();
        //Toast.makeText(getContext(),"connected sus",Toast.LENGTH_SHORT).show();
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();
        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                try {
                    Log.e("get", "here");
                    for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                        Log.e("loop", "lkk");
                        if (DataType.TYPE_STEP_COUNT_DELTA.equals(dataSource.getDataType())) {
                            Log.e("get", "in if");

                            registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                        } else {
                            Log.e("get", "in else");

                        }
                    }
                }catch (Exception e){
                    Log.e("error",e.getMessage());
                }
            }
            }

            ;

        Fitness.SensorsApi.findDataSources(client,dataSourceRequest)
                    .

            setResultCallback(dataSourcesResultCallback);


        }


    @Override
    public void onConnectionSuspended(int i) {
           }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(getContext(),"connected faied",Toast.LENGTH_SHORT).show();
        if( !authInProgress ) {
            try {
                authInProgress = false;
                connectionResult.startResolutionForResult( getActivity(), REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    client.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e( "GoogleFit", "activity" );

        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = true;
            if( resultCode == RESULT_OK ) {
                if( !client.isConnecting() && !client.isConnected() ) {
                    client.connect();
//Toast.makeText(getContext(),"ok",Toast.LENGTH_SHORT).show();
                    Log.e( "GoogleFit", "hereok" );
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        Toast.makeText(getContext(),"data point",Toast.LENGTH_SHORT).show();
        Log.e( "GoogleFit", "ondata" );

        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
    //        runOnUiThread(new Runnable() {
      //          @Override
        //        public void run() {
                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
          Log.e("google","Field: " + field.getName() + " Value: " + value);
            // }
           // });
        }
    }


    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        Toast.makeText(getContext(),"ok",Toast.LENGTH_SHORT).show();
        Log.e( "GoogleFit", "regisme" );

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, java.util.concurrent.TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( client, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e( "GoogleFit", "onResult" );

                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                        else {
                            Log.e( "GoogleFit", "SensorApi unsuccessfully added" );

                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        Fitness.SensorsApi.remove( client, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            client.disconnect();
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }
}
