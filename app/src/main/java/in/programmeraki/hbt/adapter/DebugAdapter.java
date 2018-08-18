package in.programmeraki.hbt.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.programmeraki.hbt.R;
import in.programmeraki.hbt.roomdb.FeedData;

public class DebugAdapter extends BaseAdapter {

    private final Activity activity;
    Context mContext;
    LayoutInflater inflater;
    private List<FeedData> worldpopulationlist = null;
    private ArrayList<FeedData> arraylist;

    public DebugAdapter(Activity activity, List<FeedData> worldpopulationlist) {
        mContext = activity;
        this.activity = activity;
        this.worldpopulationlist = worldpopulationlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<FeedData>();
        this.arraylist.addAll(worldpopulationlist);
    }

    public class ViewHolder {
        TextView timestamp_tv;
        TextView pulse_tv;
        TextView temp_tv;
        TextView raw_feed_tv;
    }

    @Override
    public int getCount() {
        return worldpopulationlist.size();
    }

    @Override
    public FeedData getItem(int position) {
        return worldpopulationlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_debug_adapter, null);
            // Locate the TextViews in listview_item.xml
            holder.timestamp_tv = view.findViewById(R.id.timestamp_tv);
            holder.pulse_tv = view.findViewById(R.id.pulse_tv);
            holder.temp_tv = view.findViewById(R.id.temp_tv);
            holder.raw_feed_tv = view.findViewById(R.id.raw_feed_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.timestamp_tv.setText(worldpopulationlist.get(position).getTimestamp());
        holder.pulse_tv.setText(worldpopulationlist.get(position).getPulse());
        holder.temp_tv.setText(worldpopulationlist.get(position).getTemp());
        holder.raw_feed_tv.setText("N/A");

        return view;
    }

}
