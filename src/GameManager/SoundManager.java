package GameManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundManager {
    private Map<String, Clip> soundEffects = new HashMap<>();
    private boolean sfxEnabled = true;
    private ExecutorService audioExecutor = Executors.newCachedThreadPool();

    public SoundManager() {
        loadSounds();
    }

    /**
     * Load game sounds.
     */
    private void loadSounds() {
        loadSound("hit", "/Resource/SoundEffect/hit.wav");
        loadSound("break", "/Resource/SoundEffect/break.wav");
        loadSound("bounce", "/Resource/SoundEffect/bounce.wav");
        loadSound("loseHealth", "/Resource/SoundEffect/loseHealth.wav");
        loadSound("lose", "/Resource/SoundEffect/loseHealth.wav");
        loadSound("gameOver", "/Resource/Music/gameOver.wav");
        loadSound("win", "/Resource/Music/win.wav");
        loadSound("background", "/Resource/Music/background.wav");
        loadSound("start", "/Resource/Music/start.wav");
    }

    /**
     * load specific game sound.
     * @param name file name
     * @param path path
     */
    private void loadSound(String name, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Could not find sound file: " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundEffects.put(name, clip);
            System.out.println("Successfully loaded sound: " + name);
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format for: " + path);
            System.err.println("The file might not be a valid WAV file or uses an unsupported encoding.");
            System.err.println("Try converting it to PCM WAV format (16-bit, 44100Hz)");
        } catch (IOException e) {
            System.err.println("IO Error loading sound: " + path);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for: " + path);
            e.printStackTrace();
        }
    }

    public void preLoadAllSound() {
        for (String sound : soundEffects.keySet()) {
            Clip clip = soundEffects.get((sound));
            if (clip != null) {
                synchronized (clip) {
                    clip.setFramePosition(0);
                }
            }
        }
    }

    /**
     * Play sound effect asynchronously.
     * @param name sound effect name
     */
    public void playSound(String name) {
        if (!sfxEnabled) {
            return;
        }

        audioExecutor.execute(() -> {
            Clip clip = soundEffects.get(name);

            if (clip != null) {
                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                    clip.start();
                }
            } else {
                System.err.println("Warning: Attempted to play non-existent sound: " + name);
            }
        });
    }

    /**
     * Play sound with looping for background music.
     * @param name sound effect name
     */
    public void playLoopingSound(String name) {
        if (!sfxEnabled) {
            return;
        }

        audioExecutor.execute(() -> {
            Clip clip = soundEffects.get(name);

            if (clip != null) {
                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        });
    }

    /**
     * Check if a sound is currently playing.
     * @param name sound name
     * @return true if playing
     */
    public boolean isPlaying(String name) {
        Clip clip = soundEffects.get(name);
        return clip != null && clip.isRunning();
    }

    /**
     * Stop all currently playing sound effects.
     */
    public void stopAllSounds() {
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                }
            }
        }
    }

    /**
     * Remove music and sound effect.
     */
    public void cleanup() {
        audioExecutor.shutdown();
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.close();
                }
            }
        }
    }
}