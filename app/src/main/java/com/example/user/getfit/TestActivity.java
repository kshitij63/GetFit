package com.example.user.getfit;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.R.id.progress;
import static android.R.id.toggle;
import static com.facebook.FacebookSdk.getApplicationContext;
import static java.security.AccessController.getContext;

public class TestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,OnDataPointListener,GoogleApiClient.OnConnectionFailedListener {
    DataReadRequest readRequest;
    ProgressDialog dialog;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private boolean initialized;
    public static   GoogleApiClient client;
    TextView goal,left;
    int total,Goal;
    ArcProgress progress;
    private ResultCallback<Status> mSubscribeResultCallback;
    private ResultCallback<Status> mCancelSubscriptionResultCallback;
    private ResultCallback<ListSubscriptionsResult> mListSubscriptionsResultCallback;
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
        dialog=new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setTitle("Updating progress..");
        dialog.show();
        preferences=getSharedPreferences("NotSET",MODE_PRIVATE);
        editor=preferences.edit();

        progress = (ArcProgress) findViewById(R.id.custom_progress);
        //Toast.makeText(this, getIntent().getExtras().getString("goal"), Toast.LENGTH_SHORT).show();
        //if(preferences.getInt("goal",0)==0) {  **remove this **
            Goal = Integer.valueOf(getIntent().getExtras().getString("goal"));
            editor.putInt("goal",Goal);
            editor.apply();

       /// }
        goal=(TextView) findViewById(R.id.goal);
        left=(TextView) findViewById(R.id.left);
        goal.setText(String.valueOf(preferences.getInt("goal",0)));


        //progress.setProgress(50);

        db=helper.getWritableDatabase();

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        client = new GoogleApiClient.Builder(this).
                addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .enableAutoManage(this,0,this)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();


        initCallbacks();

    }

    private void initCallbacks() {
        mSubscribeResultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                        Log.e( "RecordingAPI", "Already subscribed to the Recording API");
                    } else {
                        Log.e("RecordingAPI", "Subscribed to the Recording API");
                    }
                }
            }
        };

        mCancelSubscriptionResultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.e( "RecordingAPI", "Canceled subscriptions!");
                } else {
                    // Subscription not removed
                    Log.e("RecordingAPI", "Failed to cancel subscriptions");
                }
            }
        };

        mListSubscriptionsResultCallback = new ResultCallback<ListSubscriptionsResult>() {
            @Override
            public void onResult(@NonNull ListSubscriptionsResult listSubscriptionsResult) {
                for (Subscription subscription : listSubscriptionsResult.getSubscriptions()) {
                    DataType dataType = subscription.getDataType();
                    Log.e( "RecordingAPI", dataType.getName() );
                    for (Field field : dataType.getFields() ) {
                        Log.e( "RecordingAPI", field.toString() );
                    }
                }
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent =new Intent(this,ActiveApiClientService.class);
        startService(intent);
        dialog.show();
        TenPMNotify();

        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();

        //SensorRequest request=new SensorRequest.Builder().setDataType(DataType.TYPE_STEP_COUNT_DELTA).setSamplingRate(1, java.util.concurrent.TimeUnit.SECONDS).build();
        //Toast.makeText(getContext(),"connected sus",Toast.LENGTH_SHORT).show();
        final DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_CALORIES_EXPENDED)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build();
        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                try {
                    Log.e("get", dataSourcesResult.toString());
                    for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                        Log.e("loop", "lkk");
                        if (DataType.TYPE_CALORIES_EXPENDED.equals(dataSource.getDataType())) {
                            Log.e("get", "in if");

                            registerFitnessDataListener(dataSource, DataType.TYPE_CALORIES_EXPENDED);
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
        Fitness.RecordingApi.subscribe(client, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(mSubscribeResultCallback);
        ViewWeekStepCountTask task=new ViewWeekStepCountTask();
        task.execute();
        {
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
            Job job = dispatcher.newJobBuilder()
                    .setService(NotificationJobService.class)
                    .setTag("Notify_Job")
                    .setTrigger(Trigger.executionWindow(15*60,15*60+30))
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(false)
                    .setReplaceCurrent(true)
                    .build();

            int result = dispatcher.schedule(job);
            if(result==FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS){
                Log.e("success","sucess");

            }

        }
        //Intent intent=new Intent(this,NotificationService.class);
        //startService(intent);
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
            Handler handler=new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();

                }
            });
            //        runOnUiThread(new Runnable() {
            //          @Override
            //        public void run() {

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
    total=0;
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
    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {



        protected Void doInBackground(Void... params) {
     //       displayLastWeeksData();
            DailyTotalResult dataReadResult = Fitness.HistoryApi.readDailyTotal(client, DataType.TYPE_CALORIES_EXPENDED).await(1, TimeUnit.MINUTES);
            showDataSet(dataReadResult.getTotal());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.hide();
            Toast.makeText(TestActivity.this,total+"total",Toast.LENGTH_SHORT).show();

if(preferences.getInt("goal",0)<=total){
    left.setText("0");
    progress.setBottomText("COMPLETED");
    progress.setProgress(100);
    //makeNotification("Congratulations!!.You have successfully completed today's goal of burning " +preferences.getInt("goal",0)
    //+".Keep up the good work see your tomorrow");
}
else {
    //Toast.makeText(TestActivity.this,"progress" +((total / Goal) * 100),Toast.LENGTH_SHORT).show();
    int p = ((total * 100) / preferences.getInt("goal", 0));
    int mGoal = preferences.getInt("goal", 0) - total;
    left.setText(String.valueOf(mGoal));
    progress.setProgress(p);
}


        }
    }



    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                total= (int) (total+Float.valueOf(dp.getValue(field).toString()));
            }


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        total=0;
    }
    private void TenPMNotify(){
        AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent=new Intent(this,TestActivity.class);
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,22);
        PendingIntent intent1=PendingIntent.getBroadcast(this,(int)System.currentTimeMillis(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,intent1);

    }

}
