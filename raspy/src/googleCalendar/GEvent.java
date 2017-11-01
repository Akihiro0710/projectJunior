package googleCalendar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ta on 2017/11/01.
 */
class GEvent {
    private String title;
    private Date startTime;
    private Date endTime;
    private String description;

    GEvent(JSONObject json) {
        try {
            title = json.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            DateFormat format = new SimpleDateFormat("HH:mm");
            startTime = format.parse(json.getString("startTime"));
            endTime = format.parse(json.getString("endTime"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            startTime = null;
            endTime = null;
        }
    }

    boolean isAllDay() {
        return startTime == null && endTime == null;
    }

    @Override
    public String toString() {
        String string = "";
        if (isAllDay()) string += "終日、";
        else {
            string += GCalendar.formatTime(startTime) + "から";
            string += GCalendar.formatTime(endTime) + "まで、";
        }
        string += "「" + title + "」";
        string += description+"。。";
        return string;
    }
}
