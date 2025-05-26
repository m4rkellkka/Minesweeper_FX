# Minesweeper Game (JavaFX)

## Project Overview

This is a classic Minesweeper game implemented using JavaFX for the graphical user interface. The project aims to provide a functional and engaging single-player experience with customizable difficulty levels and a local leaderboard to track player best times.

## Features

* **Classic Minesweeper Gameplay:** All standard rules of Minesweeper apply (reveal cells, flag mines, use numbers to deduce mine locations).
* **Multiple Difficulty Levels:**
    * **Easy:** 10x10 grid, 10 mines
    * **Medium:** 12x12 grid, 20 mines
    * **Hard:** 14x14 grid, 25 mines
* **First Click Safety:** The first clicked cell is guaranteed not to be a mine, and mines are placed after the first click to ensure a playable start.
* **Chord Functionality:** Quickly clear surrounding cells when the correct number of flags are placed around an open numbered cell.
* **Timer:** Tracks game duration for performance measurement.
* **Mine Counter:** Displays the remaining number of unflagged mines.
* **Interactive Start Screen:** Allows players to enter their name and select difficulty.
* **Local Leaderboard:** Stores and displays best times for each difficulty, allowing players to compete with their own past records.
* **Responsive UI:** The game board resizes appropriately to fit the stage.
* **Custom Styling:** Utilizes CSS for a polished look and feel.

## Screenshots

**Start Screen:**
![Start Screen](\images\start_screen.JPG)

**Game in Progress:**
![Game in Progress](images\game_in_progress.JPG)

**Leaderboard:**
![Leaderboard](images\leaderboard.JPG)

**Win dialog:**
![Win dialog](images\win.JPG)



## How to Run

To build and run this Minesweeper game, you will need Java Development Kit (JDK) 11 or higher and Maven installed.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/m4rkellkka/Minesweeper.git](https://github.com/m4rkellkka/Minesweeper.git)
    cd Minesweeper
    ```

2.  **Build the project using Maven:**
    ```bash
    mvn clean package
    ```

3.  **Run the application:**
    ```bash
    java -jar target/minesweeper-1.0-SNAPSHOT.jar 
    ```
    *(Note: The exact `.jar` file name might vary based on your `pom.xml` configuration. Check the `target` directory for the correct name.)*

    Alternatively, you can run it directly from your IDE (e.g., IntelliJ IDEA, Eclipse) by running the `App` class.

## Technologies Used

* **Java 11+**
* **JavaFX:** For building the graphical user interface.
* **Maven:** For project management and build automation.
* **CSS:** For UI styling.

## Project Structure (Key Files)

* `src/main/java/com/example/minesweeper/App.java`: The main application class, handling UI navigation, game initialization, and event handling.
* `src/main/java/com/example/minesweeper/Cell.java`: Represents a single cell on the Minesweeper board, storing its state and a reference to its JavaFX Button.
* `src/main/java/com/example/minesweeper/MinesweeperGame.java`: Contains the core game logic, including mine placement, opening cells, and game state management.
* `src/main/java/com/example/minesweeper/DataManager.java`: (Assumed) Handles loading and saving game records for the leaderboard.
* `src/main/java/com/example/minesweeper/GameRecord.java`: (Assumed) A data class to represent a single entry in the leaderboard.
* `src/main/resources/StartScreen.fxml`: FXML layout for the initial start screen.
* `src/main/resources/style.css`: CSS file for styling the entire application.

## Author

* **Mikhail Savushkin, student from Topkapi university**