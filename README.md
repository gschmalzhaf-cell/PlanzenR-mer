# RömerPlant

Private Android-App zur Verwaltung deiner Pflanzen – mit Foto- oder manueller Erfassung,
KI-gestützter Bestimmung, Pflegeanweisungen und Gesundheitsanalyse über die Mistral-API.

## Funktionen

- **Pflanzen erfassen**: per Foto (Photo-Picker) oder komplett manuell
- **Mistral-KI-Analyse** (Modell `pixtral-12b-latest`):
  - Bestimmung der Pflanzenart (mit deutscher Beschreibung)
  - Pflegeanweisungen: Licht, Gießen, Luftfeuchtigkeit, Temperatur, Düngen, Substrat
  - Gesundheitsanalyse mit konkreten Handlungsanweisungen
- **Alles manuell überschreibbar**: alle KI-Felder sind in der Detailansicht editierbar
- **Gesundheit neu analysieren**: aktuelles Foto erneut prüfen lassen
- Alle Daten lokal auf dem Gerät (Room-Datenbank, keine Cloud)

## Technik (State of the Art)

- Kotlin 2.x, Jetpack Compose, Material 3 (dynamisches Dark/Light Theme)
- MVVM + Unidirectional Data Flow (`StateFlow`-basierte ViewModels)
- Hilt (Dependency Injection), Room (Persistenz), DataStore (Einstellungen)
- Retrofit + kotlinx.serialization, strukturierte KI-Antworten via JSON-Schema
- Coil für Bildanzeige, Navigation Compose, moderne Photo-Picker-API

## Setup

1. Projekt in **Android Studio** (Ladybug oder neuer) öffnen und Gradle-Sync ausführen.
2. App auf dem Gerät starten (minSdk 26).
3. In der App: **Einstellungen → Mistral API-Key** hinterlegen
   (erhältlich unter [console.mistral.ai/api-keys](https://console.mistral.ai/api-keys)).
   Der Key wird ausschließlich lokal im DataStore gespeichert.

## Nutzung

1. **+** antippen → Foto der Pflanze auswählen.
2. „Mit Mistral-KI analysieren" → Art, Beschreibung, Pflege und Gesundheit werden gefüllt.
3. Felder bei Bedarf manuell korrigieren und **Speichern**.
4. In der Detailansicht jederzeit bearbeiten oder die Gesundheit neu analysieren lassen.

## Build

```bash
./gradlew assembleDebug     # Debug-APK: app/build/outputs/apk/debug/
./gradlew assembleRelease   # Release (Signierung konfigurieren)
```

## Hinweis

Die App ist für private Nutzung gedacht; es gibt keine Nutzerkonten, keine Cloud und
keine Telemetrie. Die KI-Analyse funktioniert nur mit gültigem Mistral-API-Key.
