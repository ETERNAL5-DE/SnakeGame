package de.eternal5.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StartMenu {
    private SnakeGame snakeGame;

    public StartMenu(SnakeGame snakeGame) {
        this.snakeGame = snakeGame;
        showMenu();
    }

    private void showMenu() {
        Stage stage = new Stage();
        stage.setTitle("Snake Game - Startmenü");

        // Erstelle die GUI-Elemente
        Label usernameLabel = new Label("Benutzername:");
        TextField usernameField = new TextField();

        Label snakeColorLabel = new Label("Wähle die Schlange Farbe:");
        ColorPicker snakeColorPicker = new ColorPicker(Color.GREEN);

        Label backgroundColorLabel = new Label("Wähle den Hintergrund Farbe:");
        ColorPicker backgroundColorPicker = new ColorPicker(Color.WHITE);

        Button startButton = new Button("Spiel Starten");
        startButton.setOnAction(e -> {
            String playerName = usernameField.getText();
            Color snakeColor = snakeColorPicker.getValue();
            Color backgroundColor = backgroundColorPicker.getValue();
            snakeGame.startGame(snakeColor, backgroundColor, playerName);
            stage.close(); // Schließe das Startmenü
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(snakeColorLabel, 0, 1);
        grid.add(snakeColorPicker, 1, 1);
        grid.add(backgroundColorLabel, 0, 2);
        grid.add(backgroundColorPicker, 1, 2);
        grid.add(startButton, 0, 3, 2, 1);

        // Szene und Darstellung
        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
}


