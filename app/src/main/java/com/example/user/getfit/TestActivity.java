package com.example.user.getfit;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,OnDataPointListener,GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient client;
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
TestStepHelper helper;
    SQLiteDatabase db;
    private boolean authInProgress = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        helper=new TestStepHelper(this);

        db=helper.getWritableDatabase();

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        client = new GoogleApiClient.Builder(this).
                addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addApi(Fitness.SENSORS_API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        ((Button) findViewById(R.id.fit_but)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db2 = helper.getReadableDatabase();
                Cursor cr = db2.rawQuery("select num_steps from step", null);
                if (cr.getCount()!=0) {
                    if (cr.moveToFirst()) {
                        do {
                            String ster = cr.getString(cr.getColumnIndex("num_steps"));

                            ((TextView) findViewById(R.id.stepssss)).setText(ster);

                        } while (cr.moveToNext());
                    }
                }
                else {
                    Toast.makeText(TestActivity.this,"noooo",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
        //SensorRequest request=new SensorRequest.Builder().setDataType(DataType.TYPE_STEP_COUNT_DELTA).setSamplingRate(1, java.util.concurrent.TimeUnit.SECONDS).build();
        //Toast.makeText(getContext(),"connected sus",Toast.LENGTH_SHORT).show();
        final DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build();
        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                try {
                    Log.e("get", dataSourcesResult.toString());
                    for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                        Log.e("loop", "lkk");
                        if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                            Log.e("get", "in if");

                            registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
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
        Toast.makeText(getApplicationContext(),"connected faied",Toast.LENGTH_SHORT).show();
        if( !authInProgress ) {
            try {
                authInProgress = false;
                connectionResult.startResolutionForResult( TestActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        ContentValues values=new ContentValues();

        Toast.makeText(this,"data point",Toast.LENGTH_SHORT).show();
        Log.e( "GoogleFit", "ondata" );

        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            values.put("num_step",value.toString());
            db.insert("step",null,values);

            //        runOnUiThread(new Runnable() {
            //          @Override
            //        public void run() {

            Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
            Log.e("google","Field: " + field.getName() + " Value: " + value);
            // }
            // });
        }




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        Toast.makeText(this,"ok",Toast.LENGTH_SHORT).show();
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
