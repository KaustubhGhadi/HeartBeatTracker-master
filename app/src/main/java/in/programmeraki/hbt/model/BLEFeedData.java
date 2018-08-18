package in.programmeraki.hbt.model;

public class BLEFeedData {
    private String timestamp;
    private String pulse;
    private String temp;
    private String raw_data;

    public BLEFeedData(String timestamp, String pulse, String temp, String raw_data) {
        this.timestamp = timestamp;
        this.pulse = pulse;
        this.temp = temp;
        this.raw_data = raw_data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(String raw_data) {
        this.raw_data = raw_data;
    }
}
