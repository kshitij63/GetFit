package com.example.user.getfit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by user on 7/10/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
Context context;
int total;
    SharedPreferences preferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        preferences=context.getSharedPreferences("NotSET",0);

        if(ActiveApiClientService.client.isConnected()){
            Log.e("Jobdispatch","Connected");

            ViewWeekStepCountTask viewWeekStepCountTask=new ViewWeekStepCountTask();
            viewWeekStepCountTask.execute();
        }
    }
    private void makeNotification(String goal) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);

        builder.setContentTitle("Goal Completed");
        builder.setContentText(goal);
        builder.setSmallIcon(R.drawable.dum);

        Intent notificationIntent=new Intent(context,TestActivity.class);
        notificationIntent.putExtra("intent","fromintent");
        PendingIntent pendingIntent=PendingIntent.getActivity(context, (int) System.currentTimeMillis(),notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager =(NotificationManager)context. getSystemService(NOTIFICATION_SERVICE);

        manager.notify(001,builder.build());

    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {



        protected Void doInBackground(Void... params) {
            //       displayLastWeeksData();
            DailyTotalResult dataReadResult = Fitness.HistoryApi.readDailyTotal(ActiveApiClientService.client, DataType.TYPE_CALORIES_EXPENDED).await(1, TimeUnit.MINUTES);
            showDataSet(dataReadResult.getTotal());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //Toast.makeText(TestActivity.this,total+"total",Toast.LENGTH_SHORT).show();

            if(preferences.getInt("goal",0)<=total){
                makeNotification("Congratulations!!.You have successfully completed today's goal of burning " +preferences.getInt("goal",0)
                        +".Keep up the good work see your tomorrow");
            }
            else {
                makeNotification("Sorry!!.You did not completed your goal");

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

}
