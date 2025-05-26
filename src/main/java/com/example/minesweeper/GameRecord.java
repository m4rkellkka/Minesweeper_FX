package com.example.minesweeper;

/**
 * Represents a single game record for the leaderboard.
 * Stores player name, difficulty, and time taken.
 */
public class GameRecord {
    private String playerName;
    private String difficulty; // "Easy", "Medium", "Hard"
    private int timeInSeconds;

    // Default constructor for Gson
    public GameRecord() {
    }

    public GameRecord(String playerName, String difficulty, int timeInSeconds) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.timeInSeconds = timeInSeconds;
    }

    // --- Getters ---
    public String getPlayerName() {
        return playerName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    // --- Setters (optional, but good for Gson if direct deserialization is needed) ---
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "playerName='" + playerName + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", timeInSeconds=" + timeInSeconds +
                '}';
    }
}