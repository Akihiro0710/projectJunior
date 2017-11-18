package openJTalk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ta on 2017/11/03.
 */
public class Helper {
    private static Helper helper = new Helper();
    private String tmpDir;
    private File voiceDir;
    private String dicDir;

    private Helper(){
    }

    public static Helper getHelper() {
        return helper;
    }

    String getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    File getVoiceDir() {
        return voiceDir;
    }

    public void setVoiceDir(String voiceDirPath){
        this.voiceDir = new File(voiceDirPath);
    }
    public void setVoiceDir(File voiceDir) {
        this.voiceDir = voiceDir;
    }

    String getDicDir() {
        return dicDir;
    }

    public void setDicDir(String dicDir) {
        this.dicDir = dicDir;
    }

    Map<String, String> loadVoices(){
        return loadVoices(getVoiceDir());
    }
    Map<String, String> loadVoices(File voiceDir) {
        Map<String, String> voice = new HashMap<>();
        for (File files : voiceDir.listFiles()) {
            if (!files.isDirectory()) continue;
            for (File file : files.listFiles()) {
                if (isHtsvoice(file)) {
                    String fileName = file.getName();
                    String key = fileName.substring(0, fileName.lastIndexOf('.'));
                    String value = file.getPath();
                    voice.put(key, value);
                }
            }
        }
        return voice;
    }

    private static boolean isHtsvoice(File file) {
        if (!file.isFile()) return false;
        String regex = "^.*\\.([^.]+)$";
        return file.toString().replaceAll(regex, "$1").equals("htsvoice");
    }
}
