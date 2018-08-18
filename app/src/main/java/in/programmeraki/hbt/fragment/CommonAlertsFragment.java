package in.programmeraki.hbt.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.programmeraki.hbt.Common;
import in.programmeraki.hbt.R;
import in.programmeraki.hbt.adapter.AlertsAdapter;
import in.programmeraki.hbt.model.TrackerAlert;

public class CommonAlertsFragment extends Fragment {

    RecyclerView recyclerView;
    TextView noDataTv;
    AlertsAdapter alertsAdapter;

    public int type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv);
        noDataTv= view.findViewById(R.id.no_data_tv);
        alertsAdapter = new AlertsAdapter(type);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(alertsAdapter);

        boolean dataAvail = false;
        if(type == TrackerAlert.pulseType && Common.instance.getPulseTrackerAlerts().size() > 0){
            dataAvail = true;
        } else if(type == TrackerAlert.tempType &&
                Common.instance.getTempTrackerAlerts().size() > 0){
            dataAvail = true;
        }

        if(dataAvail){
            noDataTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noDataTv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }
}
