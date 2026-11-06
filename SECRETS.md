# Secrets für RömerPlant

Diese App braucht zwei Arten von Secrets:

## 1. Mistral API-Key (für die App selbst)

Die KI-Analyse läuft über die Mistral-API. Der Key gehört **nicht** ins Repo und nicht in GitHub –
er wird direkt in der App unter **Einstellungen → Mistral API-Key** hinterlegt und nur lokal
im DataStore des Geräts gespeichert.

Erstellung: [console.mistral.ai/api-keys](https://console.mistral.ai/api-keys) → „Create new key“.

## 2. Signierungs-Keystore (für Release-APKs)

Damit GitHub Actions signierte Release-APKs bauen kann, werden vier GitHub-Repo-Secrets
benötigt (Settings → Secrets and variables → Actions → „New repository secret“).

### Schritt 1: Keystore lokal erzeugen (einmalig, auf deinem Rechner)

```bash
keytool -genkeypair -v \
  -keystore roemerplant.keystore \
  -alias roemerplant \
  -keyalg RSA -keysize 2048 -validity 10000
```

`keytool` ist im JDK enthalten (z. B. mit Android Studio installiert).
Beim Ausführen wirst du nach einem Keystore-Passwort, einem Key-Passwort und
Namen/Ort gefragt – notiere beides.

> **Wichtig:** `roemerplant.keystore` niemals committen. Er liegt idealerweise
> außerhalb des Repos, und du solltest eine Sicherungskopie verwahren –
> ohne diesen Keystore können zukünftige Updates nicht mehr als „gleiche App“
> installiert werden (Android verlangt dieselbe Signatur).

### Schritt 2: Keystore als Base64 exportieren

```bash
base64 -w0 roemerplant.keystore > keystore.b64      # Linux
base64 -i roemerplant.keystore -o keystore.b64       # macOS
```

### Schritt 3: GitHub-Secrets setzen

```bash
gh secret set ANDROID_KEYSTORE_BASE64 < keystore.b64
gh secret set KEYSTORE_PASSWORD   # Keystore-Passwort (interaktiv eingeben)
gh secret set KEY_ALIAS           # z. B. roemerplant
gh secret set KEY_PASSWORD        # Key-Passwort (interaktiv eingeben)
```

Danach baut der Release-Workflow ([.github/workflows/release.yml](.github/workflows/release.yml))
bei jedem `v*`-Tag automatisch eine **signierte Release-APK** und hängt sie an das
GitHub-Release. Ohne diese Secrets wird ersatzweise eine Debug-APK gebaut.

### Lokaler Release-Build (Alternativ zu CI)

```bash
export KEYSTORE_FILE=$HOME/roemerplant.keystore
export KEYSTORE_PASSWORD=...
export KEY_ALIAS=roemerplant
export KEY_PASSWORD=...
./gradlew assembleRelease
```
