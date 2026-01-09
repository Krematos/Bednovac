/**
 * Modul `bednovac` je hlavní modul aplikace, která slouží k výpočtu ceny CS:GO
 * beden a klíčů.
 * Definuje závislosti na JavaFX a modulech pro zpracování JSON a HTTP.
 */
module bednovac {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.logging;
    requires java.net.http;

    requires com.fasterxml.jackson.databind;

    opens com.example.bednovac to javafx.fxml;
    opens com.example.bednovac.controller to javafx.fxml;

    exports com.example.bednovac;
    exports com.example.bednovac.controller;
    exports com.example.bednovac.model;
}