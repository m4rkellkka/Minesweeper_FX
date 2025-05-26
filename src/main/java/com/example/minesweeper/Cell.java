package com.example.minesweeper;

import javafx.scene.control.Button;

/**
 * The `Cell` class represents a single cell on the Minesweeper game board.
 * It encapsulates the logical state of the cell (mine, number, opened, flagged, questioned)
 * and holds a reference to its corresponding JavaFX Button for UI representation.
 */
public class Cell {

    // --- Logical State of the Cell ---
    private final int row;         // The row index of the cell
    private final int col;         // The column index of the cell
    private boolean isMine;        // True if the cell contains a mine
    private int minesAround;       // Number of mines in adjacent cells
    private boolean isOpen;        // True if the cell has been opened by the player
    private boolean isFlagged;     // True if the cell is marked with a flag
    private boolean isQuestioned;  // True if the cell is marked with a question mark

    // --- UI Element Associated with this Cell ---
    private final Button button;   // The JavaFX Button representing this cell on the screen

    /**
     * Constructs a new Cell object with specified row and column coordinates.
     * Initializes the cell's logical state to default values (no mine, closed, no flags/questions).
     * A new JavaFX Button is also created for this cell.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.minesAround = 0;
        this.isOpen = false;
        this.isFlagged = false;
        this.isQuestioned = false;
        this.button = new Button(); // Create the UI button for this cell.
        // Initial text and styling of the button will be handled by the updateUI method in App.
    }

    // --- Getters and Setters for Cell Properties ---
    // These methods allow other classes to read and modify the cell's state.

    /**
     * Gets the row index of the cell.
     * @return The row index.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of the cell.
     * @return The column index.
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks if the cell contains a mine.
     * @return True if it's a mine, false otherwise.
     */
    public boolean isMine() {
        return isMine;
    }

    /**
     * Sets whether the cell contains a mine.
     * @param mine True to set as a mine, false otherwise.
     */
    public void setMine(boolean mine) {
        isMine = mine;
    }

    /**
     * Gets the number of mines in adjacent cells.
     * @return The count of adjacent mines.
     */
    public int getMinesAround() {
        return minesAround;
    }

    /**
     * Sets the number of mines in adjacent cells.
     * @param minesAround The count of adjacent mines.
     */
    public void setMinesAround(int minesAround) {
        this.minesAround = minesAround;
    }

    /**
     * Checks if the cell is open.
     * @return True if the cell is open, false otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Sets whether the cell is open.
     * @param open True to open the cell, false to close it.
     */
    public void setOpen(boolean open) {
        isOpen = open;
    }

    /**
     * Checks if the cell is flagged.
     * @return True if the cell is flagged, false otherwise.
     */
    public boolean isFlagged() {
        return isFlagged;
    }

    /**
     * Sets whether the cell is flagged.
     * If the cell is flagged, any question mark is removed.
     * @param flagged True to flag the cell, false to unflag it.
     */
    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
        // If a cell is flagged, it cannot also be questioned.
        if (flagged) {
            isQuestioned = false;
        }
    }

    /**
     * Checks if the cell is marked with a question mark.
     * @return True if the cell has a question mark, false otherwise.
     */
    public boolean isQuestioned() {
        return isQuestioned;
    }

    /**
     * Sets whether the cell is marked with a question mark.
     * If the cell is questioned, any flag is removed.
     * @param questioned True to mark with a question mark, false to remove it.
     */
    public void setQuestioned(boolean questioned) {
        isQuestioned = questioned;
        // If a cell is questioned, it cannot also be flagged.
        if (questioned) {
            isFlagged = false;
        }
    }

    /**
     * Gets the JavaFX Button associated with this cell.
     * This button is the visual representation of the cell in the UI.
     * @return The JavaFX Button object.
     */
    public Button getButton() {
        return button;
    }

    /**
     * Resets the cell's logical state to its initial, unopened, unflagged, and unmined state.
     * Also resets the text and style of the associated JavaFX Button.
     */
    public void reset() {
        isMine = false;
        minesAround = 0;
        isOpen = false;
        isFlagged = false;
        isQuestioned = false;
        button.setText("");   // Clear any text on the button
        button.setStyle("");  // Clear any inline styles, reverting to CSS defaults
    }

    /**
     * Provides a string representation of the cell's current logical state.
     * This is useful for debugging purposes to quickly see the board state in console.
     * @return A string representing the cell's state (e.g., "M" for mine, "F" for flag, "1" for number).
     */
    @Override
    public String toString() {
        if (isMine) return "M";
        if (isFlagged) return "F";
        if (isQuestioned) return "?";
        if (isOpen) {
            if (minesAround > 0) return String.valueOf(minesAround);
            return " "; // Opened empty cell
        }
        return "X"; // Closed cell
    }
}