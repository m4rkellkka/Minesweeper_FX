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

    private Cell[][] grid;      // Reference to the game board represented by Cell objects
    private int rows;           // Number of rows in the grid
    private int cols;           // Number of columns in the grid
    private int totalMines;     // Total number of mines on the board for the current game
    private int cellsOpen;      // Count of non-mine cells that have been opened by the player
    private boolean gameOver;   // Flag indicating if the game has ended (win or loss)
    private boolean gameWon;    // Flag indicating if the game was won
    private boolean firstClick; // Flag to determine if it's the very first click of the game

    // DEBUG: This list helps track placed mines if you need to debug placement.
    // Private field for tracking placed mines (used in commented-out debug code)
    private List<Cell> mines = new ArrayList<>();

    private final Random random = new Random(); // Random number generator for mine placement

    /**
     * Constructor for initializing a new Minesweeper game.
     * Sets up the game dimensions and mine count, then resets the game state.
     *
     * @param grid The 2D array of Cell objects representing the game board.
     * @param rows The number of rows in the board.
     * @param cols The number of columns in the board.
     * @param totalMines The total number of mines for this game instance.
     */
    public MinesweeperGame(Cell[][] grid, int rows, int cols, int totalMines) {
        this.grid = grid;
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        resetGame(); // Initialize game state to default
    }

    /**
     * Resets the entire game state to prepare for a new game.
     * Clears cell states, resets counters, and sets flags.
     */
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

    /**
     * Handles a left-click action on a specific cell.
     * Manages first click logic (mine placement), opening cells, and game over conditions.
     *
     * @param cell The Cell object that was clicked.
     */
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
            calculateMinesAround(); // Calculate numbers for all non-mine cells
            firstClick = false; // Toggle first click flag
            openCell(cell.getRow(), cell.getCol()); // Open the first clicked cell
            checkGameEnd(); // Check if this first action led to a win
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

        // Open the cell and check for game end conditions.
        openCell(cell.getRow(), cell.getCol());
        checkGameEnd();
    }

    /**
     * Attempts to perform a "chord" action on an opened, numbered cell.
     * If the number of flags around the cell matches the cell's `minesAround` count,
     * all unflagged, unopened neighbors are automatically opened.
     *
     * @param r The row index of the opened cell.
     * @param c The column index of the opened cell.
     */
    private void tryChord(int r, int c) {
        Cell clickedCell = grid[r][c];
        // Chord action is only valid on an already open cell with a number.
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
                    if (dr == 0 && dc == 0) continue; // Skip the cell itself

                    int neighborRow = r + dr;
                    int neighborCol = c + dc;

                    if (neighborRow >= 0 && neighborRow < rows &&
                            neighborCol >= 0 && neighborCol < cols) {
                        Cell neighborCell = grid[neighborRow][neighborCol];
                        // Only open cells that are currently closed and not flagged.
                        if (!neighborCell.isOpen() && !neighborCell.isFlagged()) {
                            // If opening a neighbor reveals a mine, the game ends.
                            if (neighborCell.isMine()) {
                                neighborCell.setOpen(true); // Open this exploded mine
                                gameOver = true;
                                revealAllMines(); // Show all mines on loss
                                System.out.println("You lost by chording into a mine! Game Over!"); // DEBUG: Chord loss message
                                return; // Exit immediately after a loss
                            } else {
                                openCell(neighborRow, neighborCol); // Recursively open the neighbor
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles a right-click action on a specific cell.
     * Cycles through flag, question mark, and default (closed) states.
     *
     * @param cell The Cell object that was clicked.
     */
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
        // UI will be updated by App.java after this method returns.
    }

    /**
     * Randomly places mines on the board, ensuring no mine is placed
     * on the first clicked cell.
     *
     * @param firstClickRow The row index of the first clicked cell.
     * @param firstClickCol The column index of the first clicked cell.
     */
    private void placeMines(int firstClickRow, int firstClickCol) {
        // IMPORTANT: The commented-out block below is a more advanced mine placement
        // that tries to ensure a minimum distance between mines. The simpler
        // current implementation is also valid for basic Minesweeper.
        // It's kept for reference.

        // DEBUG: Clear mines list before new placement if used for debugging.
        mines.clear();

        int minesPlaced = 0;
        while (minesPlaced < totalMines) {
            int r = random.nextInt(rows); // Random row
            int c = random.nextInt(cols); // Random column

            // Ensure mine is not placed on the first clicked cell
            // and not on a cell that already has a mine.
            if (!grid[r][c].isMine() && !(r == firstClickRow && c == firstClickCol)) {
                grid[r][c].setMine(true);
                mines.add(grid[r][c]); // DEBUG: Add to list for potential distance checks or logging
                minesPlaced++;
            }
        }
    }

    /*
    // Advanced mine placement with minimum distance (currently commented out)
    private void placeMines(int firstClickRow, int firstClickCol) {
        mines.clear(); // Clear the list of mines before new placement

        int minesPlaced = 0;
        // Define minimum distance. Value 1.5 is a good start, but can be adjusted.
        // If mines are too clustered, increase it. If it's hard to place all mines, decrease it.
        double minDistance = 2.0; // Minimum Euclidean distance between mines (adjustable)

        // Consider how minDistance relates to board size and mine count.
        // E.g., for 8x8 with 10 mines, 1.5-2.0 might be reasonable.
        // For 16x16 with 40 mines, you might want to increase minDistance.

        while (minesPlaced < totalMines) {
            int r = random.nextInt(rows); // Random row
            int c = random.nextInt(cols); // Random column

            // 1. Ensure mine is not placed on the first clicked cell.
            if (r == firstClickRow && c == firstClickCol) {
                continue; // Skip this iteration if it's the initial clicked cell
            }

            // 2. Ensure mine is not placed on a cell that already has a mine.
            if (grid[r][c].isMine()) {
                continue; // Skip this iteration if a mine already exists
            }

            // 3. Check minimum distance to already placed mines.
            boolean isFarEnough = true;
            for (Cell existingMine : mines) {
                // Calculate Euclidean distance between candidate and existing mine.
                // Math.hypot(x2-x1, y2-y1) is a more readable way.
                double distance = Math.hypot(r - existingMine.getRow(), c - existingMine.getCol());
                if (distance < minDistance) {
                    isFarEnough = false;
                    break; // Not far enough, break from the distance check loop
                }
            }

            if (isFarEnough) {
                grid[r][c].setMine(true); // Place the mine
                mines.add(grid[r][c]);    // Add to the list of placed mines
                minesPlaced++;            // Increment mine count
            }
            // If isFarEnough == false, we simply skip this iteration and will
            // generate new random r, c in the next loop.
        }
    }
    */

    /**
     * Calculates the number of adjacent mines for every non-mine cell on the board.
     * Sets the `minesAround` property for each cell.
     */
    private void calculateMinesAround() {
        System.out.println("Calculating mines around..."); // DEBUG: Start calculation
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isMine()) {
                    // Mines do not have 'minesAround' values relevant for display,
                    // so we explicitly set it to 0 or could skip.
                    grid[r][c].setMinesAround(0);
                    continue; // Skip calculation for mine cells
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
                // DEBUG: Print count for cells with numbers.
                if (count > 0) {
                    System.out.println("Cell [" + r + "," + c + "] has " + count + " mines around.");
                }
            }
        }
        System.out.println("Mine calculation complete."); // DEBUG: End calculation
    }

    /**
     * Recursively opens a cell. If the opened cell has 0 adjacent mines,
     * it recursively opens all its adjacent cells (flood fill).
     *
     * @param r The row index of the cell to open.
     * @param c The column index of the cell to open.
     */
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

    /**
     * Reveals all mine cells on the board.
     * Typically called when the game is lost to show where all mines were.
     */
    private void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isMine()) {
                    grid[r][c].setOpen(true); // Mark mine cells as open for UI display
                }
                // Note: This method only opens mine cells. It does not
                // change the 'isFlagged' state of correctly flagged mines.
                // The UI is responsible for displaying these.
            }
        }
    }

    /**
     * Checks the game end condition: either win or lose.
     * Sets `gameWon` and `gameOver` flags accordingly.
     */
    private void checkGameEnd() {
        // Win condition: The number of opened non-mine cells equals
        // the total number of cells minus the total number of mines.
        if (cellsOpen == (rows * cols) - totalMines) {
            gameWon = true;
            gameOver = true;
            System.out.println("You won!"); // DEBUG: Win message
            // Further actions like revealing all mines (if not already opened)
            // or showing a win message will be handled by the UI (App.java).
        }
        // Loss condition is handled directly in handleLeftClick when a mine is hit.
    }

    // --- Getters for Game State ---

    /**
     * Checks if the game has ended (either won or lost).
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Checks if the game has been won.
     * @return True if the game is won, false otherwise.
     */
    public boolean isGameWon() {
        return gameWon;
    }
}