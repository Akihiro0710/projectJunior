import openJTalk.Helper;
import openJTalk.OpenJTalk;

import java.io.File;
import java.util.Properties;

/**
 * Created by ta on 2017/11/05.
 */
public class Speaker {
    private OpenJTalk openJTalk;

    public Speaker(String speaker) {
        Helper helper = Helper.getHelper();
        helper.setDicDir("/usr/local/Cellar/open-jtalk/1.10_1/dic");
        helper.setVoiceDir("/usr/local/Cellar/open-jtalk/1.10_1/voice");
        String tmpDirPath = "/Users/ta/Desktop/test";
        File file = new File(tmpDirPath);
        if(!file.exists())file.mkdir();
        helper.setTmpDir(tmpDirPath);

        openJTalk = new OpenJTalk();
        openJTalk.setVoice(speaker);
        Properties param = new Properties();
//        param.put("p", "240");
//        param.put("a", "0.5");
//        param.put("r", "0.8");
//        param.put("u", "0.0");
//        param.put("jm", "1.0");
//        param.put("jf", "1.0");
        openJTalk.setProperties(param);
    }

    public void speak(Object[] objects) {
        for (Object object : objects) speak(object);
    }

    public void speak(Object object) {
        speak(object.toString());
    }

    public void speak(String text) {
        openJTalk.speak(text);
    }

    public boolean isPlaying() {
        return openJTalk.isPlaying();
    }
}
