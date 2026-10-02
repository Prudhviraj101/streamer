<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-Application-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/TMDB-API-01B4E4?style=for-the-badge" />
  <img src="https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" />
</p>

<h1 align="center">🎬 Streamer</h1>

<p align="center">
  <strong>A modern Android movie discovery and streaming client built with Kotlin and Jetpack Compose.</strong>
</p>

<p align="center">
  Discover movies, explore details, save data locally, and play supported media through a clean Compose-first experience.
</p>

---

## ✨ Highlights

- 🎬 **TMDB-powered discovery** for movies and metadata
- 🎨 **Jetpack Compose + Material 3** modern UI
- 🌐 **Retrofit + OkHttp + Kotlin Serialization** for networking
- 🖼️ **Coil** for efficient image loading
- ▶️ **Media3 / ExoPlayer** for media playback
- 🗄️ **Room** for local persistence
- 💉 **Hilt** for dependency injection
- 💾 **DataStore** for preferences and lightweight state
- ⚡ **Kotlin Coroutines** for asynchronous operations
- 🔐 Local API-key configuration with no hardcoded credential fallback
- 🤖 **GitHub Actions CI** for automated Android builds

## 🧠 Architecture

The app follows a layered Android architecture that keeps UI, state, networking, and persistence separated.

```
┌──────────────────────────────┐
│        Jetpack Compose       │
│       UI + Material 3        │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│          ViewModel           │
│       UI state + logic       │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│         Repository           │
│   Single source of data      │
└──────────┬───────────┬───────┘
           ↓           ↓
     ┌──────────┐ ┌──────────┐
     │ TMDB API │ │ Room /   │
     │ Retrofit │ │ DataStore│
     └──────────┘ └──────────┘
           │
           ↓
     ┌──────────────┐
     │ Media3 /     │
     │ ExoPlayer    │
     └──────────────┘
```

## 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | ViewModel, Repository pattern |
| Networking | Retrofit, OkHttp, Kotlin Serialization |
| Images | Coil |
| Playback | Android Media3 / ExoPlayer |
| Local data | Room, DataStore |
| DI | Hilt |
| Async | Kotlin Coroutines |
| CI | GitHub Actions |
| Data | TMDB API |

## 🚀 Getting Started

### Requirements

- Android Studio with a recent Android SDK
- JDK 17
- Android emulator or physical device
- TMDB API key

### 1. Clone

```bash
git clone https://github.com/Prudhviraj101/streamer.git
cd streamer
```

### 2. Configure TMDB

Create `local.properties` in the project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
```

Keep `local.properties` out of Git. It is intended for local development only.

### 3. Build and run

Open the project in Android Studio, sync Gradle, then run the `app` configuration.

Or build from the terminal:

```bash
./gradlew assembleDebug
```

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/                 # Kotlin source
│   ├── res/                  # Android resources
│   └── AndroidManifest.xml
└── build.gradle.kts

.github/
└── workflows/
    └── android.yml           # Android CI
```

## 🔐 Security

- API credentials are loaded locally through `local.properties`.
- Do not commit API keys, signing credentials, or generated build files.
- If an old credential was ever exposed in Git history, revoke/rotate it before using the project publicly.

## 🧪 Continuous Integration

Every push and pull request targeting `main` runs an automated debug APK build through GitHub Actions.

This helps catch Gradle, dependency, and compilation problems before changes are merged.

## 🗺️ Roadmap

- [ ] Improve offline-first caching
- [ ] Add richer watchlist features
- [ ] Expand playback/error handling
- [ ] Add automated Android tests
- [ ] Improve accessibility and tablet layouts

## 📌 Project Status

This is an actively evolving portfolio project focused on modern Android development, clean architecture, and API-driven media experiences.

## 📄 License

See the repository for the current licensing information.
