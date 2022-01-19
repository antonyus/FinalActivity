package com.example.finalactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    private final ArrayList<User> userArrayList;

    //userAdapter
    //constructor
    public UserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    //onCreateViewHolder
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    //onBindViewHolder
    // updates RecyclerView.ViewHoder.itemVIew
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.universalFirstName.setText(user.getName());
        holder.universalUsername.setText(user.getUsername());
        holder.universalProfilePicture.setTransitionName(user.getProfilePic());
        Picasso.get().load(user.getProfilePic()).transform(new CropCircleTransformation()).into(holder.universalProfilePicture);
        holder.universalEmail.setText(user.getEmail());
        holder.universalPhone.setText(user.getPhone());
    }

    //getItemCount
    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final private ImageView universalProfilePicture;
        final private TextView universalFirstName, universalUsername, universalEmail, universalPhone;
        Intent intent;

        //viewHolder
        //get items
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            universalProfilePicture = itemView.findViewById(R.id.universal_profile_picture);
            universalFirstName = itemView.findViewById(R.id.universal_name);
            universalUsername = itemView.findViewById(R.id.universal_username);
            universalEmail = itemView.findViewById(R.id.universal_email);
            universalPhone = itemView.findViewById(R.id.universal_phone);

            itemView.setOnClickListener(this);

        }

        //shows all info and sends to next activity (individual users)
        public void ShowAllInformation(View view) {

            intent = new Intent(view.getContext(), IndividualUserDetails.class);

            String profPicUrl = universalProfilePicture.getTransitionName();
            String firstAndLastName = universalFirstName.getText().toString();
            String username = universalUsername.getText().toString();
            String email = universalEmail.getText().toString();
            String phone = universalPhone.getText().toString();

            intent.putExtra("profilePic", profPicUrl);
            intent.putExtra("firstAndLastName", firstAndLastName);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);

            //notification control
            RecyclerViewActivity.isActivityCalled = true;
            view.getContext().startActivity(intent);

        }

        //onClick
        @Override
        public void onClick(View view) {
            ShowAllInformation(view);
        }
    }
}