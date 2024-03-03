package com.example.test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.R;
import com.example.test.Utils;

import java.util.List;

public class ScaleAdapter extends ArrayAdapter<String> {
    private List<String> roundIds;
    private Context context;
    public ScaleAdapter(Context context,List<String> roundIds) {
        super(context, 0, roundIds);
        this.roundIds = roundIds;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.items_game, parent, false);
        }
        // Bind the data to the view
        String roundId = getItem(position);
        LinearLayout bg = convertView.findViewById(R.id.bg);
        Utils.roundBG(bg, Color.parseColor("#202020"),15,5);
        TextView textView = convertView.findViewById(R.id.round_d);
        textView.setText(roundId);

        return convertView;
    }
}
