package com.example.user.getfit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {
    SignInButton signInButton;
    CallbackManager callbackManager;
    private ProgressDialog dialog;
    private LoginButton facebook_login;
    private static final int RC_SIGN_IN=100;
    private FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitialUISetup();
        dialog=new ProgressDialog(this);
        dialog.setTitle("Logging In....");
        dialog.setIndeterminate(true);
        callbackManager = CallbackManager.Factory.create();
        facebook_login=(LoginButton) findViewById(R.id.facebook_login);
        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                dialog.hide();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          if(firebaseAuth.getCurrentUser()!=null){
              Toast.makeText(MainActivity.this,"Welcome "+firebaseAuth.getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT).show();
              Intent intent=new Intent(MainActivity.this,TaskActivity.class);
              intent.putExtra("name",firebaseAuth.getCurrentUser().getDisplayName());
              intent.putExtra("email",firebaseAuth.getCurrentUser().getEmail());
              intent.putExtra("photo",firebaseAuth.getCurrentUser().getPhotoUrl());
              startActivity(intent);
              finish();
          }
            }
        };

        signInButton=(SignInButton) findViewById(R.id.google_sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
      //          Toast.makeText(MainActivity.this,"result3",Toast.LENGTH_LONG).show();

                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
dialog.show();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
               // Toast.makeText(MainActivity.this,"result",Toast.LENGTH_LONG).show();
                dialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                      dialog.hide();
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                      //      Toast.makeText(MainActivity.this,"welcome" +user.getDisplayName(),Toast.LENGTH_SHORT).show();
                  //          Intent intent=new Intent(MainActivity.this,TaskActivity.class);
                    //        startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }


    private void setInitialUISetup(){
        final ImageView imageView=(ImageView) findViewById(R.id.back);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(MainActivity.this)
                        .radius(15)
                        .sampling(2).capture(findViewById(R.id.frame)).into(imageView);

            }
        });
        TextView tx = (TextView)findViewById(R.id.appname);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "rainbowFont.ttf");

        tx.setTypeface(custom_font);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        mAuth.addAuthStateListener(authStateListener);
    }
    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            dialog.hide();
                            FirebaseUser user = mAuth.getCurrentUser();
                        //    Toast.makeText(MainActivity.this,"Facebook" +user.getDisplayName(),Toast.LENGTH_SHORT).show();
              //              Intent intent=new Intent(MainActivity.this,TaskActivity.class);
                //            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
       //                     Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

}
