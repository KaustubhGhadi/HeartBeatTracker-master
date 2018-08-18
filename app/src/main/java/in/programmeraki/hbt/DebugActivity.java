package in.programmeraki.hbt;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import in.programmeraki.hbt.adapter.BLEFeedAdapter;
import in.programmeraki.hbt.adapter.DebugAdapter;
import in.programmeraki.hbt.model.BLEFeedData;
import in.programmeraki.hbt.nrfkit.profile.BleProfileActivity;
import in.programmeraki.hbt.roomdb.FeedData;
import in.programmeraki.hbt.utils.HRSManager;
import in.programmeraki.hbt.utils.HRSManagerCallbacks;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;

public class DebugActivity extends BleProfileActivity implements HRSManagerCallbacks {

    private final static String HR_VALUE = "hr_value";
    private final static String HR_POS = "hr_pos";

    private final static int MAX_HR_VALUE = 65535;
    private final static int MIN_POSITIVE_VALUE = 0;
    private final static int REFRESH_INTERVAL = 1000; // 1 second interval

    private final String TAG = "Main";
    BLEFeedAdapter bleFeedAdapter;
    BLEFeedAdapter bleFeedAdapter_old;
    private Handler mHandler = new Handler();
    private TextView title_tv, clear_db_tv;
    private Button sync_btn, action_connect;
    private ImageView back_iv;
    private ViewGroup content_vg, topbar_ll;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView mHRSValue, mHRSPosition;
    private int mHrmValue = 0;
    private int mCounter = 0;
    private Handler rvHandler;


    /*
     * BleActivity Abstract Methods
     * */
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_debug);

        Common.instance.setUpAppDatabase(getApplicationContext());

        title_tv = findViewById(R.id.title_tv);
        action_connect = findViewById(R.id.action_connect);
        sync_btn = findViewById(R.id.sync_btn);
        back_iv = findViewById(R.id.back_iv);
        progressBar = findViewById(R.id.progressBar);
        content_vg = findViewById(R.id.content_vg);
        topbar_ll = findViewById(R.id.topbar_ll);
        recyclerView = findViewById(R.id.recyclerView);
        mHRSValue = findViewById(R.id.text_hrs_value);
        mHRSPosition = findViewById(R.id.text_hrs_position);
        clear_db_tv = findViewById(R.id.clear_db_tv);

        progressBar.setVisibility(View.GONE);
        bleFeedAdapter = new BLEFeedAdapter();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        String deviceconnected = sharedPreferences.getString("deviceconnected", "");

        if (deviceconnected.equals("0")) {
//            Toast.makeText(this, "Device Disconnected", Toast.LENGTH_SHORT).show();

            getData();

            recyclerView.setAdapter(bleFeedAdapter_old);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        else
        {
            recyclerView.setAdapter(bleFeedAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        sync_btn.setOnClickListener(view -> {
//            startActivity(new Intent(this, LiveActivity.class));
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(R.string.critical_alert_str)
                    .setMessage("Under Development")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        back_iv.setOnClickListener(view -> {
            finish();
        });

        clear_db_tv.setOnClickListener(view -> {
            Common.instance.getAppDatabase().feedDataDao().deleteAll();
            Log.d(TAG, "onCreateView: totalRows:" + String.valueOf(Common.instance.getAppDatabase().feedDataDao().numberOfRows()));
        });

        rvHandler = new Handler();
    }

    @Override
    protected BleManager<? extends BleManagerCallbacks> initializeManager() {
        final HRSManager manager = HRSManager.getInstance(getApplicationContext());
        manager.setGattCallbacks(this);
        return manager;
    }

    @Override
    protected void setDefaultUI() {
        Log.d(TAG, "setDefaultUI: ");
        mHRSValue.setText(R.string.not_available_value);
        mHRSPosition.setText(R.string.not_available);
    }

    @Override
    protected int getDefaultDeviceName() {
        Log.d(TAG, "getDefaultDeviceName: ");
        return R.string.hrs_default_name;
    }

    @Override
    protected int getAboutTextId() {
        Log.d(TAG, "getAboutTextId: ");
        return R.string.hrs_about_text;
    }

    @Override
    protected UUID getFilterUUID() {
        Log.d(TAG, "getFilterUUID: ");
        return HRSManager.HR_SERVICE_UUID;
    }

    public void getData() {
        ArrayList<FeedData> allFeedData = (ArrayList<FeedData>) Common.instance.getAppDatabase().feedDataDao().getAll();
        for (int i = 0; i < allFeedData.size(); i++) {
            FeedData feedData = allFeedData.get(i);
            String timestamp = feedData.getTimestamp();
            String pulse = String.valueOf(feedData.getPulse());
            String temp = String.valueOf(feedData.getTemp());

            bleFeedAdapter_old = new BLEFeedAdapter();
            bleFeedAdapter_old.appendNewFeed(new BLEFeedData(timestamp, ""+pulse, temp, "N/A"));
            recyclerView.scrollToPosition(0);
        }
    }


    /*
     * Custom Methods
     * */
    private void setHRSValueOnView(final int value) {
        Log.d(TAG, "setHRSValueOnView: ");
        runOnUiThread(() -> {
            if (value >= MIN_POSITIVE_VALUE && value <= MAX_HR_VALUE) {
                mHRSValue.setText(Integer.toString(value));
            } else {
                mHRSValue.setText(R.string.not_available_value);
            }

            Date dtNow = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String strDate= formatter.format(dtNow);
            bleFeedAdapter.appendNewFeed(new BLEFeedData(strDate, ""+value, "N/A", "N/A"));
            recyclerView.scrollToPosition(0);
        });
    }

    private void setHRSPositionOnView(final String position) {
        Log.d(TAG, "setHRSPositionOnView: ");
        runOnUiThread(() -> {
            if (position != null) {
                mHRSPosition.setText(position);
            } else {
                mHRSPosition.setText(R.string.not_available);
            }
        });
    }

    /*
     * HRS Manager Callbacks
     * */
    @Override
    public void onHRSensorPositionFound(BluetoothDevice device, String position) {
        Log.d(TAG, "onHRSensorPositionFound: " + device.getAddress() + ", pos: " + position);
        setHRSPositionOnView(position);
    }

    @Override
    public void onHRValueReceived(BluetoothDevice device, int value) {
        Log.d(TAG, "onHRValueReceived: " + device.getAddress() + ", val: " + value);
        mHrmValue = value;
        setHRSValueOnView(mHrmValue);
    }

    /*
     * BleActivity Override Methods
     * */
    @Override
    public void onServicesDiscovered(BluetoothDevice device, boolean optionalServicesFound) {
        super.onServicesDiscovered(device, optionalServicesFound);
        Log.d(TAG, "onServicesDiscovered: ");
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        super.onDeviceConnected(device);
        runOnUiThread(() ->{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Device Connected", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {
        super.onDeviceReady(device);
        Log.d(TAG, "onDeviceReady: ");
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        super.onDeviceDisconnected(device);
        Log.d(TAG, "onDeviceDisconnected: ");
        runOnUiThread(() -> {
            mHRSValue.setText(R.string.not_available_value);
            mHRSPosition.setText(R.string.not_available);
            Toast.makeText(this, "Device Disconnected", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDeviceConnecting(BluetoothDevice device) {
        super.onDeviceConnecting(device);
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(HR_VALUE, mHrmValue);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHrmValue = savedInstanceState.getInt(HR_VALUE);
    }


}
