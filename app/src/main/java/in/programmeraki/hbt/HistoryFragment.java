package in.programmeraki.hbt;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;

import in.programmeraki.hbt.roomdb.FeedData;

public class HistoryFragment extends Fragment implements OnChartValueSelectedListener{

    public boolean isLastWeekType = false;
    final String pulseDataSetLabel = "Pulse (bpm)";
    final String tempDataSetLabel = "Temp (˚C)";
    final int pulseAndTempBoth = 1;
    final int pulseOnly = 2;
    final int tempOnly = 3;

    private Context activity;
    ArrayList<Entry> pulseValues = new ArrayList<>();
    ArrayList<Entry> tempValues = new ArrayList<>();
    LineDataSet pulseDataSet;
    LineDataSet tempDataSet;
    ViewGroup graph_fl;
    private LineChart mChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graph_fl = view.findViewById(R.id.graph_fl);
        mChart = view.findViewById(R.id.mChart);
        setUpChart();

        makeGraphFromDatabase();
    }

    private void makeGraphFromDatabase(){
        ArrayList<FeedData> allFeedData = (ArrayList<FeedData>) Common.instance.getAppDatabase().feedDataDao().getAll();
        for (int i = 0; i < allFeedData.size(); i++) {
            FeedData feedData = allFeedData.get(i);
            appendLineChartData(feedData.getPulse(), feedData.getTemp());
        }
    }

    /*
     * Chart Methods
     */
    private void setUpChart(){
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
            if (mChart.getData().getDataSetByLabel(pulseDataSetLabel, true) == null ){
                mChart.getData().addDataSet(pulseDataSet);
            }
        } else if (mode == tempOnly){
            if (mChart.getData().getDataSetByLabel(pulseDataSetLabel, true) != null) {
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
    }
}
