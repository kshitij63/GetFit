package com.example.user.getfit;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

/**
 * Created by user on 7/10/2017.
 */

public class ActiveApiClientService extends Service implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {
    public  static GoogleApiClient client;
    private  boolean authInProgress;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     //* @param name Used to name the worker thread, important only for debugging.
     */
    public ActiveApiClientService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
       Thread thread=new Thread(new Mythread(startId));
        thread.start();
    return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("service","connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Fialed","failed");
        if( !authInProgress ) {
            try {
                authInProgress = false;
                Log.e("Fialed","failed334");

                connectionResult.startResolutionForResult( (Activity) getApplicationContext(),1);
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    client.disconnect();
    }

    public void createClient(){
        client = new GoogleApiClient.Builder(this).
                addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addApi(Fitness.HISTORY_API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    }

    class Mythread extends Thread{
        int start_id;

        Mythread(int start_id){
            this.start_id=start_id;
        }
        @Override
        public void run() {
            super.run();
        if(client==null){
            createClient();
        }
        client.connect();
        }
    }


}
