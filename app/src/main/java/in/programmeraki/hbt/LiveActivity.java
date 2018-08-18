package in.programmeraki.hbt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import in.programmeraki.hbt.fragment.HistoryActivity;
import in.programmeraki.hbt.model.TrackerAlert;
import in.programmeraki.hbt.nrfkit.profile.BleProfileActivity;
import in.programmeraki.hbt.roomdb.FeedData;
import in.programmeraki.hbt.utils.Constant;
import in.programmeraki.hbt.utils.HRSManager;
import in.programmeraki.hbt.utils.HRSManagerCallbacks;
import in.programmeraki.hbt.utils.NotificationBuilder;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;

public class LiveActivity extends BleProfileActivity
        implements HRSManagerCallbacks, OnChartValueSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private final static String HR_VALUE = "hr_value";
    private final static String HR_POS = "hr_pos";
    private final static int MAX_HR_VALUE = 65535;
    private final static int MIN_POSITIVE_VALUE = 0;
    private final static int REFRESH_INTERVAL = 1000; // 1 second interval
    public static boolean isAlive = false;
    final int pulseAndTempBoth = 1;
    final int pulseOnly = 2;
    final int tempOnly = 3;

    final String pulseDataSetLabel = "Pulse (bpm)";
    final String tempDataSetLabel = "Temp (˚C)";

    private final String TAG = "Main";

    private TextView title_tv;
    private Button action_connect;
    private ImageView back_iv, navheder_imageView;
    private ViewGroup content_vg, topbar_ll, graph_fl;
    private ProgressBar progressBar;
    private TextView current_pulse_tv, today_pulse_peak_tv, current_temp_tv, today_temp_peak_tv, navheadrtitle_tv, battery;
    private int mHrmValue = 0;
    private LineChart mChart;
    private TrackerAlert alertLocal;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Context activity;
    ArrayList<Entry> pulseValues = new ArrayList<>();
    ArrayList<Entry> tempValues = new ArrayList<>();
    LineDataSet pulseDataSet;
    LineDataSet tempDataSet;

    NavigationView navigationView;
    DrawerLayout drawer;

    private LineChart mChart1;

    /*
     * BleActivity Abstract Methods
     * */
    @Override
    protected void onPause() {
        super.onPause();
        isAlive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlive = true;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_live);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        activity = this;
        Constant.selected_frag_id = 2;

        Common.instance.setUpAppDatabase(getApplicationContext());

        action_connect = findViewById(R.id.action_connect);
//        title_tv = findViewById(R.id.title_tv);
//        back_iv = findViewById(R.id.back_iv);
        progressBar = findViewById(R.id.progressBar);
        content_vg = findViewById(R.id.content_vg);
        topbar_ll = findViewById(R.id.topbar_ll);
        graph_fl = findViewById(R.id.graph_fl);
        content_vg = findViewById(R.id.content_vg);
        current_pulse_tv = findViewById(R.id.current_pulse_tv);
        today_pulse_peak_tv = findViewById(R.id.today_pulse_peak_tv);
//        current_temp_tv = findViewById(R.id.current_temp_tv);
        today_temp_peak_tv = findViewById(R.id.today_temp_peak_tv);
        battery = findViewById(R.id.battery);


        View headerView = navigationView.getHeaderView(0);
        navheadrtitle_tv = headerView.findViewById(R.id.navheadrtitle_tv);
        navheder_imageView = headerView.findViewById(R.id.navheder_imageView);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        String imgstr = sharedPreferences.getString("imageString", "");
        String fname = sharedPreferences.getString("fname", "");
        String lname = sharedPreferences.getString("lname", "");

        if(fname != null && !fname.isEmpty() && !fname.equals("null") && lname != null && !lname.isEmpty() && !lname.equals("null")) {
            String Fullname = fname + " " + lname;
            navheadrtitle_tv.setText(Fullname);
        }

        if(imgstr != null && !imgstr.isEmpty() && !imgstr.equals("null")) {
            //decode base64 string to image
            byte[] imageBytes = Base64.decode(imgstr, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            navheder_imageView.setImageBitmap(decodedImage);
        }

        sharedPreferences = getSharedPreferences("MyPref", 0);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getString(Constant.p_min, "").isEmpty()){
            editor.putString(Constant.p_min, Constant.p_min_default);
        }
        if(sharedPreferences.getString(Constant.p_max, "").isEmpty()){
            editor.putString(Constant.p_max, Constant.p_max_default);
        }
        if(sharedPreferences.getString(Constant.t_min, "").isEmpty()){
            editor.putString(Constant.t_min, Constant.t_min_default);
        }
        if(sharedPreferences.getString(Constant.t_max, "").isEmpty()){
            editor.putString(Constant.t_max, Constant.t_max_default);
        }
        editor.putString("deviceconnected", "0");
        editor.apply();

//        title_tv.setOnClickListener(view -> {
//            content_vg.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
            action_connect.setVisibility(View.GONE);
//            setHRSValueOnView(60);
//        });
//
//        back_iv.setOnClickListener(view -> {
//            finish();
//        });

        setUpChart();

        content_vg.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        View.OnClickListener rgClickListener = view -> {
            switch (view.getId()){
                case R.id.both_rg:
                    toggleChartVisibility(pulseAndTempBoth);
                    break;
                case R.id.pulse_rg:
                    toggleChartVisibility(pulseOnly);
                    break;
                case R.id.temp_rg:
                    toggleChartVisibility(tempOnly);
                    break;
            }
        };

        findViewById(R.id.both_rg).setOnClickListener(rgClickListener);
        findViewById(R.id.pulse_rg).setOnClickListener(rgClickListener);
        findViewById(R.id.temp_rg).setOnClickListener(rgClickListener);

        NotificationBuilder.setNotificationInterface(alert -> {
            alertLocal = alert;
            Snackbar mSnackbar = Snackbar.make( findViewById(R.id.root_v), ""+alert.getMsg(),
                    Snackbar.LENGTH_INDEFINITE);
            mSnackbar.setAction("SHOW", new MySnackBarListener());
            mSnackbar.setActionTextColor(getResources().getColor(R.color.tealPrimaryDark));
            mSnackbar.show();
        });
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
        current_pulse_tv.setText(R.string.not_available_value);
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


    /*
     * Custom Methods
     * */
    private void setHRSValueOnView(final int value) {
        Log.d(TAG, "setHRSValueOnView: ");
        runOnUiThread(() -> {
            if (value >= MIN_POSITIVE_VALUE && value <= MAX_HR_VALUE) {
                current_pulse_tv.setText(Integer.toString(value));
            } else {
                current_pulse_tv.setText(R.string.not_available_value);
            }

            String temps = battery.getText().toString().trim();
            temps = temps.substring(0, temps.length() - 2);
            int temp = Integer.parseInt(temps);
            appendLineChartData(value, temp);
            checkForAlert(value);
            storeInDB(value, temp);
            current_pulse_tv.setText(Integer.toString(value));
        });
    }

    private void checkForAlert(int value) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        int p_min = Integer.parseInt(sharedPreferences.getString(Constant.p_min,"0"));
        int p_max = Integer.parseInt(sharedPreferences.getString(Constant.p_max,"0"));
        TrackerAlert tAlert = null;
        if(value >= p_max){
            tAlert = new TrackerAlert();
            tAlert.setMsg("The pulse has crossed " + value + " bpm.");
            tAlert.setCondition(TrackerAlert.maxCondition);
            tAlert.setConditionVal(p_max);
        } else if(value <= p_min){
            tAlert = new TrackerAlert();
            tAlert.setMsg("The pulse is under " + value + " bpm.");
            tAlert.setCondition(TrackerAlert.minCondition);
            tAlert.setConditionVal(p_min);
        }

        if(tAlert != null){
            tAlert.setVal(value);
            tAlert.setType(TrackerAlert.pulseType);
            tAlert.setDatetime(Calendar.getInstance().getTime());
            Common.instance.trackerAlerts.add(tAlert);
            NotificationBuilder.showNotification(
                    tAlert,
                    Common.instance.trackerAlerts.size(),
                    getApplicationContext()
            );
        }
    }

    private void storeInDB(int pulseVal, int tempVal){

        Date dtNow = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate= formatter.format(dtNow);

        FeedData feedData = new FeedData();
        feedData.setFeed_type(FeedData.pulseType);
        feedData.setPulse(pulseVal);
        feedData.setTemp(tempVal);
        feedData.setTimestamp(strDate);

        int timeInLong = (int) Calendar.getInstance().getTime().getTime();
        feedData.setDate_time_in_millis(timeInLong);

        // create worker thread to insert data into database
        new InsertTask(feedData).execute();
    }

    /*
     * Chart Methods
     */
    private void setUpChart(){
        mChart = findViewById(R.id.mChart);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);
//        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum(150f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);

        mChart.getAxisRight().setEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    private void appendLineChartData(int pulse, int temp){
//        Entry pulseEntry = new Entry(Calendar.getInstance().getTime().getTime(), pulse);
//        Entry tempEntry = new Entry(Calendar.getInstance().getTime().getTime(), temp);

        Entry pulseEntry = new Entry(pulseValues.size(), pulse);
        Entry tempEntry = new Entry(tempValues.size(), temp);

        pulseValues.add(pulseEntry);
        tempValues.add(tempEntry);

        if( mChart.getData() != null && mChart.getData().getDataSetCount() > 1){
            mChart.getData().getDataSets().get(0).addEntry(pulseEntry);
            mChart.getData().getDataSets().get(1).addEntry(tempEntry);

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            return;
        }

        // create a dataset and give it a type
        pulseDataSet = new LineDataSet(pulseValues, pulseDataSetLabel);
        tempDataSet = new LineDataSet(tempValues, tempDataSetLabel);

        // set the line to be drawn like this "- - - - - -"
        pulseDataSet.setColor(getResources().getColor(R.color.lorange));
        tempDataSet.setColor(getResources().getColor(R.color.tealSecondaryMid));

        ArrayList<ILineDataSet> chartDataSets = new ArrayList<>();
        chartDataSets.add(pulseDataSet); // add the datasets
        chartDataSets.add(tempDataSet); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(chartDataSets);

        pulseDataSet.setDrawIcons(false);
        tempDataSet.setDrawIcons(false);

        pulseDataSet.setDrawCircles(false);
        tempDataSet.setDrawCircles(false);

//        pulseSet.setCircleColor(getResources().getColor(R.color.redPrimaryDark));
//        tempSet.setCircleColor(getResources().getColor(R.color.orangePrimaryDark));

        pulseDataSet.setLineWidth(3f);
        tempDataSet.setLineWidth(3f);

//        pulseDataSet.setCircleRadius(3f);
//        tempDataSet.setCircleRadius(3f);

//        pulseDataSet.setDrawCircleHole(false);
//        tempDataSet.setDrawCircleHole(false);

//        pulseDataSet.setValueTextSize(9f);
//        tempDataSet.setValueTextSize(9f);

        pulseDataSet.setDrawFilled(true);
        tempDataSet.setDrawFilled(true);

        pulseDataSet.setFormLineWidth(1f);
        tempDataSet.setFormLineWidth(1f);

        pulseDataSet.setFormSize(15.f);
        tempDataSet.setFormSize(15.f);

        pulseDataSet.setFillColor(Color.WHITE);
        tempDataSet.setFillColor(Color.WHITE);

        // set data
        mChart.setData(data);
    }

    private void toggleChartVisibility(int mode){
        if (mode == pulseAndTempBoth) {
            if (mChart.getData().getDataSetByLabel(pulseDataSetLabel, true) == null ){
                mChart.getData().addDataSet(pulseDataSet);
            }
            if (mChart.getData().getDataSetByLabel(tempDataSetLabel, true) == null ){
                mChart.getData().addDataSet(tempDataSet);
            }
        } else if (mode == pulseOnly) {
            if (mChart.getData().getDataSetByLabel(tempDataSetLabel, true) != null) {
                mChart.getData().removeDataSet(tempDataSet);
            }
            if (mChart.getData().getDataSetByLabel(pulseDataSetLabel, true) == null){
                mChart.getData().addDataSet(pulseDataSet);
            }
        } else if (mode == tempOnly){
            if (mChart.getData().getDataSetByLabel(pulseDataSetLabel, true) != null ) {
                mChart.getData().removeDataSet(pulseDataSet);
            }
            if (mChart.getData().getDataSetByLabel(tempDataSetLabel, true) == null ){
                mChart.getData().addDataSet(tempDataSet);
            }
        }
        mChart.getData().notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }


    /*
     * HRS Manager Callbacks
     * */
    @Override
    public void onHRSensorPositionFound(BluetoothDevice device, String position) {
        Log.d(TAG, "onHRSensorPositionFound: " + device.getAddress() + ", pos: " + position);
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
            action_connect.setVisibility(View.GONE);
            Toast.makeText(this, "Device Connected", Toast.LENGTH_SHORT).show();
            content_vg.setVisibility(View.VISIBLE);

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
            current_pulse_tv.setText(R.string.not_available_value);
            current_pulse_tv.setText(R.string.not_available);
            Toast.makeText(this, "Device Disconnected", Toast.LENGTH_SHORT).show();

            sharedPreferences = getSharedPreferences("MyPref", 0);
            editor = sharedPreferences.edit();
            editor.putString("deviceconnected", "0");
            editor.commit();

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

    /*
     * Chart implemented methods
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String eVal = new SimpleDateFormat("HH:mm:ss").format(new Date((long) e.getX()));
        Toast.makeText(this, "time: " + eVal + ", value: " +e.getY(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        Fragment fragment = null;
//
        if (id == R.id.nav_alerts) {
            Intent intent = new Intent(this, AlertsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_history) {
            // Handle the camera action
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);

        } else if (id == R.id.nav_debug) {
            Intent intent = new Intent(this, DebugActivity.class);
            startActivity(intent);

        }

//        displaySelectedScreen(item.getItemId());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class MySnackBarListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(activity);
            }
            builder.setTitle(R.string.critical_alert_str)
                    .setMessage(alertLocal.getMsg())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private static class InsertTask extends AsyncTask<Void,Void,Boolean> {
        private FeedData feedData;
        private String TAG = "db_ops";

        // only retain a weak reference to the activity
        InsertTask(FeedData note) {
            this.feedData = note;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            Common.instance.getAppDatabase().feedDataDao().insertAll(feedData);
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                Log.d(TAG, "onPostExecute: saved in db, total rows:" +
                        String.valueOf(Common.instance.getAppDatabase().
                                feedDataDao().numberOfRows()));
            }
        }
    }

}
