package googleCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ta on 2017/11/01.
 */
public class GCalendar {
    private URL gasUrl;
    public GCalendar(String urlString) throws MalformedURLException {
        gasUrl = new URL(urlString);
    }
    public GEvent[] getGEvents(){
        String jsonString = getResponse();
        JSONArray json;
        try {
            json = new JSONArray(jsonString);
            GEvent[] gEvents = new GEvent[json.length()];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                gEvents[i] = new GEvent(jsonObject);
            }
            return gEvents;
        } catch (JSONException e) {
            e.printStackTrace();
            return new GEvent[]{};
        }
    }
    private String getResponse() {
        HttpURLConnection connection = null;
        BufferedReader br = null;
        String result = "";
        try {
            connection = (HttpURLConnection) gasUrl.openConnection();
            connection.connect();

            br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            while (true) {
                String tmp = br.readLine();
                if (tmp == null) break;
                result += tmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static String formatTime(Date date) {
        String time = "";
        int h = Integer.parseInt(new SimpleDateFormat("HH").format(date));
        int m = Integer.parseInt(new SimpleDateFormat("mm").format(date));
        time += h + "時";
        if (m != 0) time += m + "分";
        return time;
    }
}
