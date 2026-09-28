# 🛠️ Pipe Connect Board

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?style=flat&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-orange.svg)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-37-brightgreen.svg)](https://developer.android.com)

**Pipe Connect Board** is a modern, industrial-themed pipe-routing puzzle game built natively for Android using **Jetpack Compose**, **StateFlow**, and modern Android architecture principles. Players manipulate modular pipe tiles across a 6×6 grid to construct an uninterrupted fluid conduit connecting the Source inlet to the Destination outlet.

---

## 🌟 Key Features

* **🧩 Strategic 6×6 Grid Puzzle**:
  * Fixed **Source Inlet** at `(2, 0)` and **Destination Outlet** at `(3, 5)`.
  * Non-interactive structural **Blocked Cells** adding tactical complexity to path planning.
* **🔁 Interactive Tray & Tile Queue System**:
  * 3-slot active tile tray with a deterministic bag queue of 18 handcrafted tiles: *Straight*, *Elbow*, *T-Junction*, and *Cross* pipes.
  * 90° clockwise tile rotation, placement validation, and step-by-step **Undo** functionality.
* **⚡ Real-Time BFS Connectivity Engine**:
  * Fast Breadth-First Search (`ConnectivitySolver`) continuously validates directional pipe connections.
  * Visual fluid flow highlighting illuminated pipes as soon as a valid connection to the Source is established.
* **🎨 Custom Canvas Rendering**:
  * High-performance 2D drawing via Jetpack Compose `Canvas`.
  * Detailed pipe geometries with flanges, joint rivets, flow animations, and distinct industrial textures.
* **🔊 Procedural Audio & Haptic Feedback**:
  * Built-in low-latency procedural audio synthesizer using Android `AudioTrack` (zero heavy audio asset dependencies).
  * Tactile feedback engine (`HapticManager`) providing mechanical vibration responses on rotation, placement, and pipeline completion.
* **🎨 Themes & Customization**:
  * Supports Light and Dark Material 3 application themes.
  * Custom **Board Finish Themes**: *Classic Industrial Steel* & *Midnight Stealth*.
  * Configurable HUD settings: toggle timer, sound effects, haptic vibration, and connected flow highlights.
* **📊 Persistent Statistics**:
  * Automatically tracks total games played, completed pipelines, best move count, and fastest time using **Preferences DataStore**.

---

## 🎮 Gameplay Mechanics & Rules

1. **Select & Rotate**: Pick one of 3 available pipe pieces from your tray. Tap **ROTATE** to orient the tile openings.
2. **Place**: Tap an empty cell on the 6×6 board to place the selected tile.
3. **Directional Flow**: Neighboring openings must align (e.g., a **RIGHT** opening connects to a **LEFT** opening).
4. **Win Condition**: Create an unbroken connection from the Source inlet to the Destination outlet before running out of tiles.
5. **Tile Supply**: You have a total inventory of 18 tiles. Plan efficiently to avoid tile exhaustion!

---

## 🏗️ Architecture & Project Structure

The project follows the official Android Architecture Guidelines using **MVVM**, **Clean Code principles**, and unidirectional data flow (UDF) via Kotlin Coroutines `StateFlow`.

```
com.example.pipeconnectboard
├── audio/            # Procedural audio engine (SoundPlayer using AudioTrack)
├── data/             # Preferences DataStore & repository for user settings & stats
├── haptics/          # Vibration & haptic feedback controller (HapticManager)
├── logic/            # Game logic engine & algorithms
│   ├── ConnectivitySolver.kt   # BFS connectivity and flow traversal solver
│   └── PuzzleFactory.kt        # Grid specification & deterministic tile supply
├── model/            # Data models (GridPosition, PipeCell, PipeTile, Direction, etc.)
├── ui/               # Jetpack Compose UI layer
│   ├── components/   # Modular UI widgets (BoardView, PipeCanvas, TrayView, GameHud, etc.)
│   ├── navigation/   # Jetpack Navigation Compose host & route definitions
│   ├── screens/      # Application screens (HomeScreen, GameScreen, SettingsScreen, etc.)
│   └── theme/        # Industrial M3 color palette, typography, and board themes
└── viewmodel/        # GameViewModel managing state, timer, actions & undo stack
```

---

## 🛠️ Tech Stack & Dependencies

* **Language**: 100% Kotlin
* **UI**: Jetpack Compose (Material 3)
* **Architecture**: Android ViewModel + Coroutines StateFlow
* **Navigation**: Jetpack Navigation Compose
* **Data Persistence**: AndroidX DataStore Preferences
* **Graphics**: Jetpack Compose `Canvas` 2D rendering
* **Audio**: Native Android `AudioTrack` procedural audio generation
* **Build System**: Gradle Kotlin DSL (`build.gradle.kts`), Version Catalog (`libs.versions.toml`)
* **Target SDK**: Android 15 (API 37) | **Min SDK**: Android 7.0 (API 24)

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio**: Ladybug / Hedgehog or newer (2024.1+)
* **JDK**: Java 11 or 17
* **Android SDK**: API 37 installed

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/Pipe-Connect.git
   cd Pipe-Connect
   ```

2. **Open in Android Studio**:
   Open Android Studio and select **Open** -> Navigate to the cloned folder.

3. **Build the Project**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

5. **Run on Device / Emulator**:
   Select an Android virtual device or connected hardware device running Android 7.0 (API 24) or higher and click **Run** (`Shift + F10`).

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

<p align="center">Made with ❤️ using Jetpack Compose & Kotlin</p>
