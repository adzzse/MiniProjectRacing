package com.example.miniprojectracing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

    public HistoryAdapter(Context context, List<HistoryItem> historyItems) {
        super(context, 0, historyItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
        }

        HistoryItem item = getItem(position);

        TextView tvHorseName = convertView.findViewById(R.id.tv_horse_name);
        TextView tvTotalWins = convertView.findViewById(R.id.tv_total_wins);
        TextView tvWinRate = convertView.findViewById(R.id.tv_win_rate);

        tvHorseName.setText(item.getHorseName());
        tvTotalWins.setText("Thắng: " + item.getTotalWins());
        tvWinRate.setText("Tỷ lệ thắng: " + String.format("%.2f%%", item.getWinRate()));

        return convertView;
    }
}
