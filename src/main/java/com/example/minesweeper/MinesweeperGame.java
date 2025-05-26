package com.example.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The `MinesweeperGame` class manages the core logic of the Minesweeper game.
 * This includes mine placement, counting adjacent mines, handling cell openings,
 * and checking for win/loss conditions.
 */
public class MinesweeperGame {

    private Cell[][] grid;
    private int rows;
    private int cols;
    private int totalMines;
    private int cellsOpen;
    private boolean gameOver;
    private boolean gameWon;
    private boolean firstClick;

    private List<Cell> mines = new ArrayList<>();

    private final Random random = new Random(); // Random number generator for mine placement

    public MinesweeperGame(Cell[][] grid, int rows, int cols, int totalMines) {
        this.grid = grid;
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        resetGame(); // Initialize game state to default
    }
    public void resetGame() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].reset(); // Reset each individual cell's state
            }
        }
        cellsOpen = 0;
        gameOver = false;
        gameWon = false;
        firstClick = true; // Set to true to trigger deferred mine placement on first interaction
        mines.clear(); // Clear the list of placed mines (if used for debugging placement)
        // Mines are not placed here; they are placed on the first click.
    }

    public void handleLeftClick(Cell cell) {
        if (gameOver) {
            return; // Ignore clicks if the game has already ended
        }

        if (cell.isOpen()) {
            // If the cell is already open and has a number, attempt a "chord" action.
            if (cell.getMinesAround() > 0) {
                tryChord(cell.getRow(), cell.getCol());
            }
            return; // Ignore normal left-click on an already open cell
        }

        if (cell.isFlagged()) {
            return; // Ignore left-click if the cell is flagged (flags prevent opening)
        }

        // IMPORTANT: Mine placement is deferred until the first actual click.
        // This ensures the first clicked cell is never a mine.
        if (firstClick) {
            placeMines(cell.getRow(), cell.getCol()); // Place mines, avoiding the first clicked cell
            calculateMinesAround();
            firstClick = false; // Toggle first click flag
            openCell(cell.getRow(), cell.getCol());
            checkGameEnd();
            return; // Exit to prevent re-opening or immediate loss
        }

        // If it's a mine, the game is over.
        if (cell.isMine()) {
            cell.setOpen(true); // Mark the exploded mine as open
            gameOver = true;
            revealAllMines(); // Show all mines on the board
            System.out.println("You lost!"); // DEBUG: Loss message
            return;
        }

        openCell(cell.getRow(), cell.getCol());
        checkGameEnd();
    }

    private void tryChord(int r, int c) {
        Cell clickedCell = grid[r][c];
        if (!clickedCell.isOpen() || clickedCell.getMinesAround() == 0) {
            return;
        }

        int flagCount = 0;
        // First, count the number of flags around the clicked cell.
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // Skip the cell itself

                int neighborRow = r + dr;
                int neighborCol = c + dc;

                // Check bounds and if the neighbor is flagged.
                if (neighborRow >= 0 && neighborRow < rows &&
                        neighborCol >= 0 && neighborCol < cols) {
                    if (grid[neighborRow][neighborCol].isFlagged()) {
                        flagCount++;
                    }
                }
            }
        }

        // If the flag count matches the cell's number, open adjacent cells.
        if (flagCount == clickedCell.getMinesAround()) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;

                    int neighborRow = r + dr;
                    int neighborCol = c + dc;

                    if (neighborRow >= 0 && neighborRow < rows &&
                            neighborCol >= 0 && neighborCol < cols) {
                        Cell neighborCell = grid[neighborRow][neighborCol];
                        if (!neighborCell.isOpen() && !neighborCell.isFlagged()) {
                            if (neighborCell.isMine()) {
                                neighborCell.setOpen(true); // Open this exploded mine
                                gameOver = true;
                                revealAllMines(); // Show all mines on loss
                                System.out.println("You lost by chording into a mine! Game Over!");
                                return;
                            } else {
                                openCell(neighborRow, neighborCol);
                            }
                        }
                    }
                }
            }
        }
    }

    public void handleRightClick(Cell cell) {
        // Ignore right-clicks if game is over or cell is already open.
        if (gameOver || cell.isOpen()) {
            return;
        }

        if (cell.isFlagged()) {
            cell.setFlagged(false);
            cell.setQuestioned(true); // Flag -> Question mark
        } else if (cell.isQuestioned()) {
            cell.setQuestioned(false); // Question mark -> Closed
        } else {
            cell.setFlagged(true); // Closed -> Flag
        }
    }

    private void placeMines(int firstClickRow, int firstClickCol) {

        //Clear mines list before new placement if used for debugging.
        mines.clear();

        int minesPlaced = 0;
        while (minesPlaced < totalMines) {
            int r = random.nextInt(rows); // Random row
            int c = random.nextInt(cols); // Random column

            // Ensure mine is not placed on the first clicked cell
            // and not on a cell that already has a mine.
            if (!grid[r][c].isMine() && !(r == firstClickRow && c == firstClickCol)) {
                grid[r][c].setMine(true);
                mines.add(grid[r][c]); //Add to list for potential distance checks or logging
                minesPlaced++;
            }
        }
    }

    private void calculateMinesAround() {
        System.out.println("Calculating mines around...");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isMine()) {
                    grid[r][c].setMinesAround(0);
                    continue;
                }

                int count = 0;
                // Iterate through all 8 neighboring cells.
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        // Skip the cell itself.
                        if (dr == 0 && dc == 0) continue;

                        int neighborRow = r + dr;
                        int neighborCol = c + dc;

                        // Check if neighbor coordinates are within board bounds.
                        if (neighborRow >= 0 && neighborRow < rows &&
                                neighborCol >= 0 && neighborCol < cols) {
                            if (grid[neighborRow][neighborCol].isMine()) {
                                count++; // Increment count if neighbor is a mine
                            }
                        }
                    }
                }
                grid[r][c].setMinesAround(count); // Set the calculated count
                //Print count for cells with numbers.
                if (count > 0) {
                    System.out.println("Cell [" + r + "," + c + "] has " + count + " mines around.");
                }
            }
        }
        System.out.println("Mine calculation complete."); //End calculation
    }

    private void openCell(int r, int c) {
        // IMPORTANT: Boundary and state checks are crucial for recursion.
        // Stop if outside bounds, already open, flagged, or is a mine.
        if (r < 0 || r >= rows || c < 0 || c >= cols ||
                grid[r][c].isOpen() || grid[r][c].isFlagged() || grid[r][c].isMine()) {
            return;
        }

        grid[r][c].setOpen(true);
        cellsOpen++; // Increment the count of successfully opened non-mine cells

        // If the opened cell has 0 adjacent mines (it's a "blank" cell),
        // recursively open all its 8 neighbors.
        if (grid[r][c].getMinesAround() == 0) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    // Skip the current cell itself.
                    if (dr == 0 && dc == 0) {
                        continue;
                    }
                    openCell(r + dr, c + dc); // Recursive call for neighbors
                }
            }
        }
    }
    private void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isMine()) {
                    grid[r][c].setOpen(true); // Mark mine cells as open for UI display
                }
            }
        }
    }
    private void checkGameEnd() {
        // Win condition: The number of opened non-mine cells equals
        // the total number of cells minus the total number of mines.
        if (cellsOpen == (rows * cols) - totalMines) {
            gameWon = true;
            gameOver = true;
            System.out.println("You won!");
        }
    }

    // --- Getters for Game State ---

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }
}