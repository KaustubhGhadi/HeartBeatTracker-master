package in.programmeraki.hbt.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;

import in.programmeraki.hbt.Common;
import in.programmeraki.hbt.R;
import in.programmeraki.hbt.model.BLEFeedData;
import in.programmeraki.hbt.roomdb.FeedData;

public class BLEFeedAdapter extends RecyclerView.Adapter<BLEFeedAdapter.MyViewHolder> {

    ArrayList<BLEFeedData> bleFeedDataArr = new ArrayList<>();
    String timestamp = "";
    String Pulse = "";
    String Temp = "";

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item_raw_feed, null, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        timestamp = bleFeedDataArr.get(position).getTimestamp();
        Pulse = bleFeedDataArr.get(position).getPulse();
        Temp = bleFeedDataArr.get(position).getTemp();
        holder.timestamp_tv.setText(timestamp);
        holder.pulse_tv.setText(Pulse);
        holder.temp_tv.setText(Temp);
        holder.raw_feed_tv.setText("N/A");
    }

    @Override
    public int getItemCount() {
        return bleFeedDataArr.size();
    }

    public void appendNewFeed(BLEFeedData data){
        bleFeedDataArr.add(0, data);
        this.notifyItemInserted(0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp_tv;
        TextView pulse_tv;
        TextView temp_tv;
        TextView raw_feed_tv;

        MyViewHolder(View itemView) {
            super(itemView);
            timestamp_tv = itemView.findViewById(R.id.timestamp_tv);
            pulse_tv = itemView.findViewById(R.id.pulse_tv);
            temp_tv = itemView.findViewById(R.id.temp_tv);
            raw_feed_tv = itemView.findViewById(R.id.raw_feed_tv);
        }
    }
}
