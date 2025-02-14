package com.example.miniprojectracing;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.droidsonroids.gif.GifImageView;

public class HomeRaceActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer, victorySound, loseSound;
    private boolean isMuted = false; // Trạng thái âm thanh
    private ImageButton btnToggleSound;
    private SeekBar sbHorse1, sbHorse2, sbHorse3, sbHorse4;
    private GifImageView gifThumb1, gifThumb2, gifThumb3, gifThumb4;
    private TextView tvMoney, tvRaceResult;
    private EditText etBetMoney;
    private Spinner spinnerHorses;
    private Button btnStart, btnDeposit, btnGuide, btnReset;

    private int currentMoney = 1000; // Số tiền giả định ban đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_race);
        // Music
        btnToggleSound = findViewById(R.id.btn_toggle_sound);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        // Khởi tạo âm thanh thắng/thua
        victorySound = MediaPlayer.create(this, R.raw.victory);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        // Kết nối các thành phần UI
        tvMoney = findViewById(R.id.tv_money);
        etBetMoney = findViewById(R.id.et_bet_money);
        spinnerHorses = findViewById(R.id.spinner_horses);
        sbHorse1 = findViewById(R.id.sb_horse1);
        sbHorse2 = findViewById(R.id.sb_horse2);
        sbHorse3 = findViewById(R.id.sb_horse3);
        sbHorse4 = findViewById(R.id.sb_horse4);
        gifThumb1 = findViewById(R.id.wheelchair_gif1);
        gifThumb2 = findViewById(R.id.wheelchair_gif2);
        gifThumb3 = findViewById(R.id.wheelchair_gif3);
        gifThumb4 = findViewById(R.id.wheelchair_gif4);
        btnStart = findViewById(R.id.btn_start);
        btnDeposit = findViewById(R.id.btn_deposit);
        btnGuide = findViewById(R.id.btn_guide);
        btnReset = findViewById(R.id.btn_reset);
        tvRaceResult = findViewById(R.id.tv_race_result);

        //xử lý nút âm thanh
        btnToggleSound.setOnClickListener(v -> {
                if (isMuted) {
                    mediaPlayer.setVolume(1.0f, 1.0f); // Mở âm thanh
                    btnToggleSound.setImageResource(R.drawable.ic_sound_on);// Cập nhật UI
                } else {
                    mediaPlayer.setVolume(0f, 0f); // Tắt âm thanh
                    btnToggleSound.setImageResource(R.drawable.ic_sound_off); // Cập nhật UI
                }
                isMuted = !isMuted; // Đảo trạng thái
            });


        // Hiển thị số tiền hiện có
        tvMoney.setText("Money: " + currentMoney);

        // Populate spinner with horse names
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horse_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorses.setAdapter(adapter);

        // Chuyển đến màn hình nạp tiền
        btnDeposit.setOnClickListener(v -> {
            Intent depositIntent = new Intent(HomeRaceActivity.this, DepositActivity.class);
            startActivityForResult(depositIntent, 1);
        });

        // Chuyển đến màn hình hướng dẫn
        btnGuide.setOnClickListener(v -> {
            Intent guideIntent = new Intent(HomeRaceActivity.this, GuideActivity.class);
            startActivity(guideIntent);
        });

        // Reset cuộc đua
        btnReset.setOnClickListener(v -> {
            sbHorse1.setProgress(0);
            sbHorse2.setProgress(0);
            sbHorse3.setProgress(0);
            sbHorse4.setProgress(0);
            tvRaceResult.setText("Kết quả đua sẽ hiển thị tại đây");

            // Đặt lại vị trí GIF
            updateGifPosition(gifThumb1, sbHorse1.getProgress(), sbHorse1);
            updateGifPosition(gifThumb2, sbHorse2.getProgress(), sbHorse2);
            updateGifPosition(gifThumb3, sbHorse3.getProgress(), sbHorse3);
            updateGifPosition(gifThumb4, sbHorse4.getProgress(), sbHorse4);

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer = MediaPlayer.create(this, R.raw.music);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });

        // Bắt đầu cuộc đua (giả lập đơn giản)
        btnStart.setOnClickListener(v -> {
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
                    if (sbHorse1.getProgress() >= 500 || sbHorse2.getProgress() >= 500 ||
                            sbHorse3.getProgress() >= 500 || sbHorse4.getProgress() >= 500) {

                        int maxProgress = Math.max(Math.max(sbHorse1.getProgress(), sbHorse2.getProgress()),
                                Math.max(sbHorse3.getProgress(), sbHorse4.getProgress()));

                        String selectedHorse = spinnerHorses.getSelectedItem().toString();

                        if (maxProgress == sbHorse1.getProgress()) {
                            tvRaceResult.setText("Ngựa 1 thắng! " + getWinMessage(selectedHorse, "Ngựa 1"));
                        } else if (maxProgress == sbHorse2.getProgress()) {
                            tvRaceResult.setText("Ngựa 2 thắng! " + getWinMessage(selectedHorse, "Ngựa 2"));
                        } else if (maxProgress == sbHorse3.getProgress()) {
                            tvRaceResult.setText("Ngựa 3 thắng! " + getWinMessage(selectedHorse, "Ngựa 3"));
                        } else {
                            tvRaceResult.setText("Ngựa 4 thắng! " + getWinMessage(selectedHorse, "Ngựa 4"));
                        }
                    } else {
                        // Continue updating progress every 100ms
                        handler.postDelayed(this, 100);
                    }
                }
            };
            handler.post(updateProgress); // Start the race
        });
    }

    private void updateGifPosition(GifImageView gif, int progress, SeekBar seekBar) {
        int seekBarWidth = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
        int newX = seekBar.getPaddingLeft() + (seekBarWidth * progress / seekBar.getMax());

        gif.setX(newX - (gif.getWidth() / 2));
    }

    private String getWinMessage(String selectedHorse, String winningHorse) {
        if (selectedHorse.equals(winningHorse)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            victorySound.start();
            return "Bạn đã chọn đúng ngựa thắng!";

        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            loseSound.start();
            return "Ngựa bạn chọn không thắng. Thử lại nào!";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
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
