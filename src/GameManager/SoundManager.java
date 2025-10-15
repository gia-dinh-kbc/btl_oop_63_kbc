package GameManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> soundEffects = new HashMap<>();
    private Clip backgroundMusic;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

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
        loadSound("lose", "/Resource/SoundEffect/lose.wav");
        loadSound("gameOver", "/Resource/SoundEffect/gameOver.wav");
        loadSound("win", "/Resource/SoundEffect/win.wav");
        loadBackgroundMusic("/Resource/Music/background.wav");
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Load background music.
     * @param path music path
     */
    private void loadBackgroundMusic(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Could not find music file: " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading background music: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Play sound effect.
     * @param name sound effect name
     */
    public void playSound(String name) {
        if (!sfxEnabled) return;

        Clip clip = soundEffects.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Play background music.
     */
    public void playBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null) return;

        if (!backgroundMusic.isRunning()) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        }
    }

    /**
     * Stop playing background music.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Toggle music.
     */
    public void toggleMusic() {
        musicEnabled = !musicEnabled;
        if (!musicEnabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
    }

    /**
     * Toggle sound effect.
     */
    public void toggleSFX() {
        sfxEnabled = !sfxEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isSFXEnabled() {
        return sfxEnabled;
    }

    /**
     * Set volume.
     * @param volume volume number
     */
    public void setMusicVolume(float volume) {
        setVolume(backgroundMusic, volume);
    }

    /**
     * Set sound effect volume.
     * @param volume volume number
     */
    public void setSFXVolume(float volume) {
        for (Clip clip : soundEffects.values()) {
            setVolume(clip, volume);
        }
    }

    /**
     * Set volume.
     * @param clip sound effect
     * @param volume volume number
     */
    private void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Error setting volume");
            }
        }
    }

    /**
     * Remove music and sound effect.
     */
    public void cleanup() {
        stopBackgroundMusic();
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
    }
}