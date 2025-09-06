package com.example.bednovac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CaseHarrdener extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CaseHarrdener.class.getResource("/com/example/bednovac/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        //scene.getStylesheets().add(CaseHarrdener.class.getResource("/com/example/bednovac/style.css").toExternalForm());
        stage.setTitle("Case Harrdener");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}