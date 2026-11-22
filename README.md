# 🌿 Gideon's RömerPlant

**RömerPlant** ist eine private, lokal arbeitende Android-App zur liebevollen Verwaltung deiner Zimmer- und Gartenpflanzen – gestaltet im **Botanical Warmth**-Design mit KI-gestützter Pflanzenbestimmung, Gesundheitsanalyse, interaktivem KI-Botaniker und saisonaler Pflegeintelligenz über die Mistral-API.

> **Datenschutz zuerst:** Alle Pflanzen, Fotos und Einstellungen bleiben ausschließlich lokal auf deinem Gerät (Room + DataStore). Keine Nutzerkonten, keine Cloud, keine Telemetrie.

---

## ✨ Funktionen

### 📸 Erfassen & Bestimmen
- **Flexibel erfassen**: Direkt per Kamera, aus der Galerie (Android Photo Picker) oder komplett manuell.
- **KI-Fotoanalyse (Mistral Vision, Standard `pixtral-12b-latest`)**:
  - Bestimmt botanische Art, deutschen Namen, Familie und Beschreibung.
  - Erstellt vollständige Pflegehinweise (Licht, Gießen, Luftfeuchtigkeit, Temperatur, Düngen, Substrat) unter Berücksichtigung der **aktuellen Jahreszeit**.
  - Liefert eine ehrliche Gesundheitsbewertung (`gesund`, `leicht gestresst`, `krank`, `Schädlingsbefall`) inklusive konkreter Handlungsschritte.
- **KI-Pflegeplan ohne Foto**: Gib einfach Namen oder Art ein – RömerPlant füllt den kompletten botanischen Steckbrief und alle Pflegefelder automatisch per KI aus.

### 🧠 Intelligente Pflanzenpflege
- **Interaktiver KI-Botaniker in der Detailansicht**: Stelle gezielte Fragen zu jeder Pflanze (z. B. *„Warum werden Blattspitzen braun?“*, *„Giftig für Haustiere?“*, *„Wann umtopfen?“*) – mit Schnellfragen-Chips und vollem Pflanzen- & Saisonkontext.
- **Smarter Garten-Check & saisonale Tipps**: Erkennt automatisch pflegebedürftige Pflanzen auf der Startseite und zeigt täglich passende Pflegetipps für Frühling, Sommer, Herbst und Winter.
- **Dynamische Pflege-Indikatoren**: Wasser-, Licht-, Luftfeuchte- und Temperatur-Badges werden automatisch aus dem jeweiligen Pflegeprofil abgeleitet.
- **Volltext-Suche & Sortierung**: Suche nach Name, Art, Familie, Status oder Notizen; sortierbar nach **A–Z** oder **Neu**.

### 🎨 Design & Personalisierung
- **4 abgestimmte Farbschemata** (jeweils Light & Dark Mode): *Herbarium*, *Waldgrün*, *Blütenrosa* und *Terrakotta*.
- **Adaptives & monochromes App-Icon** (inkl. Android 13+ Themed Icons).
- **Alles editierbar**: Sämtliche KI-Felder und Tagebuch-Notizen lassen sich jederzeit manuell anpassen.
- **Diagnose-Report**: Erstellt auf Wunsch einen strukturierten Markdown-Report (ohne API-Key) zum einfachen Teilen bei Feedback oder Fehlermeldungen.

---

## 🛠️ Technik-Stack

| Bereich | Technologien |
| :--- | :--- |
| **Sprache & UI** | Kotlin 2.4, Jetpack Compose, Material 3, Google Fonts (*Epilogue*, *Quicksand*, *Nunito Sans*) |
| **Architektur** | MVVM + Unidirectional Data Flow (`StateFlow`), Navigation Compose |
| **DI & Persistenz** | Hilt, Room (SQLite, inkl. Migrationen), Jetpack DataStore |
| **Netzwerk & KI** | Retrofit 3, OkHttp 5, `kotlinx.serialization`, Mistral Chat Completions + JSON-Schema |
| **Medien & Qualität** | Coil, FileProvider, Android Photo Picker, JUnit-Tests, GitHub Actions CI & Release |

---

## 🚀 Schnellstart & Einrichtung

1. **App installieren**: Lade die neueste signierte `RoemerPlant-v*.apk` aus den [GitHub Releases](https://github.com/gschmalzhaf-cell/PlanzenR-mer/releases) herunter – oder öffne das Projekt in **Android Studio** und starte es auf einem Gerät (ab **Android 8.0 / API 26**).
2. **Mistral API-Key hinterlegen** *(optional, für KI-Funktionen)*:
   - Erstelle einen Key unter [console.mistral.ai/api-keys](https://console.mistral.ai/api-keys).
   - Öffne in der App **Profil → Mistral-KI & Modell**, trage den Key ein und speichere.
   - Optional über **„Verfügbare Modelle laden“** dein bevorzugtes Vision-Modell auswählen (Empfehlung: `pixtral-12b-latest`).
3. **Erste Pflanze erfassen**: Tippe in der Mitte der Navigationsleiste auf **Kamera**, fotografiere deine Pflanze und tippe auf **„Pflanze bestimmen“** – oder trage den Namen ein und nutze **„Pflegeplan per KI ergänzen“**.

---

## 📦 Build & Release

```bash
# Unit-Tests ausführen
./gradlew testDebugUnitTest

# Debug-APK bauen (app/build/outputs/apk/debug/)
./gradlew assembleDebug

# Signierte Release-APK bauen (Details siehe SECRETS.md)
./gradlew assembleRelease
```

Jeder Push auf `main` startet die **Android CI** ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)). Jeder Git-Tag im Format `v*` erzeugt automatisch ein signiertes **GitHub Release** ([`.github/workflows/release.yml`](.github/workflows/release.yml)). Informationen zu Keystore-Secrets stehen in [`SECRETS.md`](SECRETS.md).

---

## 💡 Ideen & Roadmap (Feature-Vorschläge & Optimierungen)

Die folgenden Ideen eignen sich hervorragend als nächste Ausbaustufen für RömerPlant:

### 🌱 Intelligente Pflege & KI-Features
1. **Smarte Gieß- & Dünge-Erinnerungen (WorkManager)**:
   - Die Datenbank-Felder (`lastWateredAt`, `waterIntervalDays`, `lastFertilizedAt`, `fertilizeIntervalDays`) sind bereits im Modell vorhanden. Mit `WorkManager` lassen sich dezente, wetter- oder saisonabhängige Pflege-Benachrichtigungen und ein „Heute fällig“-Überblick realisieren.
2. **Wachstums- & Gesundheits-Zeitstrahl (Foto-Verlauf)**:
   - Mehrere Fotos pro Pflanze mit Zeitstempel speichern, um das Wachstum über Monate zu vergleichen – inklusive KI-Vorher/Nachher-Vergleich (*„Hat sich der Zustand seit letztem Monat verbessert?“*).
3. **Haustier- & Kinder-Sicherheits-Check**:
   - Strukturierte KI-Angabe zur Giftigkeit für Katzen, Hunde und Kleinkinder als klares Badge im Steckbrief.
4. **Standort- & Raum-Zuordnung**:
   - Räume anlegen (*Wohnzimmer Süd*, *Schlafzimmer*, *Balkon*) und Pflanzen danach filtern – inkl. KI-Bewertung, ob der gewählte Standort zur Pflanze passt.
5. **Lichtmesser über den Smartphone-Sensor**:
   - Nutzung des eingebauten `TYPE_LIGHT`-Umgebungslichtsensors (Lux-Messung), um direkt am Standort zu prüfen, ob es für eine bestimmte Pflanze hell genug ist.

### ⚙️ App-Qualität & Komfort-Optimierungen
6. **Lokales Backup & Export/Import (ZIP/JSON)**:
   - Komplette Sammlung inkl. Fotos als ZIP-Archiv exportieren und auf einem neuen Gerät wieder einspielen – weiterhin 100 % ohne Cloud.
7. **Homescreen-Widgets (Jetpack Glance)**:
   - Ein kleines Widget für den Startbildschirm, das den heutigen Garten-Check oder die nächste Pflegeaufgabe anzeigt.
8. **Offline-Warteschlange für KI-Analysen**:
   - Wenn im Garten oder Gewächshaus kein Netz vorhanden ist, Fotos lokal vormerken und automatisch analysieren, sobald wieder WLAN/Mobilfunk besteht.
9. **Erweiterte Tests & Bildkompression**:
   - Automatisierte UI-Tests (Compose Testing) für die Hauptflows, Room-Migrations-Tests sowie EXIF-Orientierungskorrektur in `ImageHandler` für gedrehte Kamerafotos.
10. **Sprachnotizen & Schnell-Tagebuch**:
    - Kurze Pflege-Einträge mit Datum (z. B. *„Umgetopft“*, *„Neues Blatt entdeckt“*) statt eines einzelnen Fließtextfelds.
