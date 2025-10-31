package GameManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String FILE_PATH = "src/Resource/DataFiles/highscores.txt";
    private static final int MAX_SCORES = 5;
    private List<Integer> scores;

    public ScoreManager() {
        scores = new ArrayList<>();
        loadScores();

        // Ensure scores are sorted even after loading from file
        sortAndTrimScores();
    }

    private void loadScores() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    int score = Integer.parseInt(line);
                    // Only add non-negative scores
                    if (score >= 0) {
                        scores.add(score);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score format: " + line);
                }
            }
            System.out.println("Loaded " + scores.size() + " scores from file");
        } catch (IOException e) {
            System.out.println("No existing score file found, will create new one");
        }
    }

    private void saveScores() {
        try {
            File file = new File(FILE_PATH);

            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Integer score : scores) {
                    bw.write(score + "\n");
                }
                System.out.println("Saved " + scores.size() + " scores to: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error saving scores:");
            e.printStackTrace();
        }
    }

    /**
     * Sort scores in descending order and keep only top MAX_SCORES
     */
    private void sortAndTrimScores() {
        // Sort in descending order (highest first)
        scores.sort(Collections.reverseOrder());

        // Keep only top MAX_SCORES
        if (scores.size() > MAX_SCORES) {
            scores = new ArrayList<>(scores.subList(0, MAX_SCORES));
        }
    }

    /**
     * Add a new score to the leaderboard
     * @param score the score to add
     */
    public void addScore(int score) {
        // Don't add negative or zero scores
        if (score <= 0) {
            System.out.println("Score " + score + " is too low to save");
            return;
        }

        System.out.println("Adding score: " + score);
        scores.add(score);
        sortAndTrimScores();
        saveScores();
    }

    /**
     * Check if a score would make it to the leaderboard
     * @param score the score to check
     * @return true if this score is high enough for the leaderboard
     */
    public boolean isHighScore(int score) {
        if (score <= 0) {
            return false;
        }

        if (scores.size() < MAX_SCORES) {
            return true;
        }

        // Check if this score is higher than the lowest score
        return score > scores.get(scores.size() - 1);
    }

    /**
     * Get the rank of a given score (1 = highest)
     * @param score the score to check
     * @return the rank (1-based), or -1 if not in top scores
     */
    public int getScoreRank(int score) {
        sortAndTrimScores(); // Ensure list is sorted

        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) == score) {
                return i + 1; // Return 1-based rank
            }
        }
        return -1; // Not in top scores
    }

    /**
     * Get the top high scores
     * @return a copy of the high scores list (sorted highest to lowest)
     */
    public List<Integer> getHighScores() {
        return new ArrayList<>(scores);
    }

    /**
     * Get the highest score
     * @return the highest score, or 0 if no scores exist
     */
    public int getHighestScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(0);
    }

    /**
     * Get the lowest score in the leaderboard
     * @return the lowest score, or 0 if no scores exist
     */
    public int getLowestScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(scores.size() - 1);
    }

    /**
     * Clear all scores (useful for testing)
     */
    public void clearScores() {
        scores.clear();
        saveScores();
        System.out.println("All scores cleared");
    }

    /**
     * Get the number of scores in the leaderboard
     * @return number of scores
     */
    public int getScoreCount() {
        return scores.size();
    }
}