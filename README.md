# SmartRemote Pro 📱⚡

**SmartRemote Pro** is an Android Universal Remote Control application built with **Kotlin, Jetpack Compose (Material 3), Room Database, Clean Architecture, and Coroutines/Flow**.

---

## 🌟 Key Features

### 1. Hardware IR Blaster & Protocol Engine
- Direct hardware control via Android's `ConsumerIrManager`
- Custom protocol encoders for **NEC (38kHz), Sony SIRC (40kHz), and Philips RC5 (36kHz)**
- **500+ Device Pre-loaded Database**:
  - **TVs**: Samsung, LG, Sony, Xiaomi, OnePlus, Panasonic, TCL, Philips
  - **ACs**: Daikin, Voltas, Hitachi, Samsung, LG, Blue Star, Carrier, Lloyd
  - **Set-Top Boxes**: Airtel Xstream, Tata Play (Sky), Dish TV, Sun Direct
  - **Soundbars**: JBL, Bose, Sony, Philips, Samsung
  - **DVD/Blu-ray**: Sony, LG, Samsung, Pioneer
- **IR Learning Mode**: Point physical remotes at the phone to record custom IR hex pulses.

### 2. WiFi & Network Control
- **Zero-Config Discovery**: Multicast SSDP (UPnP) and mDNS scanning.
- **Smart TV Protocols**: WebSocket control for LG webOS (`ssap://`) and Samsung Tizen (`ms.remote.control`), HTTP REST for Roku and Android TV.
- **Deep Streaming App Launchers**: One-tap direct launches for **YouTube, Netflix, Prime Video, Disney+ Hotstar**.
- **UPnP / DLNA Controller**: Play, Pause, Stop, and volume control over local network renderers.

### 3. Bilingual Voice Control (No Cloud AI Required)
- Uses Android's native `SpeechRecognizer` API with zero external dependencies.
- Custom rule-based NLP parser supporting **English & Hindi** (transliterated and Devanagari):
  - English: *"Turn on TV"*, *"Volume up"*, *"Set volume to 25"*, *"Channel 100"*, *"Open YouTube"*
  - Hindi: *"TV chalu karo"*, *"Awaaz badao"*, *"Volume kam karo"*, *"YouTube kholo"*, *"AC band karo"*
- **Live Audio Waveform Animation**: Dynamic visualizer that pulses to speech volume (RMS dB).
- **Spoken Confirmations**: Integrated bilingual Android `TextToSpeech`.

### 4. Motion & Touch Gestures
- **Shake Phone**: Accelerometer sensor triggers immediate Pause / Play toggle.
- **Directional Swipes**: Swipe Up/Down for Volume, Left/Right for Channels.
- **Circle Gesture**: Circular touch gestures invoke the quick action menu.
- **Point Phone at TV**: Compass azimuth detector auto-connects to aligned room displays.

### 5. Airtel Xstream Box / DTH Remote
- Dedicated Airtel DTH remote layout with Color keys (Red, Green, Yellow, Blue).
- **Channel Guide (EPG)**: Entertainment, Sports, Movies, News, Kids, Regional categories with channel numbers and logos.
- **Favorites & Recents**: Pin favorite channels and track recently watched history.
- **Account & Recharge Alerts**: WorkManager scheduled alerts before pack expiry date with balance tracking.

### 6. Automation & Multi-Step Macros
- **Pre-made Modes**:
  - *Movie Mode*: TV On + Netflix + Volume 25 + AC 24°C
  - *Gaming Mode*: TV On + HDMI 1 + Soundbar On + Bass Boost
  - *Night Mode*: All devices off + Night light
- **Custom Macro Creator**: Step-by-step sequential device actions with millisecond delay intervals.
- **NFC Sticker Integration**: Tap physical NFC tags to trigger automated macros.

### 7. Multi-Device & Family Profile System
- **Free vs. Premium Tiers**: Free tier supports 3 devices and 5 daily voice commands; Premium unlocks unlimited devices, zero ads, and multi-phone sync.
- **Family Profiles**:
  - *Admin*: Full control with PIN security lock.
  - *Parent & Child*: Restricted channels, time limits, locked settings.
  - *Elder Mode*: Simplified, high-contrast UI with 84dp enlarged buttons and strong haptics.

### 8. System Integrations
- **Floating Mini-Remote**: Draggable overlay bubble (`SYSTEM_ALERT_WINDOW`) accessible on top of Netflix or YouTube.
- **Notification Shade Remote**: Ongoing Foreground Service with Power, Volume, and Mute buttons.
- **Android Quick Settings Tile**: 1-tap power toggle from system notification drawer.
- **Home Screen App Widgets**: Small (2x1), Medium (4x2), and Large (4x3) widgets.
- **Find My Remote**: Loud alarm tone and flasher to locate a misplaced phone in the room.

---

## 🏗 Architecture & Technologies

- **Architecture**: MVVM + Clean Architecture + Repository Pattern
- **UI Framework**: Jetpack Compose + Material Design 3
- **Local Storage**: Room Database 2.6 (`devices`, `macros`, `macro_steps`, `schedules`, `channels`, `profiles`)
- **Security**: EncryptedSharedPreferences (AES-256 GCM)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Background Tasks**: Android WorkManager & Foreground Services
- **Hardware APIs**: `ConsumerIrManager`, `SpeechRecognizer`, `SensorManager`, `NfcAdapter`, `WifiManager`, `Vibrator`

---

## 🚀 Building & Running

1. Open project in **Android Studio Hedgehog / Iguana / Jellyfish** (JDK 17+).
2. Sync Gradle dependencies:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run Unit Tests:
   ```bash
   ./gradlew test
   ```
4. Deploy to an Android device (Android 7.0+ / API 24+).
