package com.example.bednovac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CaseHarrdener extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CaseHarrdener.class.getResource("/com/example/bednovac/MainView.fxml"));
        URL fxmlUrl = CaseHarrdener.class.getResource("/com/example/bednovac/MainView.fxml");
        System.out.println("FXML path: " + fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        System.out.println(CaseHarrdener.class.getResource("/com/example/bednovac/Style.css"));
        scene.getStylesheets().add(Objects.requireNonNull(CaseHarrdener.class.getResource("/com/example/bednovac/Style.css")).toExternalForm());
        stage.setTitle("Case Harrdener");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}