# Streamer

A modern Android movie-streaming client built with Kotlin and Jetpack Compose.

## Highlights

- 🎬 Movie discovery powered by **TMDB**
- 🎨 **Jetpack Compose + Material 3** UI
- 🌐 **Retrofit + Kotlin Serialization + OkHttp**
- 🖼️ **Coil** image loading
- ▶️ **Media3 / ExoPlayer** playback
- 🗄️ **Room** local persistence
- 💉 **Hilt** dependency injection
- 💾 **DataStore** preferences
- ⚡ Kotlin Coroutines for asynchronous work

## Architecture

```
Compose UI
   ↓
ViewModel
   ↓
Repository
 ↙     ↘
TMDB API  Room / DataStore
   ↓
Media3 / ExoPlayer
```

## Getting Started

### Requirements

- Android Studio with a recent Android SDK
- JDK 11+
- A TMDB API key

### Configure TMDB

Create or edit `local.properties` in the project root:

```properties
TMDB_API_KEY=your_tmdb_api_key
```

The project reads the key locally during the build. Do not commit `local.properties`.

### Run

1. Clone the repository.
2. Add `TMDB_API_KEY` to `local.properties`.
3. Open the project in Android Studio.
4. Sync Gradle.
5. Run the `app` configuration on an emulator or device.

## Security

The repository no longer contains a hardcoded TMDB API-key fallback. If a credential was previously exposed in Git history, revoke or rotate it with TMDB and use the local configuration above.

## Project Structure

```
app/
├── src/main/
│   ├── java/
│   ├── res/
│   └── AndroidManifest.xml
└── build.gradle.kts
```
