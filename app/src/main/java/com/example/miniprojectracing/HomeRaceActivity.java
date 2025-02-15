package com.example.miniprojectracing;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import pl.droidsonroids.gif.GifImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeRaceActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer, victorySound, loseSound;
    private boolean isMuted = false; // Trạng thái âm thanh
    private ImageButton btnToggleSound;
    private SeekBar sbHorse1, sbHorse2, sbHorse3, sbHorse4;
    private GifImageView gifThumb1, gifThumb2, gifThumb3, gifThumb4;
    private TextView tvMoney, tvRaceResult;
    private EditText etBetMoney;
    private Spinner spinnerHorses;
    private Button btnStart, btnDeposit, btnGuide, btnReset, btnLogout;

    private int currentMoney = 1000; // Số tiền giả định ban đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_race);

        // Kết nối các thành phần UI
        btnLogout = findViewById(R.id.btn_logout);
        tvMoney = findViewById(R.id.tv_money);
        etBetMoney = findViewById(R.id.et_bet_money);
        spinnerHorses = findViewById(R.id.spinner_horses);
        sbHorse1 = findViewById(R.id.sb_horse1);
        sbHorse2 = findViewById(R.id.sb_horse2);
        sbHorse3 = findViewById(R.id.sb_horse3);
        sbHorse4 = findViewById(R.id.sb_horse4);
        btnStart = findViewById(R.id.btn_start);
        btnDeposit = findViewById(R.id.btn_deposit);
        btnGuide = findViewById(R.id.btn_guide);
        btnReset = findViewById(R.id.btn_reset);
        tvRaceResult = findViewById(R.id.tv_race_result);
        gifThumb1 = findViewById(R.id.wheelchair_gif1);
        gifThumb2 = findViewById(R.id.wheelchair_gif2);
        gifThumb3 = findViewById(R.id.wheelchair_gif3);
        gifThumb4 = findViewById(R.id.wheelchair_gif4);

        // Music
        btnToggleSound = findViewById(R.id.btn_toggle_sound);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        // Khởi tạo âm thanh thắng/thua
        victorySound = MediaPlayer.create(this, R.raw.victory);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        // Hiển thị số tiền hiện có
        tvMoney.setText("Số tiền: " + currentMoney);

        // Populate spinner with horse names
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horse_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorses.setAdapter(adapter);

        // Chuyển đến màn hình nạp tiền
        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent depositIntent = new Intent(HomeRaceActivity.this, DepositActivity.class);
                startActivityForResult(depositIntent, 1);
            }
        });
        //xử lý nút âm thanh
        btnToggleSound.setOnClickListener(v -> {
            if (isMuted) {
                mediaPlayer.setVolume(1.0f, 1.0f); // Mở âm thanh
                btnToggleSound.setImageResource(R.drawable.ic_sound_on);// Cập nhật UI
            } else {
                mediaPlayer.setVolume(0f, 0f); // Tắt âm thanh
                btnToggleSound.setImageResource(R.drawable.ic_sound_offf); // Cập nhật UI
            }
            isMuted = !isMuted; // Đảo trạng thái
        });
        //Đăng xuất tài khoản
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent loginIntent = new Intent(HomeRaceActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });


        // Chuyển đến màn hình hướng dẫn
        btnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a custom dialog
                final Dialog dialog = new Dialog(HomeRaceActivity.this);
                dialog.setContentView(R.layout.dialog_tutorial);

                // Set up the close button
                Button btnClose = dialog.findViewById(R.id.btn_close);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });

        // Reset cuộc đua
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbHorse1.setProgress(0);
                sbHorse2.setProgress(0);
                sbHorse3.setProgress(0);
                sbHorse4.setProgress(0);
                tvRaceResult.setText("The race results will be displayed here");
                etBetMoney.setText("");

            // Đặt lại vị trí GIF
            updateGifPosition(gifThumb1, sbHorse1.getProgress(), sbHorse1);
            updateGifPosition(gifThumb2, sbHorse2.getProgress(), sbHorse2);
            updateGifPosition(gifThumb3, sbHorse3.getProgress(), sbHorse3);
            updateGifPosition(gifThumb4, sbHorse4.getProgress(), sbHorse4);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer = MediaPlayer.create(HomeRaceActivity.this, R.raw.music);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }});

        // Bắt đầu cuộc đua (giả lập đơn giản)
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRaceResult.setText("The race results will be displayed here");
                // Lấy số tiền cược từ EditText
                String betMoneyStr = etBetMoney.getText().toString();
                if (betMoneyStr.isEmpty()) {
                    Toast.makeText(HomeRaceActivity.this, "Input the bet amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                int betMoney = Integer.parseInt(betMoneyStr);
                // Kiểm tra nếu số tiền cược hợp lệ
                if (betMoney > currentMoney) {
                    Toast.makeText(HomeRaceActivity.this, "Insufficient bet amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Trừ số tiền cược khỏi số tiền hiện có
                currentMoney -= betMoney;
                tvMoney.setText("Money: " + currentMoney); // Cập nhật số tiền hiện tại

                final Handler handler = new Handler();
                final Runnable updateProgress = new Runnable() {
                    @Override
                    public void run() {
                        sbHorse1.setProgress(sbHorse1.getProgress() + (int) (Math.random() * 5));
                        sbHorse2.setProgress(sbHorse2.getProgress() + (int) (Math.random() * 5));
                        sbHorse3.setProgress(sbHorse3.getProgress() + (int) (Math.random() * 5));
                        sbHorse4.setProgress(sbHorse4.getProgress() + (int) (Math.random() * 5));

                        // Cập nhật vị trí GIF khi SeekBar thay đổi
                        updateGifPosition(gifThumb1, sbHorse1.getProgress(), sbHorse1);
                        updateGifPosition(gifThumb2, sbHorse2.getProgress(), sbHorse2);
                        updateGifPosition(gifThumb3, sbHorse3.getProgress(), sbHorse3);
                        updateGifPosition(gifThumb4, sbHorse4.getProgress(), sbHorse4);

                        // Check if any horse has won
                        if (sbHorse1.getProgress() >= 300 || sbHorse2.getProgress() >= 300 ||
                                sbHorse3.getProgress() >= 300 || sbHorse4.getProgress() >= 300) {

                            int maxProgress = Math.max(Math.max(sbHorse1.getProgress(), sbHorse2.getProgress()),
                                    Math.max(sbHorse3.getProgress(), sbHorse4.getProgress()));

                            String selectedHorse = spinnerHorses.getSelectedItem().toString();
                            String winMessage = "";
                            boolean isWin = false;
                            int winAmount = betMoney;  // Số tiền thắng cược (nếu bạn có thêm tỷ lệ cược, hãy thay đổi giá trị này)

                            if (maxProgress == sbHorse1.getProgress()) {
                                if (selectedHorse.equals("Wheelchair 1")) {
                                    currentMoney += winAmount*2; // Cộng tiền thắng
                                    isWin = true;
                                }
                                winMessage = getWinMessage(selectedHorse, "Wheelchair 1", betMoney, isWin, winAmount);
                            } else if (maxProgress == sbHorse2.getProgress()) {
                                if (selectedHorse.equals("Wheelchair 2")) {
                                    currentMoney += winAmount*2; // Cộng tiền thắng
                                    isWin = true;
                                }
                                winMessage = getWinMessage(selectedHorse, "Wheelchair 2", betMoney, isWin, winAmount);
                            } else if (maxProgress == sbHorse3.getProgress()) {
                                if (selectedHorse.equals("Wheelchair 3")) {
                                    currentMoney += winAmount*2; // Cộng tiền thắng
                                    isWin = true;
                                }
                                winMessage = getWinMessage(selectedHorse, "Wheelchair 3", betMoney, isWin, winAmount);
                            } else {
                                if (selectedHorse.equals("Wheelchair 4")) {
                                    currentMoney += winAmount*2; // Cộng tiền thắng
                                    isWin = true;
                                }
                                winMessage = getWinMessage(selectedHorse, "Wheelchair 4", betMoney, isWin, winAmount);
                            }

                            // Hiển thị kết quả
                            tvRaceResult.setText(winMessage);
                            tvMoney.setText("Money: " + currentMoney);
                        } else {
                            // Tiếp tục cập nhật mọi 100ms
                            handler.postDelayed(this, 100);
                        }
                    }
                };
                //Cập nhật lại seekbar
                sbHorse1.setProgress(0);
                sbHorse2.setProgress(0);
                sbHorse3.setProgress(0);
                sbHorse4.setProgress(0);
                handler.post(updateProgress); // Start the race
            }
        });
    }
    private void updateGifPosition(GifImageView gif, int progress, SeekBar seekBar) {
        int seekBarWidth = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
        int newX = seekBar.getPaddingLeft() + (seekBarWidth * progress / seekBar.getMax());
        gif.setX(newX - (gif.getWidth() / 2));
    }


    private String getWinMessage(String selectedHorse, String winningHorse, int betMoney, boolean isWin, int winAmount) {
        if (isWin) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            victorySound.start();
            return "Congratulations! You chose " + winningHorse + " and won! \n You won " + winAmount*2 + " \n Your current balance is: " + (currentMoney);
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            loseSound.start();
            return selectedHorse + " that you chose did not win. You lost " + winAmount + "\n Try again!";
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Cập nhật tiền sau khi nạp
            int depositedMoney = data.getIntExtra("depositedMoney", 0);
            currentMoney += depositedMoney;
            tvMoney.setText("Money: " + currentMoney);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng MediaPlayer khi Activity bị hủy
        }
    }
}
