package in.programmeraki.hbt.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class FeedData {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @ColumnInfo(name = "feed_type")
    private int feed_type;

    @ColumnInfo(name = "pulse")
    private int pulse;

    @ColumnInfo(name = "temp")
    private int temp;

    @ColumnInfo(name = "raw_data")
    private String raw_data;

    @ColumnInfo(name = "date_time_in_millis")
    private int date_time_in_millis;

    @Ignore
    public static final int pulseType = 1;

    @Ignore
    public static final int tempType = 2;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(String raw_data) {
        this.raw_data = raw_data;
    }

    public int getFeed_type() {
        return feed_type;
    }

    public void setFeed_type(int feed_type) {
        this.feed_type = feed_type;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getDate_time_in_millis() {
        return date_time_in_millis;
    }

    public void setDate_time_in_millis(int date_time_in_millis) {
        this.date_time_in_millis = date_time_in_millis;
    }
}
