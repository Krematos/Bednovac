# Bednovac

Bednovac is a lightweight JavaFX desktop helper that converts a budget in your local currency into the number of CS2 cases + keys you can buy right now. The app fetches live case prices from the Steam Community Market and up-to-date exchange rates so you can plan your next unboxing session accurately.

## Features
- Choice of supported CS2 cases with current Steam Market pricing (USD base).
- Currency selector (USD, EUR, CZK) with automatic conversion via frankfurter.app.
- Instant calculation of purchasable case+key bundles and remaining balance.
- Built-in HTTP client with caching to avoid hammering third-party APIs.
- JavaFX UI packaged as both a runnable JAR and a custom runtime image.

## Prerequisites
- Java 21 JDK (the project targets Java 21).
- Maven 3.9+.
- Internet access for the Steam Market and frankfurter.app API calls.

## Getting Started
1. Clone or download this repository.
2. Open a terminal in the project root `Bednovac`.
3. Run `mvn clean javafx:run` to launch the app directly from source.

You can also build the executable artefacts:

- Runnable JAR: `mvn clean package` (the JAR is written to `target/Bednovac-1.0-SNAPSHOT.jar`).
- Custom runtime image (includes JRE): `mvn clean javafx:jlink` (output under `target/image`).

## Using the App
- Pick a CS2 case from the list on the left.
- Choose your currency and enter the amount you want to spend.
- Click `Exchange` to fetch the latest data and compute how many full case+key bundles you can afford.
- Use `Reset` to clear the input and results.

The UI performs API calls asynchronously and disables the `Exchange` button while a request is in flight. When network calls fail, the app falls back to cached data (if still fresh) and displays a user-friendly error message otherwise.

## Architecture Notes
- `com.example.bednovac.CaseHarrdener` bootstraps the JavaFX application and loads `MainView.fxml`.
- `MainController` wires UI events to the services and updates the labels safely on the FX thread.
- `PriceService` coordinates case pricing and exchange-rate lookups, applying in-memory caching with configurable TTLs (`Constants`).
- `ApiClient` uses `java.net.http.HttpClient` plus Jackson to parse JSON responses from Steam and frankfurter APIs.
- Model classes (`Case`, `Exchange`, `Currency`) encapsulate domain data and provide helpers like currency conversion.

## External APIs
- **Steam Community Market** (`https://steamcommunity.com/market/priceoverview`) for case pricing in USD.
- **frankfurter.app** (`https://api.frankfurter.app`) for currency exchange rates (USD base).

Both endpoints are queried without authentication; be mindful of their public rate limits.

## Logging & Diagnostics
- Java Util Logging is used throughout the service layer. Adjust logging levels via JVM properties or add a logging framework if you need richer output.
- When running via Maven, you can enable verbose logging with `mvn -Djava.util.logging.config.file=path/to/config javafx:run`.

## Troubleshooting
- **App fails to start**: verify that JavaFX modules are available; when running the JAR manually you must include `--module-path` pointing to JavaFX SDK unless you use the jlink image.
- **Network errors or empty prices**: API providers may rate-limit or return localized price strings; wait and retry, or ensure your system locale uses `.` as decimal separator.
- **Currency not listed**: extend the `Currency` enum and update API parsing to include the new code.

## Contributing
Pull requests are welcome. Please keep styles in `Style.css`, add new cases in `MainController`, and consider adding tests around service logic.

---

*Původní nápad: jednoduchá appka pro kamarády, která vezme částku a přepočítá ji na bedny a klíče v CS2. Aktuální data získává z veřejných API.*
