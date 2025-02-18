package com.example.miniprojectracing;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifImageView;
import android.animation.ObjectAnimator;

public class HomeRaceActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer, victorySound, loseSound;
    private boolean isMuted = false;
    private ImageButton btnToggleSound;
    private SeekBar sbHorse1, sbHorse2, sbHorse3, sbHorse4;
    private GifImageView gifThumb1, gifThumb2, gifThumb3, gifThumb4;
    private TextView tvMoney, tvRaceResult;
    private Button btnStart, btnDeposit, btnGuide, btnReset, btnLogout, btnHistory;
    private int currentMoney = 1000; // Số tiền ban đầu
    private EditText etBet1, etBet2, etBet3, etBet4; // Tiền cược cho từng con
    private View raceContainer, rootLayout;
    private boolean isRacing = false; // Trạng thái cuộc đua
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_race);

        raceContainer = findViewById(R.id.race_container);
        rootLayout = findViewById(R.id.root_layout);

        // Kết nối UI
        btnLogout = findViewById(R.id.btn_logout);
        tvMoney = findViewById(R.id.tv_money);
        sbHorse1 = findViewById(R.id.sb_horse1);
        sbHorse2 = findViewById(R.id.sb_horse2);
        sbHorse3 = findViewById(R.id.sb_horse3);
        sbHorse4 = findViewById(R.id.sb_horse4);
        btnStart = findViewById(R.id.btn_start);
        btnDeposit = findViewById(R.id.btn_deposit);
        btnGuide = findViewById(R.id.btn_guide);
        btnReset = findViewById(R.id.btn_reset);
        btnHistory = findViewById(R.id.btn_history);
        gifThumb1 = findViewById(R.id.wheelchair_gif1);
        gifThumb2 = findViewById(R.id.wheelchair_gif2);
        gifThumb3 = findViewById(R.id.wheelchair_gif3);
        gifThumb4 = findViewById(R.id.wheelchair_gif4);


        etBet1 = findViewById(R.id.editTextNumber1);
        etBet2 = findViewById(R.id.editTextNumber2);
        etBet3 = findViewById(R.id.editTextNumber3);
        etBet4 = findViewById(R.id.editTextNumber4);


        // Nhạc nền
        btnToggleSound = findViewById(R.id.btn_toggle_sound);
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        victorySound = MediaPlayer.create(this, R.raw.victory);
        loseSound = MediaPlayer.create(this, R.raw.lose);


        // Lấy số tiền từ bộ nhớ hiển thị lên tvMoney
        SharedPreferences sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE);
        currentMoney = sharedPreferences.getInt("current_money", 1000);
        tvMoney.setText("Money: " + currentMoney);


        // Mở màn hình DepositActivity khi nhấn nút Topup -> nhấn confirm -> trả về số tiền đã nạp
        btnDeposit.setOnClickListener(v -> {
            Intent depositIntent = new Intent(HomeRaceActivity.this, DepositActivity.class);
            startActivityForResult(depositIntent, 1);
        });


        // Bật/Tắt âm thanh
        btnToggleSound.setOnClickListener(v -> {
            isMuted = !isMuted;
            mediaPlayer.setVolume(isMuted ? 0f : 1.0f, isMuted ? 0f : 1.0f);
            btnToggleSound.setImageResource(isMuted ? R.drawable.ic_sound_offf : R.drawable.ic_sound_on);
        });


        // Lịch sử đua
        btnHistory.setOnClickListener(v -> {
            Intent historyIntent = new Intent(HomeRaceActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
        });


        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent loginIntent = new Intent(HomeRaceActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });


        // Hướng dẫn
        btnGuide.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(HomeRaceActivity.this);
            dialog.setContentView(R.layout.dialog_tutorial);
            Button btnClose = dialog.findViewById(R.id.btn_close);
            btnClose.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });


        // Reset cuộc đua về mặc định
        btnReset.setOnClickListener(v -> {

            handler.removeCallbacksAndMessages(null); // Dừng ngay lập tức các tiến trình trong handler
            isRacing = false; // Dừng cuộc đua

            zoomRaceTrack(false);

            currentMoney = 1000; // Reset tiền về 1000 coins
            sbHorse1.setProgress(0);
            sbHorse2.setProgress(0);
            sbHorse3.setProgress(0);
            sbHorse4.setProgress(0);
            etBet1.setText("");
            etBet2.setText("");
            etBet3.setText("");
            etBet4.setText("");

            tvMoney.setText("Money: " + currentMoney);

        updateGifPosition(gifThumb1, sbHorse1.getProgress(), sbHorse1);
        updateGifPosition(gifThumb2, sbHorse2.getProgress(), sbHorse2);
        updateGifPosition(gifThumb3, sbHorse3.getProgress(), sbHorse3);
        updateGifPosition(gifThumb4, sbHorse4.getProgress(), sbHorse4);

        etBet1.setVisibility(View.VISIBLE);
            etBet2.setVisibility(View.VISIBLE);
            etBet3.setVisibility(View.VISIBLE);
            etBet4.setVisibility(View.VISIBLE);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer = MediaPlayer.create(HomeRaceActivity.this, R.raw.music);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
        // Bắt đầu cuộc đua
        btnStart.setOnClickListener(v -> {
            startRace();
        });
    }
    private void startRace() {
        // Lấy số tiền cược từng con
        int betHorse1 = parseBet(etBet1.getText().toString());
        int betHorse2 = parseBet(etBet2.getText().toString());
        int betHorse3 = parseBet(etBet3.getText().toString());
        int betHorse4 = parseBet(etBet4.getText().toString());
        int totalBet = betHorse1 + betHorse2 + betHorse3 + betHorse4;
        // Kiểm tra cược hợp lệ
        if (totalBet == 0) {
            Toast.makeText(this, "Enter the bet amount!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalBet > currentMoney) {
            Toast.makeText(this, "Not enough money to place a bet!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Trừ tiền cược
        currentMoney -= totalBet;
        isRacing = true; // Cuộc đua bắt đầu
        hideNumberColumns();  // Ẩn các ô số khi bấm Start
        zoomRaceTrack(true); // Phóng to đường đua trước khi bắt đầu
        tvMoney.setText("Money: " + currentMoney);


        final Runnable updateProgress = new Runnable() {
            @Override
            public void run() {

                if (!isRacing) {
                    handler.removeCallbacks(this); // Dừng cuộc đua nếu isRacing = false
                    return;
                }
                sbHorse1.setProgress(sbHorse1.getProgress() + (int) (Math.random() * 5));
                sbHorse2.setProgress(sbHorse2.getProgress() + (int) (Math.random() * 5));
                sbHorse3.setProgress(sbHorse3.getProgress() + (int) (Math.random() * 5));
                sbHorse4.setProgress(sbHorse4.getProgress() + (int) (Math.random() * 5));

                updateGifPosition(gifThumb1, sbHorse1.getProgress(), sbHorse1);
                updateGifPosition(gifThumb2, sbHorse2.getProgress(), sbHorse2);
                updateGifPosition(gifThumb3, sbHorse3.getProgress(), sbHorse3);
                updateGifPosition(gifThumb4, sbHorse4.getProgress(), sbHorse4);


                if (sbHorse1.getProgress() >= 300 || sbHorse2.getProgress() >= 300 ||
                        sbHorse3.getProgress() >= 300 || sbHorse4.getProgress() >= 300) {


                    int maxProgress = Math.max(Math.max(sbHorse1.getProgress(), sbHorse2.getProgress()),
                            Math.max(sbHorse3.getProgress(), sbHorse4.getProgress()));


                    String winningHorse = "";
                    int winAmount = 0;
                    int loseAmount = 0;


                    if (maxProgress == sbHorse1.getProgress()) {
                        winningHorse = "Wheelchair 1";
                        winAmount = betHorse1 * 2;
                        loseAmount = betHorse2 + betHorse3 + betHorse4;
                    } else if (maxProgress == sbHorse2.getProgress()) {
                        winningHorse = "Wheelchair 2";
                        winAmount = betHorse2 * 2;
                        loseAmount = betHorse1 + betHorse3 + betHorse4;
                    } else if (maxProgress == sbHorse3.getProgress()) {
                        winningHorse = "Wheelchair 3";
                        winAmount = betHorse3 * 2;
                        loseAmount = betHorse1 + betHorse2 + betHorse4;
                    } else {
                        winningHorse = "Wheelchair 4";
                        winAmount = betHorse4 * 2;
                        loseAmount = betHorse1 + betHorse2 + betHorse3;
                    }

                    isRacing = false; // Đua xong Check lai xem dung vi tri chua
                    handler.removeCallbacks(this);

                    // Cộng tiền thắng vào currentMoney
                    currentMoney += winAmount;
                    tvMoney.setText("Money: " + currentMoney); // Cập nhật lại số tiền hiển thị

// Ghi nhận lịch sử đua
                    //  HistoryActivity.updateRaceHistory(winningHorse);
                    HistoryActivity.updateRaceHistory(HomeRaceActivity.this, winningHorse);

// Hiển thị kết quả
                    showRaceResultDialog(winningHorse, winAmount, loseAmount, currentMoney);
                    handler.removeCallbacks(this);

                } else {
                    handler.postDelayed(this, 100);
                }
            }
        };


        sbHorse1.setProgress(0);
        sbHorse2.setProgress(0);
        sbHorse3.setProgress(0);
        sbHorse4.setProgress(0);
        isRacing = true;
        handler.post(updateProgress);
    }

    public void updateGifPosition(GifImageView gif, int progress, SeekBar seekBar) {
        int seekBarWidth = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
        int newX = seekBar.getPaddingLeft() + (seekBarWidth * progress / seekBar.getMax());
        gif.setX(newX - (gif.getWidth() / 2));
    }


    private int parseBet(String betStr) {
        if (betStr.isEmpty()) return 0;
        try {
            return Integer.parseInt(betStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private void showRaceResultDialog(String winningHorse, int winAmount, int loseAmount, int currentBalance) {
        final Dialog dialog = new Dialog(HomeRaceActivity.this);
        dialog.setContentView(R.layout.dialog_race_result);


        TextView tvWinner = dialog.findViewById(R.id.tv_winner);
        TextView tvWin = dialog.findViewById(R.id.tv_win);
        TextView tvLose = dialog.findViewById(R.id.tv_lose);
        TextView tvBalance = dialog.findViewById(R.id.tv_balance);
        Button btnClose = dialog.findViewById(R.id.btn_close_result);


        tvWinner.setText("Winner: " + winningHorse);
        tvWin.setText("Win: " + winAmount);
        if(winAmount > 0){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            victorySound.start();
        }
        tvLose.setText("Loose: " + loseAmount);
        if(loseAmount > 0){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            loseSound.start();

        }
        tvBalance.setText("Current balance: " + currentBalance);


        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            zoomRaceTrack(false); // Thu nhỏ lại sau khi đóng thông báo kết quả
            etBet1.setVisibility(View.VISIBLE);
            etBet2.setVisibility(View.VISIBLE);
            etBet3.setVisibility(View.VISIBLE);
            etBet4.setVisibility(View.VISIBLE);

            sbHorse1.setProgress(0);
            sbHorse2.setProgress(0);
            sbHorse3.setProgress(0);
            sbHorse4.setProgress(0);

            etBet1.setText("");
            etBet2.setText("");
            etBet3.setText("");
            etBet4.setText("");

            updateGifPosition(gifThumb1, sbHorse1.getProgress(), sbHorse1);
            updateGifPosition(gifThumb2, sbHorse2.getProgress(), sbHorse2);
            updateGifPosition(gifThumb3, sbHorse3.getProgress(), sbHorse3);
            updateGifPosition(gifThumb4, sbHorse4.getProgress(), sbHorse4);

            mediaPlayer.start();
        });

        dialog.show();

    }

    private void hideNumberColumns() {
        etBet1.setVisibility(View.GONE);
        etBet2.setVisibility(View.GONE);
        etBet3.setVisibility(View.GONE);
        etBet4.setVisibility(View.GONE);
    }


    private void zoomRaceTrack(boolean zoomIn) {
        float scale = zoomIn ? 1.3f : 1.0f;

        // Phóng to hoặc thu nhỏ container đường đua
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(raceContainer, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(raceContainer, "scaleY", scale);
        scaleX.setDuration(500);
        scaleY.setDuration(500);
        scaleX.start();
        scaleY.start();
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


        SharedPreferences sharedPreferences = getSharedPreferences("game_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("current_money", currentMoney);
        editor.apply();
    }
}
