package in.programmeraki.hbt;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.ArrayList;

import in.programmeraki.hbt.model.TrackerAlert;
import in.programmeraki.hbt.roomdb.AppDatabase;

public class Common extends Application {

    public static Common instance = new Common();
    public ArrayList<TrackerAlert> trackerAlerts = new ArrayList<>();
    AppDatabase db;

    public ArrayList<TrackerAlert> getPulseTrackerAlerts() {
        ArrayList<TrackerAlert> temp = new ArrayList<>();
        for (int i = 0; i < trackerAlerts.size(); i++) {
            if (trackerAlerts.get(i).getType() == TrackerAlert.pulseType) {
                temp.add(trackerAlerts.get(i));
            }
        }
        return temp;
    }

    public ArrayList<TrackerAlert> getTempTrackerAlerts() {
        ArrayList<TrackerAlert> temp = new ArrayList<>();
        for (int i = 0; i < trackerAlerts.size(); i++) {
            if (trackerAlerts.get(i).getType() == TrackerAlert.tempType) {
                temp.add(trackerAlerts.get(i));
            }
        }
        return temp;
    }


    public AppDatabase getAppDatabase() {
        return db;
    }

    public void cleanUp(){
        db = null;
    }

    public void setUpAppDatabase(Context context) {
        if(db != null){
            return;
        }
        db = Room.databaseBuilder(context, AppDatabase.class,
                "BabyMonitor").allowMainThreadQueries().build();
    }
}
