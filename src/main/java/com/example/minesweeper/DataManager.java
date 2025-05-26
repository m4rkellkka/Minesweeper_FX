package com.example.minesweeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator; // Для сортировки
import java.util.List;
import java.util.stream.Collectors; // Для работы со стримами

/**
 * Manages loading and saving game records to a JSON file.
 * Implemented as a Singleton to ensure only one instance manages data.
 */
public class DataManager {
    private static final String DATA_FILE_PATH = "minesweeper_records.json";
    private static DataManager instance; // Singleton instance
    private Gson gson;
    private List<GameRecord> records;

    // Private constructor for Singleton pattern
    private DataManager() {
        gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty printing for readable JSON
        records = loadRecords();
    }

    /**
     * Returns the singleton instance of DataManager.
     * @return The DataManager instance.
     */
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    /**
     * Loads game records from the JSON file.
     * @return A list of GameRecord objects.
     */
    private List<GameRecord> loadRecords() {
        try (FileReader reader = new FileReader(DATA_FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<GameRecord>>() {}.getType();
            List<GameRecord> loadedRecords = gson.fromJson(reader, listType);
            return loadedRecords != null ? loadedRecords : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Could not load records from " + DATA_FILE_PATH + ". Creating new file.");
            return new ArrayList<>(); // Return empty list if file not found or error
        }
    }

    /**
     * Saves the current list of game records to the JSON file.
     */
    private void saveRecords() {
        try (FileWriter writer = new FileWriter(DATA_FILE_PATH)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            System.err.println("Could not save records to " + DATA_FILE_PATH + ": " + e.getMessage());
        }
    }

    /**
     * Adds a new game record. If a player already has a record for the same difficulty,
     * it updates if the new time is better. Otherwise, it adds the new record.
     * @param newRecord The new game record to add.
     */
    public void addRecord(GameRecord newRecord) {
        boolean updated = false;
        // Check if there's an existing record for this player and difficulty
        for (int i = 0; i < records.size(); i++) {
            GameRecord existingRecord = records.get(i);
            if (existingRecord.getPlayerName().equalsIgnoreCase(newRecord.getPlayerName()) &&
                    existingRecord.getDifficulty().equalsIgnoreCase(newRecord.getDifficulty())) {
                // If new time is better, update the existing record
                if (newRecord.getTimeInSeconds() < existingRecord.getTimeInSeconds()) {
                    records.set(i, newRecord); // Replace with the better record
                    System.out.println("Updated record for " + newRecord.getPlayerName() + " on " + newRecord.getDifficulty() + " with new time: " + newRecord.getTimeInSeconds());
                } else {
                    System.out.println("Existing record for " + newRecord.getPlayerName() + " on " + newRecord.getDifficulty() + " is already better or equal.");
                }
                updated = true;
                break;
            }
        }
        if (!updated) {
            records.add(newRecord); // Add as a new record if no existing one found
            System.out.println("Added new record for " + newRecord.getPlayerName() + " on " + newRecord.getDifficulty() + ": " + newRecord.getTimeInSeconds());
        }
        saveRecords(); // Save changes immediately
    }

    /**
     * Gets the best records for a given difficulty, sorted by time (lowest first).
     * @param difficulty The difficulty level ("Easy", "Medium", "Hard").
     * @param limit The maximum number of records to return.
     * @return A sorted list of best GameRecord objects for the specified difficulty.
     */
    public List<GameRecord> getBestRecords(String difficulty, int limit) {
        return records.stream()
                .filter(r -> r.getDifficulty().equalsIgnoreCase(difficulty))
                .sorted(Comparator.comparingInt(GameRecord::getTimeInSeconds))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Gets the current best record for a specific player and difficulty.
     * @param playerName The name of the player.
     * @param difficulty The difficulty level.
     * @return The best GameRecord for the player and difficulty, or null if not found.
     */
    public GameRecord getPlayerBestRecord(String playerName, String difficulty) {
        return records.stream()
                .filter(r -> r.getPlayerName().equalsIgnoreCase(playerName) &&
                        r.getDifficulty().equalsIgnoreCase(difficulty))
                .min(Comparator.comparingInt(GameRecord::getTimeInSeconds))
                .orElse(null);
    }
}