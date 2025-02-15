package com.example.miniprojectracing;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private TextView tvTotalRaces;
    private Button btnBackToRace;
    private static Map<String, Integer> horseWinCount = new HashMap<>();
    private static int totalRaces = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tableLayout = findViewById(R.id.tableLayout);
        tvTotalRaces = findViewById(R.id.tv_total_races);
        btnBackToRace = findViewById(R.id.btn_back_to_race);

        btnBackToRace.setOnClickListener(v -> finish()); // Quay lại trang trước đó

        loadRaceHistory();
    }

    private void loadRaceHistory() {
        tvTotalRaces.setText("Tổng số trận đua: " + totalRaces);
        tvTotalRaces.setGravity(Gravity.CENTER);

        tableLayout.removeAllViews();

        for (String horse : new String[]{"Ngựa 1", "Ngựa 2", "Ngựa 3", "Ngựa 4"}) {
            int wins = horseWinCount.getOrDefault(horse, 0);
            double winRate = (totalRaces == 0) ? 0 : ((double) wins / totalRaces) * 100;

            TableRow row = new TableRow(this);
            row.setPadding(10, 10, 10, 10);

            TextView tvHorse = new TextView(this);
            tvHorse.setText(horse);
            tvHorse.setPadding(10, 10, 10, 10);

            TextView tvWins = new TextView(this);
            tvWins.setText("Thắng: " + wins);
            tvWins.setPadding(10, 10, 10, 10);

            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setMax(100);
            progressBar.setProgress((int) winRate);
            progressBar.setScaleY(4f);
            TableRow.LayoutParams params = new TableRow.LayoutParams(450, 40); // Đặt chiều rộng và chiều cao cụ thể
            params.setMargins(20, 10, 30, 10);
            progressBar.setLayoutParams(params);

            // Thiết lập màu nền cho thanh tiến độ
            LayerDrawable progressDrawable = (LayerDrawable) progressBar.getProgressDrawable();
            Drawable progressLayer = progressDrawable.getDrawable(1);
            if (winRate > 0) {
                progressLayer.setTint(Color.GREEN);
            } else {
                progressLayer.setTint(Color.LTGRAY);
            }

            TextView tvWinRate = new TextView(this);
            tvWinRate.setText(String.format("%.2f%%", winRate));
            tvWinRate.setPadding(0, 10, 40, 10);

            row.addView(tvHorse);
            row.addView(tvWins);
            row.addView(progressBar);
            row.addView(tvWinRate);
            tableLayout.addView(row);
        }
        tableLayout.requestLayout(); // Cập nhật lại giao diện
    }

    public static void updateRaceHistory(String winningHorse) {
        horseWinCount.put(winningHorse, horseWinCount.getOrDefault(winningHorse, 0) + 1);
        totalRaces++;
    }
}

