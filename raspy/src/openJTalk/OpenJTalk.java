package openJTalk;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

/**
 * Created by ta on 2017/10/31.
 */
public class OpenJTalk {
    private Properties properties;
    private String voicePath;
    private ArrayList<File> playList;

    public OpenJTalk() {
        properties = new Properties();
        playList = new ArrayList<>();
    }

    public void speak(String text) {
        System.out.println(text);
        String audioPath = generateVoice(text);

        playList.add(new File(audioPath));
        if (playList.size() == 1) play();
    }

    private void play() {
        Thread thread = new Thread(() -> {
            while (playList.size() != 0) {
                File audioFile = playList.get(0);
                if (!audioFile.exists()) continue;
                try {
                    // Read the sound file using AudioInputStream.
                    AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);

                    byte[] buf = new byte[stream.available()];
                    stream.read(buf, 0, buf.length);

                    // Get an AudioFormat object from the stream.
                    AudioFormat format = stream.getFormat();
                    long nBytesRead = format.getFrameSize() * stream.getFrameLength();

                    // Construct a DataLine.Info object from the format.
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

                    // Open and start the line.
                    line.open(format);
                    line.start();

                    // Write the data out to the line.
                    line.write(buf, 0, (int) nBytesRead);

                    // Drain and close the line.
                    line.drain();
                    line.close();
                    playList.remove(0);
                    audioFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private String generateVoice(File scriptFile, UUID uid) {
        String voiceFilePath = getTmpDir() + "/" + uid + ".wav";
        String[] command = generateCommand(voiceFilePath, scriptFile.getAbsolutePath());
        execute(command);
        return voiceFilePath;
    }

    private String generateVoice(String text) {
        UUID uid = UUID.randomUUID();
        String scriptFilePath = generateScript(text, uid);
        String voiceFilePath = generateVoice(new File(scriptFilePath), uid);
        new File(scriptFilePath).delete();
        return voiceFilePath;
    }

    private String[] generateCommand(String voiceFilePath, String textFilePath) {
        Helper helper = Helper.getHelper();
        ArrayList<String> command = new ArrayList<>();
        command.add("open_jtalk");
        command.addAll(Arrays.asList("-x", helper.getDicDir()));
        command.addAll(Arrays.asList("-m", voicePath));
        String[] keys = {"s", "p", "a", "b", "r", "fm", "u", "jm", "jf", "g"};
        for (String key : keys)
            if (properties.get(key) != null)
                command.addAll(Arrays.asList("-" + key, properties.getProperty(key)));

        command.addAll(Arrays.asList("-ow", voiceFilePath));
        command.add(textFilePath);
        return command.toArray(new String[command.size()]);
    }

    public String generateScript(String text, UUID uuid) {
        String path = getTmpDir() + "/" + uuid + ".txt";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.write(text);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public String getTmpDir() {
        return Helper.getHelper().getTmpDir();
    }

    public void setVoice(String voice) {
        Map<String, String> voices = Helper.getHelper().loadVoices();
        this.voicePath = voices.get(voice);
    }

    private String execute(String[] command) {
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, new File(getTmpDir()));
            p.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null)
                output.append(line + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isPlaying() {
        return playList.size() != 0;
    }
}
