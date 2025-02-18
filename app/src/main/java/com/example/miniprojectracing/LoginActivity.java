package com.example.miniprojectracing;

import android.content.Intent;
import android.media.MediaPlayer;  // Thêm import MediaPlayer
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private MediaPlayer mediaPlayer;  // Biến MediaPlayer để phát nhạc nền

    // Fake data (username and password)
    private HashMap<String, String> fakeUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // Fake user data
        fakeUsers = new HashMap<>();
        fakeUsers.put("a", "a");
        fakeUsers.put("user2", "password2");
        fakeUsers.put("user3", "password3");

        // Khởi tạo MediaPlayer để phát nhạc nền
        mediaPlayer = MediaPlayer.create(this, R.raw.nhac);  // Nhạc nền (tên tệp nhạc trong raw)
        mediaPlayer.setLooping(true);  // Đặt nhạc lặp lại liên tục
        mediaPlayer.start();  // Bắt đầu phát nhạc nền

        // Handle login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Check credentials
                if (fakeUsers.containsKey(username) && fakeUsers.get(username).equals(password)) {
                    // Successful login
                    Intent intent = new Intent(LoginActivity.this, HomeRaceActivity.class);
                    startActivity(intent);
                    finish();  // Prevent returning to LoginActivity
                } else {
                    // Failed login
                    Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Đảm bảo nhạc được tạm dừng khi Activity bị pause
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause(); // Tạm dừng nhạc khi Activity bị pause
        }
    }

    // Tiếp tục phát nhạc khi Activity quay lại
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start(); // Tiếp tục phát nhạc khi Activity trở lại
        }
    }

    // Giải phóng tài nguyên khi Activity bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng MediaPlayer khi Activity bị hủy
        }
    }
}
