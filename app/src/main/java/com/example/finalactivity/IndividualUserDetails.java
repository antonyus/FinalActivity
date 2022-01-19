package com.example.finalactivity;

import static com.example.finalactivity.RecyclerViewActivity.JSON_SAVE_RETRIEVE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class IndividualUserDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "IndividualUserDetails";

    private ImageView profPic;
    private EditText firstAndLastName, username, email, phone;
    private Button backButton;
    public Intent intent;
    private ArrayList<User> userArrayList;
    String profPicValue, firstAndLastNameValue, usernameValue, emailValue, phoneValue;
    //Compatibility library for NotificationManager with fallbacks for older platforms.
    NotificationManagerCompat notificationManager;
    UtilityClass utilityClass;

    static final int CAPTURE_IMAGE_REQUEST = 1000;

    String mCurrentPhotoPath;
    File photoFile = null;
    Uri photoUri = null;

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_details);
        userArrayList = new ArrayList<>();
        initialization();

        //click listeners
        backButton.setOnClickListener(this);
        profPic.setOnClickListener(this);

        utilityClass = new UtilityClass(this);
        notificationManager = NotificationManagerCompat.from(this);

    }

    //onResume
    @Override
    protected void onResume() {
        super.onResume();
        //Notification control between activities
        RecyclerViewActivity.isActivityCalled = false;
        notificationManager.cancelAll(); //cancel all background notification when user is back
        Log.d(TAG, "onResume Log: ");
    }

    //onPause
    @Override
    protected void onPause() {
        super.onPause();
        saveIt();

        //calls Notification

        if (!RecyclerViewActivity.isActivityCalled) {
            utilityClass.createNotificationChannel(getClass(), getIntent().getStringExtra("profilePic"),
                    getIntent().getStringExtra("firstAndLastName"), getIntent().getStringExtra("username"), getIntent().getStringExtra("email"), getIntent().getStringExtra("phone"));
            Log.d(TAG, "onPause Log: ");
        }
        //else destroyed
        utilityClass.onDestroyControl();
        Log.d(TAG, "onPause destroyed Log: ");
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
        //Toast.makeText(this, "Back Button Pressed", Toast.LENGTH_SHORT)
                //.show();
        goBackToRecyclerView();
    }
    //onBackToRecyclerView
    //Rectcler activity knows which page user come back from and retrieves it data
    public void goBackToRecyclerView() {
        intent = new Intent(this, RecyclerViewActivity.class);
        RecyclerViewActivity.isActivityCalled = true;
        startActivity(intent);
        finish();
    }


    //saveIt
    //saves data
    //should save data if changed by user
    //pushes back to json style into memory with SharedPreferences
    public void saveIt() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        //pull all info
        String json = sharedPreferences.getString(JSON_SAVE_RETRIEVE, "");
        Type type = new TypeToken<List<User>>() {
        }.getType();
        userArrayList = gson.fromJson(json, type);

        //find the user that u just clicked and changed the values
        for (User x : userArrayList) {
            if (x.getName().equals(firstAndLastNameValue)) {
                x.setName(firstAndLastName.getText().toString());
                x.setUsername(username.getText().toString());
                x.setEmail(email.getText().toString());
                x.setPhone(phone.getText().toString());
                x.setProfilePic(profPicValue);
            }
        }
        sharedPreferences.edit().putString(JSON_SAVE_RETRIEVE, gson.toJson(userArrayList)).apply();
    }

    //createImageFile
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //onSaveInstanceState
    //for phone rotations
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("firstAndLastName", firstAndLastName.getText().toString());
        outState.putString("username", username.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("phone", phone.getText().toString());
        outState.putString("profilePic", profPicValue);
        Log.d(TAG, "onSaveInstanceState Log: " + firstAndLastName.getText() + "->" + username.getText() + "->" + email.getText() + "->" + phone.getText() + "->"  + profPicValue);
    }

    //onRestoreInstanceState
    //brings back info from save instance
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        firstAndLastNameValue = savedInstanceState.getString("firstAndLastName");
        usernameValue = savedInstanceState.getString("username");
        emailValue = savedInstanceState.getString("email");
        profPicValue = savedInstanceState.getString("profilePic");
        phoneValue = savedInstanceState.getString("phone");

    }


    public void initialization() {
        profPic = findViewById(R.id.profile_picture);
        firstAndLastName = findViewById(R.id.first_last_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        backButton = findViewById(R.id.back_button);


        //get the values from last intent
        profPicValue = getIntent().getStringExtra("profilePic");
        firstAndLastNameValue = getIntent().getStringExtra("firstAndLastName");
        usernameValue = getIntent().getStringExtra("username");
        emailValue = getIntent().getStringExtra("email");
        phoneValue = getIntent().getStringExtra("phone");


        //assign values
        Picasso.get().load(profPicValue).transform(new CropCircleTransformation()).resize(400, 400).into(profPic);
        firstAndLastName.setText(firstAndLastNameValue);
        username.setText(usernameValue);
        email.setText(emailValue);
        phone.setText(phoneValue);
    }


    /*
 Camera work in progress
  */
    @SuppressLint("QueryPermissionsNeeded")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            RecyclerViewActivity.isActivityCalled = true;
        } else {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                photoFile = createImageFile();

                photoUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                RecyclerViewActivity.isActivityCalled = true;
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }
        RecyclerViewActivity.isActivityCalled = true;
    }

    //onRequestPermissionResult
    //More Camera stuff
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    openCamera();
                }
            }
        }
    }


    //onActivityResult
    //More Camera stuff
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 400, 400, false);
            profPic.setImageBitmap(myBitmap);
            for (User x : userArrayList) {
                if (x.getName().equals(firstAndLastNameValue)) {
                    profPicValue = "file://" + photoFile.getAbsolutePath();
                }
            }
        } else {
            Log.d(TAG, "onActivityResult Log: Request cancelled.");
        }
        saveIt();
    }



    //Camera
    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to change profile picture?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int which) {
                openCamera();
            }
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                goBackToRecyclerView();
                break;
            case R.id.profile_picture:
                openDialog();
                break;
        }
    }

}
