package com.example.miniprojectracing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeRaceActivity extends AppCompatActivity {

    private TextView tvMoney, tvRaceResult;
    private EditText etBetMoney;
    private Spinner spinnerHorses;
    private SeekBar sbHorse1, sbHorse2, sbHorse3, sbHorse4;
    private Button btnStart, btnDeposit, btnGuide, btnReset, btnHistory;

    private int currentMoney = 1000; // Số tiền giả định ban đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_race);

        // Kết nối các thành phần UI
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
        btnHistory = findViewById(R.id.btn_history);
        tvRaceResult = findViewById(R.id.tv_race_result);

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
        });

        // Mở màn hình lịch sử đua
        btnHistory.setOnClickListener(v -> {
            Intent historyIntent = new Intent(HomeRaceActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
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

                    if (sbHorse1.getProgress() >= 100 || sbHorse2.getProgress() >= 100 ||
                            sbHorse3.getProgress() >= 100 || sbHorse4.getProgress() >= 100) {

                        int maxProgress = Math.max(Math.max(sbHorse1.getProgress(), sbHorse2.getProgress()),
                                Math.max(sbHorse3.getProgress(), sbHorse4.getProgress()));

                        String selectedHorse = spinnerHorses.getSelectedItem().toString();
                        String winningHorse = "";

                        if (maxProgress == sbHorse1.getProgress()) {
                            winningHorse = "Ngựa 1";
                        } else if (maxProgress == sbHorse2.getProgress()) {
                            winningHorse = "Ngựa 2";
                        } else if (maxProgress == sbHorse3.getProgress()) {
                            winningHorse = "Ngựa 3";
                        } else {
                            winningHorse = "Ngựa 4";
                        }

                        tvRaceResult.setText(winningHorse + " thắng! " + getWinMessage(selectedHorse, winningHorse));
                        HistoryActivity.updateRaceHistory(winningHorse);
                    } else {
                        handler.postDelayed(this, 100);
                    }
                }
            };
            handler.post(updateProgress);
        });
    }

    private String getWinMessage(String selectedHorse, String winningHorse) {
        return selectedHorse.equals(winningHorse) ? "Bạn đã chọn đúng ngựa thắng!" : "Ngựa bạn chọn không thắng. Thử lại nào!";
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
}
