# Bednovac

Bednovac je lehký desktopový pomocník v JavaFX, který převede váš rozpočet v místní měně na počet CS2 beden + klíčů, které si můžete právě teď koupit. Aplikace stahuje aktuální ceny beden ze Steam Community Marketu a aktuální směnné kurzy, abyste si mohli přesně naplánovat své další otevírání beden.

## Funkce
- Výběr podporovaných CS2 beden s aktuálními cenami ze Steam Marketu (v USD).
- Výběr měny (USD, EUR, CZK) s automatickým převodem přes frankfurter.app.
- Okamžitý výpočet balíčků (bedna + klíč), které lze zakoupit, a zbývajícího zůstatku.
- Vestavěný HTTP klient s mezipamětí (caching), aby se zabránilo přetěžování API třetích stran.
- JavaFX UI zabalené jako spustitelný JAR i jako custom runtime image.

## Požadavky
- Java 21 JDK (projekt cílí na Java 21).
- Maven 3.9+.
- Přístup k internetu pro volání API Steam Marketu a frankfurter.app.

## Začínáme
1. Naklonujte nebo stáhněte tento repozitář.
2. Otevřete terminál v kořenovém adresáři projektu `Bednovac`.
3. Spusťte `mvn clean javafx:run` pro přímé spuštění aplikace ze zdrojového kódu.

Můžete také sestavit spustitelné artefakty:

- Spustitelný JAR: `mvn clean package` (JAR se zapíše do `target/Bednovac-1.0-SNAPSHOT.jar`).
- Custom runtime image (obsahuje JRE): `mvn clean javafx:jlink` (výstup pod `target/image`).

## Používání aplikace
- Vyberte CS2 bednu z seznamu vlevo.
- Zvolte měnu a zadejte částku, kterou chcete utratit.
- Klikněte na `Exchange` pro načtení nejnovějších dat a výpočet, kolik plných balíčků (bedna + klíč) si můžete dovolit.
- Použijte `Reset` pro vymazání vstupu a výsledků.

UI provádí volání API asynchronně a během požadavku deaktivuje tlačítko `Exchange`. Pokud síťová volání selžou, aplikace použije data z mezipaměti (pokud jsou stále čerstvá), jinak zobrazí uživatelsky přívětivou chybovou hlášku.

## Poznámky k architektuře
- `com.example.bednovac.CaseHarrdener` spouští JavaFX aplikaci a načítá `MainView.fxml`.
- `MainController` propojuje události UI se službami a bezpečně aktualizuje popisky na FX vlákně.
- `PriceService` koordinuje zjišťování cen beden a směnných kurzů, přičemž používá in-memory caching s konfigurovatelným TTL (`Constants`).
- `ApiClient` používá `java.net.http.HttpClient` a Jackson pro parsování JSON odpovědí z API Steamu a frankfurter.
- Modelové třídy (`Case`, `Exchange`, `Currency`) zapouzdřují doménová data a poskytují pomocné funkce, jako je převod měn.

## Externí API
- **Steam Community Market** (`https://steamcommunity.com/market/priceoverview`) pro ceny beden v USD.
- **frankfurter.app** (`https://api.frankfurter.app`) pro směnné kurzy (základ USD).

Oba endpointy jsou dotazovány bez autentizace; mějte na paměti jejich veřejné limity (rate limits).

## Logování a diagnostika
- Java Util Logging je použit napříč servisní vrstvou. Úroveň logování upravte pomocí vlastností JVM nebo přidejte logovací framework, pokud potřebujete bohatší výstup.
- Při spouštění přes Maven můžete povolit podrobné logování pomocí `mvn -Djava.util.logging.config.file=cesta/k/config javafx:run`.

## Řešení problémů
- **Aplikace se nespustí**: ověřte, že jsou dostupné moduly JavaFX; při ručním spouštění JAR musíte zahrnout `--module-path` ukazující na JavaFX SDK, pokud nepoužíváte jlink image.
- **Chyby sítě nebo prázdné ceny**: poskytovatelé API mohou omezovat rychlost (rate-limit) nebo vracet lokalizované řetězce cen; počkejte a zkuste to znovu, nebo zajistěte, že vaše systémové nastavení používá `.` jako oddělovač desetinných míst.
- **Měna není v seznamu**: rozšiřte enum `Currency` a aktualizujte parsování API, aby zahrnovalo nový kód.

## Přispívání
Pull requesty jsou vítány. Prosím, dodržujte styly v `Style.css`, nové bedny přidávejte v `MainController` a zvažte přidání testů pro logiku služeb.

---

*Původní nápad: jednoduchá appka pro kamarády, která vezme částku a přepočítá ji na bedny a klíče v CS2. Aktuální data získává z veřejných API.*
