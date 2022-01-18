package com.example.finalactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class RecyclerViewActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private static final String TAG = "RecyclerViewActivity";
    protected static final String JSON_SAVE_RETRIEVE = "jsonX";

    private NotificationManagerCompat notificationManager;

    private static final String USERINFO = "https://jsonplaceholder.typicode.com/users";
    private static final String PICTUREINFO = "https://robohash.org/";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    UtilityClass utilityClass;

    private static ArrayList<User> userArrayList;
    public static boolean isActivityCalled = false;
    public static boolean isAlreadyCalled = false;

    private OkHttpClient client;
    private Gson gson;
    User[] users;


    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        Log.d(TAG, "onCreate Log: ");
        utilityClass = new UtilityClass(RecyclerViewActivity.this);

        notificationManager = NotificationManagerCompat.from(this);

        recyclerView = findViewById(R.id.recViewUser);
        signOutButton = findViewById(R.id.sign_in);

        userArrayList = new ArrayList<>();
        gson = new Gson();
        client = new OkHttpClient();



        String signedUserName, signedUserEmail, signedUserProfilePicture;
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        signedUserProfilePicture = String.valueOf(googleSignInAccount.getPhotoUrl());

        signedUserName = googleSignInAccount.getDisplayName();
        signedUserEmail = googleSignInAccount.getEmail();
        User googleUser = new User(signedUserName, signedUserEmail, signedUserProfilePicture);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        isAlreadyCalled = sharedPrefs.getBoolean("isDestroyedCalled", isAlreadyCalled);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove("isDestroyedCalled");
        editor.apply();


        if (!isAlreadyCalled) {
            try {
                run();
                userArrayList.add(googleUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAlreadyCalled = true;

        }

        else{
            retrieveData();
            Log.d(TAG, "onCreate Eroor Log: ");
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, userArrayList);
        recyclerView.setAdapter(userAdapter);
        findViewById(R.id.sign_out_recycler).setOnClickListener(this);
    }

    //onResume
    @Override
    protected void onResume() {
        super.onResume();
        isActivityCalled = false;
        notificationManager.cancelAll(); //cancel all background notifications
        Log.d(TAG, "onResume Log: ");
    }


    //onPause
    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        if (!isActivityCalled) {
            utilityClass.createNotificationChannel(getClass());
            utilityClass.onDestroyControl();
        }
        Log.d(TAG, "onPause Log: ");

    }

    //onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy Log: ");
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please Sign Out", Toast.LENGTH_SHORT)
                .show();
    }

    //signOutGoBack
    //Signs out and goes to the Main Activity with is the Sign in page
    private void signOutMainActivity() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        isActivityCalled = true;
        isAlreadyCalled = false;
        startActivity(intent);
        finish();

    }

    //saveData
    public void saveData() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(userArrayList);
        editor.putString(JSON_SAVE_RETRIEVE, json);
        editor.apply();
    }

    //retrieveData
    //retrieving data from json
    public void retrieveData() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString(JSON_SAVE_RETRIEVE, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {
        }.getType();
        userArrayList = gson.fromJson(json, type);
    }

    //run
    //takes info from json and puts it into an array
    public void run() {
        Request request = new Request.Builder()
                .url(USERINFO)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    users = gson.fromJson(response.body().string(), User[].class);
                    runOnUiThread(() -> newView(users));
                }
            }
        });
    }

    //newView
    //from user array tp dynamic array
    @SuppressLint("NotifyDataSetChanged")
    public void newView(User[] users) {
        int i = 0;
        for (User u : users) {
            u.setProfilePic(PICTUREINFO + i);
            userArrayList.add(u);
            i++;
        }
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_recycler:
                signOutMainActivity();
                break;
        }
    }
}
