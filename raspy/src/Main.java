import googleCalendar.GCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

/**
 * Created by ta on 2017/10/30.
 */
public class Main implements KeyListener, MouseListener {
    private GCalendar gCalendar;
    private JLabel label;

    private Speaker speaker;

    private Main() {
        speaker = new Speaker("mei_normal");
        String urlString = "https://script.googleusercontent.com/macros/echo?user_content_key=NYHQFaiGWmZA31IYlORB3ThYRqFuBByD7qVpJYTreVIUsyN2kEy2u2fVx_hjw-anYvV30_d_R1mZtUp-a4kqD0lr6O3ffNENm5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnGC-xeM2dr9j-NuciKXdGTi1PqIu8zdweVcN6A3YddqZ7HE3jg8TYSglWkFe0VhTkSyfE_UJfLTU&lib=McZ6v7MtZqzJIOAgZvCnZEVZeC9aZAdjb";
        try {
            gCalendar = new GCalendar(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        initWindow();
    }

    private void initWindow() {
        label = new JLabel();
        new Thread(() -> {
            label.setFont(new Font("Arial", Font.BOLD, 120));
            label.setHorizontalAlignment(JLabel.CENTER);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setBounds(10, 10, 800, 450);
            frame.setTitle("Watch");
            frame.add(label);
            frame.addKeyListener(this);
            frame.addMouseListener(this);
            frame.setVisible(true);
        }).start();
    }

    private static String generateTimeText(Date date) {
        String timeText = "";
        timeText += new SimpleDateFormat("MM月dd日E曜日").format(date);
        timeText += GCalendar.formatTime(date);
        return "現在" + timeText + "です。";
    }

    private void execute() {
        if (speaker.isPlaying()) {
            System.out.println("実行中です");
            return;
        }

        Date date = Calendar.getInstance(Locale.JAPAN).getTime();
        new Thread(() -> {
            speaker.speak(generateTimeText(date));
            Object[] events = gCalendar.getGEvents();
            if (events.length == 0) speaker.speak("予定はありません。");
            else speaker.speak(events.length + "件の予定があります。");
            speaker.speak(events);
        }).start();
    }

    public static void main(String[] args) {
        Main main = new Main();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Date date = Calendar.getInstance(Locale.JAPAN).getTime();
                main.label.setText(new SimpleDateFormat("HH:mm:ss").format(date));
                if (Integer.parseInt(new SimpleDateFormat("ss").format(date)) == 0
                        && Integer.parseInt(new SimpleDateFormat("mm").format(date)) % 5 == 0)
                    main.execute();
            }
        }, 0, 1000);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') execute();
        System.out.println(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        execute();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
