package com.example.miniprojectracing.entity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miniprojectracing.R;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    private ArrayList<User> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        itemList = new ArrayList<>();
        itemList.add(new User("johnDoe", "password1", "John Doe", 100));
        itemList.add(new User("janeDoe", "password2", "Jane Doe", 200));
        itemList.add(new User("mikeSmith", "password3", "Mike Smith", 150));
        itemList.add(new User("susanLee", "password4", "Susan Lee", 180));
        itemList.add(new User("tomBrown", "password5", "Tom Brown", 220));

    }

}
