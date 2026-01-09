package com.example.bednovac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Hlavní třída aplikace `CaseHarrdener`, která spouští JavaFX aplikaci.
 * Dědí od třídy `Application` a zajišťuje načtení hlavního okna (Stage).
 */
public class CaseHarrdener extends Application {

    /**
     * Hlavní vstupní bod pro JavaFX aplikaci.
     * Načítá FXML soubor, nastavuje scénu a zobrazuje hlavní okno.
     *
     * @param stage Primární stage (okno) aplikace.
     * @throws IOException Pokud se nepodaří načíst FXML soubor.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CaseHarrdener.class.getResource("/com/example/bednovac/MainView.fxml"));
        URL fxmlUrl = CaseHarrdener.class.getResource("/com/example/bednovac/MainView.fxml");
        System.out.println("FXML path: " + fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Načtení a aplikace CSS stylů
        System.out.println(CaseHarrdener.class.getResource("/com/example/bednovac/Style.css"));
        scene.getStylesheets().add(Objects
                .requireNonNull(CaseHarrdener.class.getResource("/com/example/bednovac/Style.css")).toExternalForm());

        stage.setTitle("Case Harrdener");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Hlavní metoda programu, která spouští JavaFX aplikaci.
     *
     * @param args Argumenty příkazové řádky.
     */
    public static void main(String[] args) {
        System.out.println("Starting Bednovac...");
        launch();
    }
}