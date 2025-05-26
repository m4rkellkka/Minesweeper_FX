package com.example.minesweeper;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

/**
 * The main application class for the Minesweeper game.
 * Manages UI transitions between start screen, game screen, and leaderboard,
 * as well as game state, timer, and score management.
 */
public class App extends Application {

    // --- Constants for Game Difficulty and UI ---
    private static final int GRID_SIZE_EASY = 10;
    private static final int MINES_EASY = 10;
    private static final String DIFFICULTY_EASY = "Easy";

    private static final int GRID_SIZE_MEDIUM = 12;
    private static final int MINES_MEDIUM = 20;
    private static final String DIFFICULTY_MEDIUM = "Medium";

    private static final int GRID_SIZE_HARD = 14;
    private static final int MINES_HARD = 25;
    private static final String DIFFICULTY_HARD = "Hard";

    private static final int CELL_SIZE = 40; // Pixel size for each game cell button
    private static final String DEFAULT_PLAYER_NAME = "Mikhail Savushkin"; // Default name if none entered
    private static final String FONT_NAME_BENZIN_BOLD = "Benzin-Bold";

    // --- Game State Variables ---
    private Cell[][] grid; // Represents the game board cells
    private int currentGridSize;
    private int currentMines;
    private String currentDifficultyName;
    private String currentPlayerName = DEFAULT_PLAYER_NAME;

    private MinesweeperGame gameLogic; // Core game logic (mine placement, opening cells)
    private GridPane gameGridPane;

    // --- UI Elements for Game Screen ---
    private Label minesCounterLabel;
    private Label timerLabel;
    private Button newGameButton;

    // --- Timer Variables ---
    private AnimationTimer gameTimer;
    private long startTimeNano;
    private int secondsElapsed;

    // --- Stage Management ---
    private Stage primaryStage; // The main application window

    // --- Start Screen Difficulty Selection ---
    private int selectedDifficultyGridSize = GRID_SIZE_EASY;
    private int selectedDifficultyMines = MINES_EASY;
    private String selectedDifficultyName = DIFFICULTY_EASY; // Name of the initially selected difficulty

    // --- Data Management ---
    private DataManager dataManager;

    // --- FXML Injected UI Elements for StartScreen.fxml ---
    @FXML private TextField nameField;
    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;
    @FXML private Button startGameButton;
    @FXML private Button leaderboardButton;
    @FXML private Button exitButton;

    /**
     * The entry point of the JavaFX application.
     * Initializes the primary stage and shows the start screen.
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Minesweeper");

        dataManager = DataManager.getInstance(); // Initialize data manager for records

        showStartScreen();
    }

    private void showStartScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StartScreen.fxml"));
            loader.setController(this);

            Parent root = loader.load();

            Scene startScene = new Scene(root);
            // Ensure CSS is loaded for the start screen.
            startScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            primaryStage.setScene(startScene);
            primaryStage.setTitle("Minesweeper");
            primaryStage.setResizable(false); // Start screen is not resizable
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading StartScreen.fxml: " + e.getMessage());
        }
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by FXMLLoader. It's an ideal place for initial UI setup.
     */
    @FXML
    public void initialize() {
        // Set initial player name in the text field.
        if (currentPlayerName != null && !currentPlayerName.isEmpty()) {
            nameField.setText(currentPlayerName);
        } else {
            nameField.setText(DEFAULT_PLAYER_NAME);
        }

        // Apply CSS classes to buttons for styling.
        // Important: Ensure these buttons are correctly injected by FXML.
        if (easyButton != null) easyButton.getStyleClass().add("easy-button");
        if (mediumButton != null) mediumButton.getStyleClass().add("medium-button");
        if (hardButton != null) hardButton.getStyleClass().add("hard-button");
        if (startGameButton != null) startGameButton.getStyleClass().add("new_game_button");
        if (leaderboardButton != null) leaderboardButton.getStyleClass().add("leaderboardButton");
        if (exitButton != null) exitButton.getStyleClass().add("ExitButton");

        // Highlight the initially selected difficulty button.
        highlightSelectedDifficultyButton(getButtonForDifficulty(selectedDifficultyName));
    }

    @FXML
    private void handleEasyClick() {
        selectedDifficultyGridSize = GRID_SIZE_EASY;
        selectedDifficultyMines = MINES_EASY;
        selectedDifficultyName = DIFFICULTY_EASY;
        highlightSelectedDifficultyButton(easyButton);
        System.out.println("Selected difficulty: Easy"); // DEBUG: Console output for selection
    }

    @FXML
    private void handleMediumClick() {
        selectedDifficultyGridSize = GRID_SIZE_MEDIUM;
        selectedDifficultyMines = MINES_MEDIUM;
        selectedDifficultyName = DIFFICULTY_MEDIUM;
        highlightSelectedDifficultyButton(mediumButton);
        System.out.println("Selected difficulty: Medium"); // DEBUG: Console output for selection
    }

    @FXML
    private void handleHardClick() {
        selectedDifficultyGridSize = GRID_SIZE_HARD;
        selectedDifficultyMines = MINES_HARD;
        selectedDifficultyName = DIFFICULTY_HARD;
        highlightSelectedDifficultyButton(hardButton);
        System.out.println("Selected difficulty: Hard"); // DEBUG: Console output for selection
    }

    @FXML
    private void handleStartGame() {
        String enteredName = nameField.getText().trim();
        currentPlayerName = enteredName.isEmpty() ? DEFAULT_PLAYER_NAME : enteredName;

        System.out.println("Starting game with player: " + currentPlayerName + " and difficulty: " + selectedDifficultyName); // DEBUG: Game start info
        showGameScreen();
    }

    @FXML
    private void showLeaderboardScreen() {
        System.out.println("Showing leaderboard screen."); // DEBUG: Leaderboard navigation

        BorderPane leaderboardRoot = new BorderPane();
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f0f0f0;"); // Inline style for background

        Label titleLabel = new Label("LEADERBOARD");
        titleLabel.setFont(new Font(FONT_NAME_BENZIN_BOLD, 36));
        titleLabel.setTextFill(Color.BLUE);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Tabs cannot be closed

        // Create tabs for each difficulty level.
        Tab easyTab = new Tab(DIFFICULTY_EASY);
        easyTab.setContent(createLeaderboardTable(DIFFICULTY_EASY));
        tabPane.getTabs().add(easyTab);

        Tab mediumTab = new Tab(DIFFICULTY_MEDIUM);
        mediumTab.setContent(createLeaderboardTable(DIFFICULTY_MEDIUM));
        tabPane.getTabs().add(mediumTab);

        Tab hardTab = new Tab(DIFFICULTY_HARD);
        hardTab.setContent(createLeaderboardTable(DIFFICULTY_HARD));
        tabPane.getTabs().add(hardTab);

        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().add("main-menu");
        backButton.setOnAction(e -> showStartScreen()); // Navigate back to start screen

        mainLayout.getChildren().addAll(titleLabel, tabPane, backButton);
        leaderboardRoot.setCenter(mainLayout);

        Scene leaderboardScene = new Scene(leaderboardRoot, 600, 500);
        leaderboardScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(leaderboardScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private TableView<GameRecord> createLeaderboardTable(String difficulty) {
        TableView<GameRecord> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Columns adjust to fit

        TableColumn<GameRecord, String> nameCol = new TableColumn<>("Player Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        nameCol.setPrefWidth(200); // Set preferred width for column

        TableColumn<GameRecord, Integer> timeCol = new TableColumn<>("Time (s)");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeInSeconds"));
        timeCol.setPrefWidth(100); // Set preferred width for column
        timeCol.setStyle("-fx-alignment: CENTER-RIGHT;"); // Align time to the right

        tableView.getColumns().addAll(nameCol, timeCol);

        // Retrieve and add best records (top 10) for the specified difficulty.
        List<GameRecord> records = dataManager.getBestRecords(difficulty, 10);
        tableView.getItems().addAll(records);

        tableView.setPlaceholder(new Label("No records yet for " + difficulty + " difficulty."));

        return tableView;
    }

    @FXML
    private void handleExitButton() {
        System.out.println("Exiting application."); // DEBUG: Application exit
        primaryStage.close();
    }

    private Button getButtonForDifficulty(String difficultyName) {
        switch (difficultyName) {
            case DIFFICULTY_EASY: return easyButton;
            case DIFFICULTY_MEDIUM: return mediumButton;
            case DIFFICULTY_HARD: return hardButton;
            default: return null; // Should not happen with valid difficulty names.
        }
    }

    private void showGameScreen() {
        BorderPane gameRoot = new BorderPane();

        HBox topPanel = new HBox(10);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(10));

        minesCounterLabel = new Label("Mines: " + selectedDifficultyMines);
        minesCounterLabel.setFont(new Font(FONT_NAME_BENZIN_BOLD, 13));
        minesCounterLabel.getStyleClass().add("minesCounterLabel"); // Add CSS class

        timerLabel = new Label("Time: 0");
        timerLabel.setFont(new Font(FONT_NAME_BENZIN_BOLD, 12));
        timerLabel.setMinWidth(70); // Ensure enough space for timer text
        timerLabel.getStyleClass().add("timer-label"); // Add CSS class

        Button backToMenuButton = new Button("Back");
        backToMenuButton.getStyleClass().add("main-menu"); // Apply common CSS style
        backToMenuButton.setOnAction(e -> showStartScreen()); // Navigate back to start screen

        newGameButton = new Button("😊"); // Emoji for new game button
        newGameButton.setFont(new Font("Segoe UI Emoji", 30)); // Font for emoji
        newGameButton.getStyleClass().add("start-again"); // Add CSS class
        newGameButton.setOnAction(e -> startGame(selectedDifficultyGridSize, selectedDifficultyMines, selectedDifficultyName));

        topPanel.getChildren().addAll(minesCounterLabel, timerLabel, newGameButton, backToMenuButton);
        gameRoot.setTop(topPanel);

        gameGridPane = new GridPane();
        gameGridPane.setAlignment(Pos.CENTER);
        gameRoot.setCenter(gameGridPane);

        startGame(selectedDifficultyGridSize, selectedDifficultyMines, selectedDifficultyName);

        Scene gameScene = new Scene(gameRoot);
        // Ensure CSS is loaded for the game scene.
        gameScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(true); // Game screen can be resizable
        primaryStage.sizeToScene(); // Adjust stage size to fit the scene content
    }

    /**
     * Initializes and starts a new Minesweeper game.
     * Sets up the game board, initializes game logic, and starts the timer.
     * @param size The grid size (rows and columns).
     * @param mines The number of mines.
     * @param difficultyName The name of the selected difficulty.
     */
    private void startGame(int size, int mines, String difficultyName) {
        this.currentGridSize = size;
        this.currentMines = mines;
        this.currentDifficultyName = difficultyName;

        gameGridPane.getChildren().clear(); // Clear any existing cells from the grid
        grid = new Cell[currentGridSize][currentGridSize];

        // Populate the game grid with Cell objects and their corresponding Buttons
        for (int row = 0; row < currentGridSize; row++) {
            for (int col = 0; col < currentGridSize; col++) {
                Cell cell = new Cell(row, col);
                grid[row][col] = cell;

                Button cellButton = cell.getButton();
                cellButton.setPrefSize(CELL_SIZE, CELL_SIZE);

                // Add mouse click event handler for each cell button.
                // This is the primary interaction point for the player.
                cellButton.setOnMouseClicked(event -> {
                    if (gameLogic.isGameOver()) {
                        return; // Prevent interaction if game is already over
                    }

                    if (event.getButton() == MouseButton.PRIMARY) {
                        gameLogic.handleLeftClick(cell);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        gameLogic.handleRightClick(cell);
                    }
                    updateUI(); // Refresh the UI after each click
                });

                gameGridPane.add(cellButton, col, row); // Add button to the GridPane
            }
        }
        gameLogic = new MinesweeperGame(grid, currentGridSize, currentGridSize, currentMines);
        updateUI(); // Initial UI update to show initial state
        startTimer(); // Start the game timer
        primaryStage.sizeToScene(); // Adjust window size after board is created
    }

    /**
     * Displays a dialog box at the end of the game (win or lose).
     * Shows game outcome and provides options to play again or return to main menu.
     * @param won True if the player won, false otherwise.
     */
    private void showResultDialog(boolean won) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with main window
        dialogStage.setTitle(won ? "Congratulations!" : "Game Over!");
        dialogStage.setResizable(false);

        VBox dialogLayout = new VBox(20);
        dialogLayout.setAlignment(Pos.CENTER);
        dialogLayout.setPadding(new Insets(30));
        dialogLayout.setStyle("-fx-background-color: lightgray;"); // Inline style for dialog background

        Label messageLabel = new Label(won ? "You won in " + secondsElapsed + " seconds!" : "You hit a mine! Game Over.");
        messageLabel.setFont(new Font(FONT_NAME_BENZIN_BOLD, 24));
        messageLabel.setTextFill(won ? Color.DARKGREEN : Color.RED);

        // Display personal best information if won.
        if (won) {
            GameRecord personalBest = dataManager.getPlayerBestRecord(currentPlayerName, currentDifficultyName);
            if (personalBest != null && secondsElapsed < personalBest.getTimeInSeconds()) {
                Label newBestLabel = new Label("New personal best!");
                newBestLabel.setFont(new Font(FONT_NAME_BENZIN_BOLD, 18));
                newBestLabel.setTextFill(Color.BLUE);
                dialogLayout.getChildren().add(newBestLabel);
            } else if (personalBest != null) {
                Label currentBestLabel = new Label("Your best for " + currentDifficultyName + " is: " + personalBest.getTimeInSeconds() + " seconds");
                currentBestLabel.setFont(new Font(FONT_NAME_BENZIN_BOLD, 16));
                currentBestLabel.getStyleClass().add("dialog-current-best-label"); // Add CSS class
                dialogLayout.getChildren().add(currentBestLabel);
            }
        }

        HBox buttonsContainer = new HBox(15);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.getStyleClass().add("dialog-buttons-container"); // Add CSS class

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setFont(new Font("Arial", 16));
        playAgainButton.getStyleClass().add("start-again"); // Apply common CSS style
        playAgainButton.setOnAction(e -> {
            dialogStage.close();
            startGame(selectedDifficultyGridSize, selectedDifficultyMines, selectedDifficultyName); // Start a new game
        });

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setFont(new Font("Arial", 16));
        mainMenuButton.getStyleClass().add("main-menu"); // Apply common CSS style
        mainMenuButton.setOnAction(e -> {
            dialogStage.close();
            showStartScreen(); // Return to the start screen
        });

        buttonsContainer.getChildren().addAll(playAgainButton, mainMenuButton);
        dialogLayout.getChildren().addAll(messageLabel, buttonsContainer);

        Scene dialogScene = new Scene(dialogLayout);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void updateUI() {
        for (int r = 0; r < currentGridSize; r++) {
            for (int c = 0; c < currentGridSize; c++) {
                updateCellUI(grid[r][c]);
            }
        }

        // Update the mines counter based on currently placed flags.
        int flagsCount = 0;
        for (int r = 0; r < currentGridSize; r++) {
            for (int c = 0; c < currentGridSize; c++) {
                if (grid[r][c].isFlagged()) {
                    flagsCount++;
                }
            }
        }
        minesCounterLabel.setText("Mines: " + (currentMines - flagsCount));

        // Handle game over conditions (win or lose).
        if (gameLogic.isGameOver()) {
            if (gameLogic.isGameWon()) {
                newGameButton.setText("😎");
                stopTimer();
                // Save the game record if won.
                GameRecord record = new GameRecord(currentPlayerName, currentDifficultyName, secondsElapsed);
                dataManager.addRecord(record);
                System.out.println("Game Won! Record saved: " + record);
                showResultDialog(true);
            } else {
                // Player lost the game.
                newGameButton.setText("😵");
                showResultDialog(false);
            }
            stopTimer();
            disableAllButtons();
        } else {
            newGameButton.setText("😊");
        }
    }

    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        startTimeNano = System.nanoTime(); // Record start time in nanoseconds
        secondsElapsed = 0; // Reset seconds counter
        timerLabel.setText("Time: 0"); // Reset timer label

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameLogic.isGameOver()) {
                    gameTimer.stop();
                    return;
                }
                // Calculate elapsed seconds and update label if changed.
                int currentSeconds = (int) ((now - startTimeNano) / 1_000_000_000);
                if (currentSeconds != secondsElapsed) {
                    secondsElapsed = currentSeconds;
                    timerLabel.setText("Time: " + secondsElapsed);
                }
            }
        };
        gameTimer.start(); // Start the timer animation
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private String getNumberColor(int count) {
        switch (count) {
            case 1: return "#0000FF"; // Blue
            case 2: return "#008000"; // Green
            case 3: return "#FF0000"; // Red
            case 4: return "#00008B"; // DarkBlue
            case 5: return "#800000"; // Maroon
            case 6: return "#008080"; // Teal
            case 7: return "#000000"; // Black
            case 8: return "#808080"; // Gray
            default: return "#000000"; // Default to black for invalid counts
        }
    }

    private void highlightSelectedDifficultyButton(Button selectedButton) {
        // Remove selection style from all difficulty buttons.
        if (easyButton != null) easyButton.getStyleClass().remove("difficulty-button-selected");
        if (mediumButton != null) mediumButton.getStyleClass().remove("difficulty-button-selected");
        if (hardButton != null) hardButton.getStyleClass().remove("difficulty-button-selected");

        // Add selection style only to the newly selected button.
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("difficulty-button-selected");
        }
    }

    private void updateCellUI(Cell cell) {
        Button button = cell.getButton(); // Get the JavaFX Button associated with the cell

        // DEBUG FEATURE: Shows mines during gameplay for development purposes.
        // This block should ideally be removed for a final production build.
        /*
        if (cell.isMine() && !gameLogic.isGameOver()) {
            button.setText("M");
            button.setStyle("-fx-background-color: #FFDAB9; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");
            return; // Exit early to prevent further styling
        }
        */

        // Handle styling when the game is over (win or lose).
        if (gameLogic.isGameOver()) {
            button.setDisable(true); // Disable interaction with all cells after game over

            if (cell.isMine()) {
                if (cell.isOpen()) {
                    // Exploded mine
                    button.setText("💥");
                    button.setStyle("-fx-background-color: red; -fx-border-color: darkred;");
                } else if (cell.isFlagged() && gameLogic.isGameWon()) {
                    // Correctly flagged mine when game is won
                    button.setText("🚩");
                    button.setStyle("-fx-background-color: lightgreen; -fx-border-color: darkgreen;");
                } else if (cell.isFlagged() && !gameLogic.isGameWon()) {
                    // Flagged mine when game is lost (still a flag but context of loss)
                    button.setText("🚩");
                    button.setStyle("-fx-background-color: darkgreen; -fx-border-color: black;");
                } else {
                    // Mine that was not opened or flagged, revealed on game over (loss)
                    button.setText("💣");
                    button.setStyle("-fx-background-color: darkgray; -fx-border-color: #808080;");
                }
            } else if (cell.isFlagged() && !cell.isMine()) {
                // Incorrectly flagged cell (not a mine)
                button.setText("❌");
                button.setStyle("-fx-background-color: orange; -fx-border-color: darkorange;");
            } else {
                // Regular opened cell or un-flagged non-mine cell on game over
                button.setText(""); // Reset text
                button.setStyle("-fx-background-color: lightgray; -fx-border-color: darkgray;"); // Style for opened cell
                if (cell.getMinesAround() > 0) {
                    button.setText(String.valueOf(cell.getMinesAround()));
                    String textColor = getNumberColor(cell.getMinesAround());
                    // Apply font and text color for numbers.
                    button.setStyle("-fx-background-color: lightgray; -fx-border-color: darkgray; -fx-font-family: \"" + FONT_NAME_BENZIN_BOLD + "\"; -fx-text-fill: " + textColor + ";");
                }
            }
            return; // Exit as game is over, no further state changes needed
        }

        // Handle styling for cells during active gameplay.
        if (cell.isOpen()) {
            button.setText("");
            button.setStyle("-fx-background-color: lightgray; -fx-border-color: darkgray;"); // Default style for opened cell

            if (cell.getMinesAround() > 0) {
                button.setText(String.valueOf(cell.getMinesAround()));
                String textColor = getNumberColor(cell.getMinesAround());
                // Apply font and text color for numbers.
                button.setStyle("-fx-background-color: lightgray; -fx-border-color: darkgray; -fx-font-family: \"" + FONT_NAME_BENZIN_BOLD + "\"; -fx-text-fill: " + textColor + ";");
            }
        } else {
            // Cell is currently closed.
            button.setDisable(false);
            button.setText("");

            if (cell.isFlagged()) {
                // Cell is flagged
                button.setText("🚩");
                button.setStyle("-fx-background-color: yellowgreen; -fx-border-color: darkgreen;");
            } else if (cell.isQuestioned()) {
                // Cell is marked with a question mark
                button.setText("❓");
                button.setStyle("-fx-background-color: lightblue; -fx-border-color: darkblue;");
            } else {
                button.setStyle("-fx-background-color: #C0C0C0; -fx-border-color: #808080;"); // Standard Minesweeper grey
            }
        }
    }

    private void disableAllButtons() {
        for (int r = 0; r < currentGridSize; r++) {
            for (int c = 0; c < currentGridSize; c++) {
                grid[r][c].getButton().setDisable(true);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
