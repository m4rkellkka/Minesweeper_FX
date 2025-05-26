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

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.minesAround = 0;
        this.isOpen = false;
        this.isFlagged = false;
        this.isQuestioned = false;
        this.button = new Button();
        // Initial text and styling of the button will be handled by the updateUI method in App.
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public int getMinesAround() {
        return minesAround;
    }

    public void setMinesAround(int minesAround) {
        this.minesAround = minesAround;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
        // If a cell is flagged, it cannot also be questioned.
        if (flagged) {
            isQuestioned = false;
        }
    }

    public boolean isQuestioned() {
        return isQuestioned;
    }

    public void setQuestioned(boolean questioned) {
        isQuestioned = questioned;
        // If a cell is questioned, it cannot also be flagged.
        if (questioned) {
            isFlagged = false;
        }
    }

    public Button getButton() { // Публичный метод для получения связанной JavaFX Button.
        return button; // Возвращает ссылку на объект button.
    }

    public void reset() {
        isMine = false;
        minesAround = 0;
        isOpen = false;
        isFlagged = false;
        isQuestioned = false;
        button.setText("");   // Clear any text on the button
        button.setStyle("");  // Clear any inline styles, reverting to CSS defaults
    }
}
