package in.programmeraki.hbt.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import in.programmeraki.hbt.Common;
import in.programmeraki.hbt.R;
import in.programmeraki.hbt.model.TrackerAlert;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.MyViewHolder>{

    private int type;

    public AlertsAdapter(int type) {
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_alerts, null, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TrackerAlert alert;
        if (type == TrackerAlert.pulseType) {
            alert = Common.instance.getPulseTrackerAlerts().get(position);
        } else {
            alert = Common.instance.getTempTrackerAlerts().get(position);
        }
        holder.msg_tv.setText(alert.getMsg());
        holder.val_tv.setText(alert.getVal()+"");
        holder.datetime_tv.setText(alert.getDatetimeString());

        if(alert.getCondition() == TrackerAlert.maxCondition){
            holder.condition_tv.setText("Value greater then "+ alert.getConditionVal() + " bpm");
        } else {
            holder.condition_tv.setText("Value lower then "+ alert.getConditionVal() + " bpm");
        }
    }

    @Override
    public int getItemCount() {
        if (type == TrackerAlert.pulseType) {
            return Common.instance.getPulseTrackerAlerts().size();
        } else {
            return Common.instance.getTempTrackerAlerts().size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView msg_tv, datetime_tv, val_tv, condition_tv;

        MyViewHolder(View itemView) {
            super(itemView);
            msg_tv = itemView.findViewById(R.id.msg_tv);
            datetime_tv = itemView.findViewById(R.id.datetime_tv);
            val_tv = itemView.findViewById(R.id.val_tv);
            condition_tv = itemView.findViewById(R.id.condition_tv);
        }
    }
}
