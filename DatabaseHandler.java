package de.eternal5.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {
    private Connection connection;
    private final String HOST = "localhost"; // Host für die MySQL-Datenbank
    private final String DATABASE = "SnakeGame"; // Name deiner Datenbank
    private final String USERNAME = "root"; // MySQL Benutzername
    private final String PASSWORD = ""; // Passwort für den MySQL Benutzer
    private final String DATABASE_URL = "jdbc:mysql://" + HOST + "/" + DATABASE;

    public DatabaseHandler() {
        connect();
    }

    private void connect() {
        try {
            // Verbindung zur Datenbank aufbauen
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            System.out.println("Datenbank verbunden.");
        } catch (SQLException e) {
            System.out.println("Datenbankverbindung fehlgeschlagen: " + e.getMessage());
        }
    }

    // Methode zum Speichern des Highscores (Beispiel)
    public void saveHighscore(String username, int score) {
        String query = "INSERT INTO DB (Nutzername, Score) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            System.out.println("Highscore gespeichert.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Speichern des Highscores: " + e.getMessage());
        }
    }

    // Weitere Methoden zum Arbeiten mit der Datenbank könnten hier hinzugefügt werden
}
