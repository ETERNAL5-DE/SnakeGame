package de.eternal5.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SnakeGame extends Application {

    // Spielgrößen
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int BLOCK_SIZE = 20;

    // Bewegungsrichtung
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction direction = Direction.RIGHT;
    private boolean gameOver = false;

    // Schlange und Futter
    private List<int[]> snake = new ArrayList<>();
    private int[] food = new int[2];

    // Zufälliger Generator
    private Random random = new Random();

    // Farben
    private Color snakeColor;
    private Color backgroundColor;

    // Timeline für die Animation
    private Timeline timeline;

    private Stage primaryStage;
    private Scene gameScene;
    private Canvas canvas;

    // Highscore-Variablen
    private int score = 0;
    private String playerName;
    private int highScore = 0;
    private String highScoreName = "";

    // Datenbank-Handler
    private DatabaseHandler databaseHandler;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.databaseHandler = new DatabaseHandler(); // Datenbank initialisieren
        new StartMenu(this); // Starte das Startmenü
    }

    public void startGame(Color snakeColor, Color backgroundColor, String playerName) {
        this.snakeColor = snakeColor;
        this.backgroundColor = backgroundColor;
        this.playerName = playerName;
        createGameScene();
    }

    // Spielszene erstellen
    private void createGameScene() {
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Initialisiere die Schlange
        snake.clear();
        snake.add(new int[]{WIDTH / (2 * BLOCK_SIZE), HEIGHT / (2 * BLOCK_SIZE)});
        direction = Direction.RIGHT;
        gameOver = false;
        score = 0;  // Score zurücksetzen
        spawnFood();

        // Animation (Timeline) für das Update des Spiels
        timeline = new Timeline(new KeyFrame(Duration.millis(150), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Szene und Eingabesteuerung
        gameScene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, this::processInput);

        // Setze die Szene auf der Bühne
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();
    }

    // Verarbeitung der Tasten zur Steuerung
    private void processInput(KeyEvent event) {
        if (event.getCode() == KeyCode.UP && direction != Direction.DOWN) {
            direction = Direction.UP;
        } else if (event.getCode() == KeyCode.DOWN && direction != Direction.UP) {
            direction = Direction.DOWN;
        } else if (event.getCode() == KeyCode.LEFT && direction != Direction.RIGHT) {
            direction = Direction.LEFT;
        } else if (event.getCode() == KeyCode.RIGHT && direction != Direction.LEFT) {
            direction = Direction.RIGHT;
        } else if (event.getCode() == KeyCode.ESCAPE) { // Mit ESC die Anwendung schließen
            primaryStage.close();
        }
    }

    // Logik des Spiels
    private void run(GraphicsContext gc) {
        if (gameOver) {
            timeline.stop();
            displayGameOverAlert();  // Zeigt die Alert-Box bei Game Over
            return;
        }

        // Schlange bewegen
        moveSnake();

        // Kollisionen prüfen
        checkCollisions();

        // Zeichenfläche löschen und Hintergrund zeichnen
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Schlange und Futter zeichnen
        drawSnake(gc);
        drawFood(gc);

        // Punkte aktualisieren und anzeigen
        updateScore(gc);
    }

    // Schlange bewegen
    private void moveSnake() {
        int[] head = snake.get(0);
        int[] newHead = new int[]{head[0], head[1]};

        switch (direction) {
            case UP:
                newHead[1]--;
                break;
            case DOWN:
                newHead[1]++;
                break;
            case LEFT:
                newHead[0]--;
                break;
            case RIGHT:
                newHead[0]++;
                break;
        }

        snake.add(0, newHead);

        // Überprüfe, ob die Schlange das Futter isst
        if (newHead[0] == food[0] && newHead[1] == food[1]) {
            spawnFood();  // Futter an eine neue Stelle setzen
            score++;  // Punkte erhöhen
        } else {
            snake.remove(snake.size() - 1); // Entferne den letzten Teil der Schlange
        }
    }

    // Kollisionen prüfen (mit Wänden und dem eigenen Körper)
    private void checkCollisions() {
        int[] head = snake.get(0);

        // Kollision mit Wänden
        if (head[0] < 0 || head[1] < 0 || head[0] >= WIDTH / BLOCK_SIZE || head[1] >= HEIGHT / BLOCK_SIZE) {
            gameOver = true;
        }

        // Kollision mit dem eigenen Körper
        for (int i = 1; i < snake.size(); i++) {
            if (head[0] == snake.get(i)[0] && head[1] == snake.get(i)[1]) {
                gameOver = true;
                break;
            }
        }
    }

    // Schlange zeichnen
    private void drawSnake(GraphicsContext gc) {
        gc.setFill(snakeColor);
        for (int[] part : snake) {
            gc.fillRect(part[0] * BLOCK_SIZE, part[1] * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        }
    }

    // Futter zeichnen
    private void drawFood(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(food[0] * BLOCK_SIZE, food[1] * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }

    // Futter an einer zufälligen Stelle spawnen
    private void spawnFood() {
        food[0] = random.nextInt(WIDTH / BLOCK_SIZE);
        food[1] = random.nextInt(HEIGHT / BLOCK_SIZE);
    }

    // Game Over Alert
    private void displayGameOverAlert() {
        // Verwende Platform.runLater, um den Dialog sicher zu zeigen
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Game Over");
            alert.setHeaderText("Du hast verloren!");
            alert.setContentText("Dein Score: " + score + "\nHighscore: " + highScore + " von " + highScoreName);

            ButtonType restartButton = new ButtonType("Neustarten");
            ButtonType closeButton = new ButtonType("Schließen");

            // Füge die Optionen hinzu
            alert.getButtonTypes().setAll(restartButton, closeButton);

            // Warte auf die Auswahl des Nutzers
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == restartButton) {
                restartGame();  // Spiel neustarten
            } else if (result.isPresent() && result.get() == closeButton) {
                primaryStage.close();  // Anwendung beenden
            }

            // Speichere den Highscore
        /*if (score > highScore) {
            highScore = score;
            highScoreName = playerName;
            databaseHandler.saveHighScore(playerName, highScore); // Speichern des Highscores
        }*/
        });
    }


    // Punkte aktualisieren und anzeigen
    private void updateScore(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillText("Punkte: " + score, 10, 20); // Punkte oben links anzeigen
    }

    // Neustart des Spiels
    private void restartGame() {
        createGameScene();
    }
}
