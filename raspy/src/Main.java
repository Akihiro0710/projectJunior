import googleCalendar.GCalendar;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

/**
 * Created by ta on 2017/10/30.
 */
public class Main extends TimerTask {
    private GCalendar gCalendar;
    private OpenJTalk openJTalk;
    private DateFormat dateFormat;
    private ArrayList<String> alarmList;
    private JLabel label;

    private Main(ArrayList<String> alarmList) {
        String urlString = "https://script.googleusercontent.com/macros/echo?user_content_key=NYHQFaiGWmZA31IYlORB3ThYRqFuBByD7qVpJYTreVIUsyN2kEy2u2fVx_hjw-anYvV30_d_R1mZtUp-a4kqD0lr6O3ffNENm5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnGC-xeM2dr9j-NuciKXdGTi1PqIu8zdweVcN6A3YddqZ7HE3jg8TYSglWkFe0VhTkSyfE_UJfLTU&lib=McZ6v7MtZqzJIOAgZvCnZEVZeC9aZAdjb";
        try {
            gCalendar = new GCalendar(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        openJTalk = new OpenJTalk();
        openJTalk.setTmpDir("/Users/ta/Desktop/test");
        dateFormat = new SimpleDateFormat("MM月dd日E曜日");
        initWindow();
        this.alarmList = alarmList;
    }

    private void initWindow() {
        label = new JLabel();
        new Thread(() -> {
            label.setFont(new Font("Arial", Font.BOLD, 60));
            label.setHorizontalAlignment(JLabel.CENTER);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setBounds(10, 10, 800, 450);
            frame.setTitle("Watch");
            frame.add(label);
            frame.setVisible(true);
        }).start();
    }

    @Override
    public void run() {
        Date date = Calendar.getInstance(Locale.JAPAN).getTime();
        label.setText(new SimpleDateFormat("yyyy年MM月dd日　HH:mm:ss").format(date));
        if (Integer.parseInt(new SimpleDateFormat("ss").format(date)) != 0 || openJTalk.isPlaying()) return;
        if (alarmList.contains(new SimpleDateFormat("HH:mm").format(date))) {
            new Thread(() -> {
                openJTalk.speak("現在、" + dateFormat.format(date) + GCalendar.formatTime(date) + "です。。");
                speakSchedule();
            }).start();
        }
    }

    public void speakSchedule() {
        Object[] events = gCalendar.getGEvents();
        if (events.length == 0) openJTalk.speak("予定はありません。。");
        else openJTalk.speak(events.length + "件の予定があります。。");
        for (Object event : events) openJTalk.speak(event.toString());
    }

    public static void main(String[] args) {
        ArrayList<String> alarmList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.JAPAN);
        for (int i = 0; i < 10; i++) {
            calendar.add(Calendar.MINUTE, 1);
            alarmList.add(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
        }
        Main main = new Main(alarmList);
        Timer t = new Timer();
        t.schedule(main, 0, 1000);
    }
}
