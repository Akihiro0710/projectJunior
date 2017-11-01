import javax.sound.sampled.*;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Created by ta on 2017/10/31.
 */
public class OpenJTalk {
    private String tmpDir;
    private Properties properties;
    private ArrayList<File> playList;

    public OpenJTalk() {
        tmpDir = "temp";
        Properties param = new Properties();
        param.put("x", "default");
        param.put("m", "default");
        setProperties(param);
        playList = new ArrayList<>();
    }

    public void speak(String text) {
        System.out.println(text);
        String audioPath = generateVoice(text);

        playList.add(new File(audioPath));
        if(playList.size() == 1) play();
    }

    private void play() {
        Thread thread = new Thread(() -> {
            while (playList.size() != 0) {
                try {

                    File audioFile = playList.get(0);
                    if (!audioFile.exists()) continue;
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
        String voiceFilePath = tmpDir + "/" + uid + ".wav";
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
        if (properties.size() == 0 || (properties.get("x") == null || properties.get("m") == null))
            throw new InvalidParameterException();
        Map<String, String> dictionary = loadDictionaries();
        Map<String, String> voices = loadVoices();

        String[] command = {"open_jtalk"};
        String[] option;
        option = new String[]{"-x", dictionary.get(properties.get("x"))};
        command = mergeArray(command, option);
        option = new String[]{"-m", voices.get(properties.get("m"))};
        command = mergeArray(command, option);
        String[] keys = {"s", "p", "a", "b", "r", "fm", "u", "jm", "jf", "g"};
        for (String key : keys) {
            if (properties.get(key) != null) {
                option = new String[]{"-" + key, String.valueOf(properties.get(key))};
                command = mergeArray(command, option);
            }
        }
        option = new String[]{"-ow", voiceFilePath};
        command = mergeArray(command, option);
        command = mergeArray(command, new String[]{textFilePath});
        return command;
    }

    public String generateScript(String text, UUID uuid) {
        String path = tmpDir + "/" + uuid + ".txt";
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
        return tmpDir;
    }

    private static Map<String, String> loadDictionaries() {
        Map<String, String> dic = new HashMap<>();
//        File f = new File("dictionary");
//        for (File file : f.listFiles()) {
//            if (file.isDirectory())
//                dic.put(file.getName(), file.toString());
//        }
        dic.put("default", "/usr/local/Cellar/open-jtalk/1.10_1/dic");
        return dic;
    }

    private static Map<String, String> loadVoices() {
        Map<String, String> voice = new HashMap<>();
//        File f = new File("voices");
//        for (File files : f.listFiles())
//            if (files.isDirectory())
//                for (File subfile : files.listFiles())
//                    if (subfile.isFile()
//                            && subfile.toString()
//                            .replaceAll("^.*\\.([^.]+)$", "$1")
//                            .equals("htsvoice"))
//                        voice.put(
//                                subfile.getName().substring(0,
//                                        subfile.getName().lastIndexOf('.')),
//                                subfile.getPath());

        voice.put("default", "/usr/local/Cellar/open-jtalk/1.10_1/voice/m100/nitech_jp_atr503_m001.htsvoice");
        return voice;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    private String execute(String[] command) {
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, new File(tmpDir));
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

    private String[] mergeArray(String[] array1, String[] array2) {
        String[][] arrays = {array1, array2};
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] mergedArray = new String[length];
        int lastIndex = 0;
        for (String[] array : arrays) {
            System.arraycopy(array, 0, mergedArray, lastIndex, array.length);
            lastIndex += array.length;
        }
        return mergedArray;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isPlaying() {
        return playList.size() != 0;
    }
}
