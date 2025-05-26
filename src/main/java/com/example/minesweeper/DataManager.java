package com.example.minesweeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static final String DATA_FILE_PATH = "minesweeper_records.json";
    private static DataManager instance; // Singleton instance
    private Gson gson;
    private List<GameRecord> records;

    private DataManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        records = loadRecords();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private List<GameRecord> loadRecords() {
        try (FileReader reader = new FileReader(DATA_FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<GameRecord>>() {}.getType();
            List<GameRecord> loadedRecords = gson.fromJson(reader, listType);
            return loadedRecords != null ? loadedRecords : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Could not load records from " + DATA_FILE_PATH + ". Creating new file.");
            return new ArrayList<>();
        }
    }

    private void saveRecords() {
        try (FileWriter writer = new FileWriter(DATA_FILE_PATH)) {
            gson.toJson(records, writer);
        } catch (IOException e) {
            System.err.println("Could not save records to " + DATA_FILE_PATH + ": " + e.getMessage());
        }
    }

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

    public List<GameRecord> getBestRecords(String difficulty, int limit) {
        return records.stream()
                .filter(r -> r.getDifficulty().equalsIgnoreCase(difficulty))
                .sorted(Comparator.comparingInt(GameRecord::getTimeInSeconds))
                .limit(limit)
                .collect(Collectors.toList());
    }


    public GameRecord getPlayerBestRecord(String playerName, String difficulty) {
        return records.stream()
                .filter(r -> r.getPlayerName().equalsIgnoreCase(playerName) &&
                        r.getDifficulty().equalsIgnoreCase(difficulty))
                .min(Comparator.comparingInt(GameRecord::getTimeInSeconds))
                .orElse(null);
    }
}