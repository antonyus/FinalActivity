package com.example.finalactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
//inheritance
public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private static final String TAG = "Main Activity Log";
    private static final String GOOGLE_LOGO = "https://i.ibb.co/fkHtYJw/580b57fcd9996e24bc43c51f.png";
    private static final int SIGN_IN = 9001;


    private ImageView googleImage;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttons
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_out).setOnClickListener(this);


        googleImage = findViewById(R.id.google_logo);
        Picasso.get().load(GOOGLE_LOGO).into(googleImage);

        //Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        //Sign in Client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //Create buttons bu findView ID, set color and size
        SignInButton signInButton = findViewById(R.id.sign_in);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);


    }

    //onStart
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(googleSignInAccount);
        Log.d(TAG, "onStart Log: ");
    }

    //onResume
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Log: ");
    }

    //onPause
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause Log: ");
    }

    //onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    //onBackPressed
    @Override
    public void onBackPressed() {

        //Toast.makeText(this, "You are in LogIn Page", Toast.LENGTH_SHORT)
                //.show();
    }

    //handleSignInRESULT
    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            updateUI(googleSignInAccount);

        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult: failed: " + e.getStatusCode());
            updateUI(null);
        }
    }

    //onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //singIn
    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
    }

    //updateUI
    //updating screen layout
    public void updateUI(@Nullable GoogleSignInAccount googleSignInAccount) {

        if (googleSignInAccount != null) {

            Intent intent = new Intent(this, RecyclerViewActivity.class);
            startActivity(intent);

            findViewById(R.id.sign_in).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in).setVisibility(View.VISIBLE);
        }
    }


    //onClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                signIn();
                break;
        }
    }
}