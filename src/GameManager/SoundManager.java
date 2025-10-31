package GameManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundManager {
    private Map<String, List<Clip>> soundEffects = new ConcurrentHashMap<>();
    private Map<String, Integer> clipIndexes = new ConcurrentHashMap<>();
    private Map<String, URL> soundURLs = new ConcurrentHashMap<>();
    private boolean sfxEnabled = true;
    private String currentLoopingSound = null;
    private Clip loopingClip = null;
    private final Object loopLock = new Object();

    private ExecutorService soundExecutor = Executors.newFixedThreadPool(4);
    private ExecutorService loopExecutor = Executors.newSingleThreadExecutor();

    // số lượng âm thanh được phát ra cùng một lúc
    private static final int POOL_SIZE = 5;

    public SoundManager() {
        loadSounds();
    }

    /**
     * Tải âm thanh và nhạc với pool sizes tối ưu
     */
    private void loadSounds() {
        // Frequent collision sounds - need more instances
        loadSound("hit", "/Resource/SoundEffect/hit.wav", 4);
        loadSound("break", "/Resource/SoundEffect/break.wav", 4);
        loadSound("bounce", "/Resource/SoundEffect/bounce.wav", 4);

        // Less frequent sounds - fewer instances
        loadSound("loseHealth", "/Resource/SoundEffect/loseHealth.wav", 2);
        loadSound("lose", "/Resource/SoundEffect/loseHealth.wav", 2);
        loadSound("levelComplete", "/Resource/SoundEffect/levelComplete.wav", 1);
        loadSound("powerUp", "/Resource/SoundEffect/powerUp.wav", 3);

        // Music tracks - only need 1 instance
        loadSound("gameOver", "/Resource/Music/gameOver.wav", 1);
        loadSound("win", "/Resource/Music/win.wav", 1);
        loadSound("background", "/Resource/Music/background.wav", 1);
        loadSound("start", "/Resource/Music/start.wav", 1);
    }

    /**
     * Tải âm thanh nhất định với nhiều instances.
     * @param name tên file
     * @param path đường dẫn lưu
     * @param poolSize số lượng instances đồng thời
     */
    private void loadSound(String name, String path, int poolSize) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Could not find sound file: " + path);
                return;
            }

            soundURLs.put(name, url);
            List<Clip> clipPool = new ArrayList<>();

            // Tạo nhiều instances của cùng một âm thanh
            for (int i = 0; i < poolSize; i++) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clipPool.add(clip);
            }

            soundEffects.put(name, clipPool);
            clipIndexes.put(name, 0);
            System.out.println("Successfully loaded sound: " + name + " (" + poolSize + " instances)");

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
            List<Clip> clips = soundEffects.get(sound);
            if (clips != null) {
                for (Clip clip : clips) {
                    synchronized (clip) {
                        clip.setFramePosition(0);
                    }
                }
            }
        }
    }

    /**
     * Chơi âm thanh.
     * @param name tên âm thanh
     */
    public void playSound(String name) {
        if (!sfxEnabled) {
            return;
        }

        new Thread(() -> {
            try {
                List<Clip> clipPool = soundEffects.get(name);

                if (clipPool == null || clipPool.isEmpty()) {
                    System.err.println("Warning: Attempted to play non-existent sound: " + name);
                    return;
                }

                int index = clipIndexes.get(name);
                Clip clip = clipPool.get(index);

                clipIndexes.put(name, (index + 1) % clipPool.size());

                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                    clip.start();
                }

            } catch (Exception e) {
                System.err.println("Error playing sound " + name + ": " + e.getMessage());
            }
        }, "Sound-" + name).start();
    }

    /**
     * Chơi nhạc background.
     * @param name tên nhạc
     */
    public void playLoopingSound(String name) {
        if (!sfxEnabled) {
            return;
        }

        new Thread(() -> {
            try {
                synchronized (loopLock) {
                    if (currentLoopingSound != null && !currentLoopingSound.equals(name)) {
                        stopSound(currentLoopingSound);
                    }

                    List<Clip> clipPool = soundEffects.get(name);

                    if (clipPool == null || clipPool.isEmpty()) {
                        System.err.println("Warning: Attempted to play non-existent looping sound: " + name);
                        return;
                    }

                    Clip clip = clipPool.get(0);

                    synchronized (clip) {
                        if (clip.isRunning()) {
                            clip.stop();
                        }
                        clip.setFramePosition(0);
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                        currentLoopingSound = name;
                        loopingClip = clip;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error playing looping sound " + name + ": " + e.getMessage());
            }
        }, "LoopSound-" + name).start();
    }

    /**
     * Dừng âm thanh cụ thể.
     * @param name tên âm thanh
     */
    public void stopSound(String name) {
        List<Clip> clipPool = soundEffects.get(name);
        if (clipPool != null) {
            for (Clip clip : clipPool) {
                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                }
            }
        }

        synchronized (loopLock) {
            if (name.equals(currentLoopingSound)) {
                currentLoopingSound = null;
                loopingClip = null;
            }
        }
    }

    /**
     * Kiểm tra âm thanh có đang chơi.
     * @param name tên âm thanh
     * @return true nếu âm thanh đó đang chơi
     */
    public boolean isPlaying(String name) {
        List<Clip> clipPool = soundEffects.get(name);
        if (clipPool == null) {
            return false;
        }

        for (Clip clip : clipPool) {
            if (clip.isRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dừng tất cả các âm thanh.
     */
    public void stopAllSounds() {
        for (Map.Entry<String, List<Clip>> entry : soundEffects.entrySet()) {
            List<Clip> clipPool = entry.getValue();
            if (clipPool != null) {
                for (Clip clip : clipPool) {
                    synchronized (clip) {
                        if (clip.isRunning()) {
                            clip.stop();
                        }
                        clip.setFramePosition(0);
                    }
                }
            }
        }

        synchronized (loopLock) {
            currentLoopingSound = null;
            loopingClip = null;
        }
    }

    /**
     * Xóa tất cả âm thanh khỏi danh sách lưu.
     */
    public void cleanup() {
        stopAllSounds();

        for (List<Clip> clipPool : soundEffects.values()) {
            if (clipPool != null) {
                for (Clip clip : clipPool) {
                    synchronized (clip) {
                        if (clip.isRunning()) {
                            clip.stop();
                        }
                        clip.close();
                    }
                }
            }
        }

        soundEffects.clear();
        clipIndexes.clear();
        soundURLs.clear();

        synchronized (loopLock) {
            currentLoopingSound = null;
            loopingClip = null;
        }
    }
}