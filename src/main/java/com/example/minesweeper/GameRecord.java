package com.example.minesweeper;


public class GameRecord {
    private String playerName;
    private String difficulty; // "Easy", "Medium", "Hard"
    private int timeInSeconds;

    public GameRecord(String playerName, String difficulty, int timeInSeconds) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.timeInSeconds = timeInSeconds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

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