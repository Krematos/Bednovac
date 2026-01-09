package com.example.bednovac.controller;

import com.example.bednovac.service.ApiClient;
import com.example.bednovac.service.PriceService;
import com.example.bednovac.model.Case;
import com.example.bednovac.model.Currency;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.CompletableFuture;

/**
 * Controller pro hlavní okno aplikace, který zpracovává události a interakce s
 * uživatelským rozhraním.
 * Tato třída implementuje Initializable pro inicializaci komponent po načtení
 * FXML souboru.
 */
public class MainController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ApiClient.class.getName());

    @FXML
    private Button exchangeButton;

    @FXML
    private TextField amountTextField;

    @FXML
    private Button resetButton;

    @FXML
    private Label moneyLabel;

    @FXML
    private Label keysLabel;

    @FXML
    private Label casesLabel;

    @FXML
    private ChoiceBox<Currency> currencyChoiceBox;

    @FXML
    private ChoiceBox<String> caseChoiceBox;

    private PriceService priceService;

    /**
     * Inicializace controlleru. Tato metoda bude volána automaticky po načtení FXML
     * souboru.
     * Nastavuje výchozí hodnoty pro výběr měny a beden a inicializuje služby.
     *
     * @param url       Umístění kořenového objektu.
     * @param resources Zdroje použité pro lokalizaci kořenového objektu.
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        LOGGER.info("Initializing MainController...");
        currencyChoiceBox.getItems().addAll(Currency.values());
        currencyChoiceBox.getSelectionModel().select(Currency.USD); // Nastaví výchozí měnu na USD
        currencyChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LOGGER.info("Selected currency: " + newValue);
            }
        });
        LOGGER.info("Currency ChoiceBox initialized with items: " + currencyChoiceBox.getItems());

        // Naplnění ChoiceBoxu s bednami
        caseChoiceBox.getItems().addAll(
                "Kilowatt Case",
                "Revolution Case",
                "Spectrum Case",
                "Danger Zone Case",
                "Prisma 2 Case",
                "Prisma Case",
                "Chroma 3 Case",
                "Chroma 2 Case",
                "Chroma Case",
                "Falchion Case",
                "Horizon Case",
                "Gamma 2 Case",
                "Gamma Case",
                "Glove Case",
                "Operation Hydra Case");
        caseChoiceBox.getSelectionModel().selectFirst(); // Nastaví výchozí bednu
        caseChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LOGGER.info("Selected case: " + newValue);
            }
        });
        LOGGER.info("Case ChoiceBox initialized with items: " + caseChoiceBox.getItems());
        casesLabel.setText("0 Cases");
        keysLabel.setText("0 Keys");
        moneyLabel.setText("0 USD");

        ApiClient apiClient = new ApiClient();
        this.priceService = new PriceService(apiClient);
        loadInitialData();
    }

    /**
     * Načte úvodní data.
     * (Zatím prázdná metoda, připravena pro budoucí rozšíření).
     */
    public void loadInitialData() {

    }

    /**
     * Metoda pro získání ceny klíče.
     * 
     * @return Cena klíče v USD.
     */

    /**
     * Metoda pro zpracování události kliknutí na tlačítko "Vypočítat"
     * (exchangeButton).
     * Získá data od uživatele, stáhne aktuální ceny a kurzy a provede výpočet.
     */
    @FXML
    public void handleExchangeButtonClick() {
        exchangeButton.setDisable(true); // Zabrání opakovanému kliknutí během zpracování
        LOGGER.info("Exchange button clicked.");

        // 1. Získání částky z textového pole
        String amountText = amountTextField.getText().replace(",", ".").replace(" ", ""); // Nahraď čárku tečkou a
                                                                                          // odstraň mezery
        if (amountText.isEmpty()) {
            Platform.runLater(() -> { // Aktualizace UI musí proběhnout na FX threadu
                moneyLabel.setText("Zadejte částku.");
                exchangeButton.setDisable(false);
            });
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                moneyLabel.setText("Neplatný formát částky.");
                exchangeButton.setDisable(false);
            });
            LOGGER.log(Level.WARNING, "Invalid amount entered: " + amountText, e);
            return;
        }

        Currency selectedCurrency = currencyChoiceBox.getValue();
        if (selectedCurrency == null) {
            Platform.runLater(() -> {
                moneyLabel.setText("Vyberte měnu.");
                exchangeButton.setDisable(false);
            });
            return;
        }

        String selectedCase = caseChoiceBox.getValue();
        if (selectedCase == null || selectedCase.isEmpty()) {
            Platform.runLater(() -> {
                moneyLabel.setText("Vyberte bednu.");
                exchangeButton.setDisable(false);
            });
            return;
        }

        LOGGER.info("Starting calculation for amount: " + amount + " " + selectedCurrency.name() + " for case: "
                + selectedCase);

        // 2. Asynchronní získání cen bedny a směnných kurzů
        CompletableFuture<Double> casePriceFuture = priceService.getCasePrice(selectedCase)
                .thenApply(caseData -> {
                    if (caseData != null) {
                        return caseData.getPrice(); // Cena bedny v USD
                    } else {
                        LOGGER.warning("Failed to fetch case price for: " + selectedCase);
                        return null;
                    }
                });

        CompletableFuture<Double> exchangeRateFuture = priceService.getExchange()
                .thenApply(exchange -> {
                    if (exchange != null) {
                        return exchange.getRate(selectedCurrency); // Směnný kurz pro vybranou měnu
                    } else {
                        LOGGER.warning("Failed to fetch exchange rates.");
                        return null;
                    }
                });

        // 3. Kombinace výsledků obou asynchronních volání a finální výpočet
        CompletableFuture.allOf(casePriceFuture, exchangeRateFuture)
                .thenAccept(v -> { // thenAccept je vhodné, když už nepotřebujeme vracet další CompletableFuture
                    try {
                        Double casePriceUsd = casePriceFuture.get(); // Získá cenu bedny
                        Double exchangeRate = exchangeRateFuture.get(); // Získá směnný kurz

                        if (casePriceUsd == null || exchangeRate == null || exchangeRate <= 0) {
                            Platform.runLater(() -> moneyLabel.setText("Chyba při získávání dat (ceny/kurzu)."));
                            return;
                        }

                        double keyPriceUsd = getPriceService().getKeyPrice(); // Získá cenu klíče (konstanta v USD)

                        // VÝPOČET:
                        // A. Převeď uživatelskou částku na USD
                        // Předpoklad: exchangeRate vyjadřuje "Kolik jednotek měny je za 1 USD" (např.
                        // 23.5 CZK = 1 USD)
                        // Proto DĚLÍME zadanou částku kurzem, abychom dostali hodnotu v USD.
                        double userAmountUsd = amount / exchangeRate;

                        // B. Vypočítá celkovou cenu jednoho balíčku (bedna + klíč) v USD
                        double totalBundleCostUsd = casePriceUsd + keyPriceUsd;

                        if (totalBundleCostUsd <= 0) {
                            Platform.runLater(() -> moneyLabel.setText("Cena bedny + klíče je neplatná."));
                            return;
                        }

                        // C. Vypočítá, kolik celých balíčků si uživatel může koupit
                        int numberOfBundles = (int) (userAmountUsd / totalBundleCostUsd);

                        // D. Vypočítá zbytek peněz v USD
                        double remainingUsd = userAmountUsd % totalBundleCostUsd;

                        // E. Převod zbytku peněz zpět na původní měnu
                        double remainingInSelectedCurrency = remainingUsd * exchangeRate; // NÁSOBÍME pro zpětný převod

                        // F. Aktualizace UI (VŽDY musí běžet na JavaFX aplikačním threadu!)
                        Platform.runLater(() -> {
                            casesLabel.setText(String.format("Bedny: %d", numberOfBundles));
                            keysLabel.setText(String.format("Klíče: %d", numberOfBundles)); // Stejný počet klíčů jako
                                                                                            // beden
                            // Zobrazíme zbytek peněz (volitelné, zatím není v UI dedikovaný label, případně
                            // update moneyLabel)
                            // moneyLabel.setText(String.format("Zbytek: %.2f %s",
                            // remainingInSelectedCurrency, selectedCurrency.name()));

                            exchangeButton.setDisable(false); // Znovu povolit tlačítko
                        });

                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Chyba při výpočtu v thenAccept: " + e.getMessage(), e);
                        Platform.runLater(() -> {
                            moneyLabel.setText("Nastala chyba při výpočtu.");
                            exchangeButton.setDisable(false);
                        });
                    }
                })
                .exceptionally(e -> { // Zachycení chyb z CompletableFuture.allOf
                    LOGGER.log(Level.SEVERE, "Chyba při získávání API dat: " + e.getMessage(), e);
                    Platform.runLater(() -> {
                        moneyLabel.setText("Nepodařilo se získat data z API. Zkuste to znovu.");
                        exchangeButton.setDisable(false);
                    });
                    return null; // Důležité, aby exceptionally něco vrátilo (Void)
                });
    }

    /**
     * Metoda pro zpracování události kliknutí na tlačítko "Reset" (resetButton).
     * Vymaže zadanou částku a resetuje labely na nulu.
     */
    @FXML
    public void handleResetButtonClick() {
        // Resetování textového pole a štítků
        amountTextField.clear();
        moneyLabel.setText("0 USD");
        keysLabel.setText("0 Keys");
        casesLabel.setText("0 Cases");
    }

    // --- Gettery a Settery pro FXML komponenty ---
    // (Užitečné pro testování a přístup k UI prvkům)

    public PriceService getPriceService() {
        return priceService;
    }

    public void setPriceService(PriceService priceService) {
        this.priceService = priceService;
    }

    public void setMoneyLabel(Label moneyLabel) {
        this.moneyLabel = moneyLabel;
    }

    public Label getMoneyLabel() {
        return moneyLabel;
    }

    public void setExchangeButton(Button exchangeButton) {
        this.exchangeButton = exchangeButton;
    }

    public Button getExchangeButton() {
        return exchangeButton;
    }

    public void setAmoutTextField(TextField amoutTextField) {
        this.amountTextField = amoutTextField;
    }

    public TextField getAmoutTextField() {
        return amountTextField;
    }

    public void setResetButton(Button resetButton) {
        this.resetButton = resetButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public void setKeysLabel(Label keysLabel) {
        this.keysLabel = keysLabel;
    }

    public Label getKeysLabel() {
        return keysLabel;
    }

    public void setCasesLabel(Label casesLabel) {
        this.casesLabel = casesLabel;
    }

    public Label getCasesLabel() {
        return casesLabel;
    }

    public void setCaseChoiceBox(ChoiceBox<String> caseChoiceBox) {
        this.caseChoiceBox = caseChoiceBox;
    }

    public ChoiceBox<String> getCaseChoiceBox() {
        return caseChoiceBox;
    }

}
