# SatelliteTracker

SatelliteTracker is a sample satellite tracking application built with **Kotlin** and **Jetpack Compose**. It loads satellite data from bundled JSON assets, allowing users to browse a searchable list and follow detailed position updates.

<p align="center">
<img src="/photos/ss1.png" width="30%"/>
<img src="/photos/ss2.png" width="30%"/>
</p>

## ⬇️ Download
- **Latest debug APK**: [Download](https://github.com/thebsk/SatelliteTracker/releases/download/1.0/app-debug.apk)
  
## Features
- Searchable list of satellites with active status indicators.
- Detailed information view including first flight, size, and cost per launch.
- Position tracking with periodic updates.
- Offline-first data layer using JSON assets and caching with **Room**.
- Reactive UI entirely written in **Jetpack Compose** and **Material 3**.
- **MVI** architecture, repository pattern, and use-case driven domain layer.
- Dependency injection with **Dagger Hilt**.
- Kotlin **Coroutines & Flow** for asynchronous data streams.
- JSON parsing via **Kotlinx Serialization**.

## Tech Stack
| Layer | Technologies |
|-------|--------------|
| UI | Jetpack Compose, Material 3 |
| DI | Dagger Hilt |
| Async | Coroutines, Flow |
| Storage | Room, JSON Assets, Kotlinx Serialization |
| Navigation | Compose Navigation |
| Testing | JUnit4, Compose UI Test |

## Architecture
The project follows a clean MVI structure:
```
Compose UI -> ViewModel -> Use Cases -> Repository -> Local Data (Assets/Room)
```
Why **MVI**?

This project uses **MVI** instead of MVVM because it fits best with **Jetpack Compose**. With a **unidirectional data flow**, the UI always reflects a single source of truth, side effects are isolated, and the state is deterministic and easy to test. **Jetpack Compose’s state-driven model aligns naturally with this approach**, keeping the satellite list, search, and position updates perfectly in sync.

## Data
Satellite information is stored in JSON files within the app’s assets:
```
app/src/main/assets/satellites.json
app/src/main/assets/satellite-detail.json
app/src/main/assets/positions.json
```

## Getting Started
1. Clone the repository.
2. Open in Android Studio (Meerkat+ recommended).
3. Run `./gradlew build` or launch the app from Android Studio.

## Testing
Run unit tests:

```bash
./gradlew test
```

Run UI tests (requires an emulator or connected device):

```bash
./gradlew connectedAndroidTest
```

---
Happy tracking! 🛰️
