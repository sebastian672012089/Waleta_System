package waleta_system;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Scanner;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.*;

public class SoundJLayer extends PlaybackListener implements Runnable {

    private final String url;
    private AdvancedPlayer player;
    private Thread playerThread;
    private AudioDevice device;
    private FloatControl volControl;
    private boolean loop = false;

    public SoundJLayer(String url) {
        this.url = url;
    }

    public SoundJLayer(String url, boolean loop) {
        this.url = url;
        this.loop = loop;
    }

    public void play() {
        try {
            JavaSoundAudioDevice a = new JavaSoundAudioDevice();
            this.device = FactoryRegistry.systemRegistry().createAudioDevice();
            this.player = new AdvancedPlayer(new FileInputStream(new File(url)), device);
            this.player.setPlayBackListener(this);
            this.playerThread = new Thread(this, "AudioPlayerThread");
            this.playerThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String line = "";
        try (Scanner scanner = new Scanner(System.in)) {
            while (!line.equals("q")) {
                line = scanner.nextLine();

                if (line.matches("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$")) {
                    setVolume(Float.valueOf(line));
                }
            }
            this.player.stop();
        }
    }

    // PlaybackListener members
    @Override
    public void playbackStarted(PlaybackEvent playbackEvent) {
    }

    @Override
    public void playbackFinished(PlaybackEvent playbackEvent) {
        if (loop) {
            play();
        }
    }
    
    public void close() {
        this.player.close();
    }

    public void setVolume(float gain) {
        if (this.volControl == null) {
            Class<JavaSoundAudioDevice> clazz = JavaSoundAudioDevice.class;
            Field[] fields = clazz.getDeclaredFields();
            try {
                SourceDataLine source = null;
                for (Field field : fields) {
                    if ("source".equals(field.getName())) {
                        field.setAccessible(true);
                        source = (SourceDataLine) field.get(this.device);
                        field.setAccessible(false);
                        this.volControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.volControl != null) {
            float newGain = Math.min(Math.max(gain, volControl.getMinimum()), volControl.getMaximum());
            volControl.setValue(newGain);
        }
    }

    @Override
    public void run() {
        try {
            this.player.play();
        } catch (JavaLayerException ex) {
            ex.printStackTrace();
        }
    }
}
