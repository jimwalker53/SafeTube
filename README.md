# SafeTube

A controlled YouTube viewing experience for Android, designed for caregivers managing content exposure for family members who need assistance.

## Features

- **YouTube Browsing**: Browse trending videos, search for content, and manage subscriptions
- **Content Filtering**:
  - Block specific search terms
  - Hide videos with blocked keywords in titles
  - Block entire channels
- **PIN Protection**: Secure access to settings with a PIN
- **Google Sign-In**: Access personalized subscriptions and recommendations
- **Tablet Optimized**: Responsive grid layout for tablets and phones

## Architecture

The app follows Clean Architecture with MVVM pattern:

```
app/
├── data/           # Data layer (repositories, API, database)
│   ├── local/      # Room database, preferences
│   ├── remote/     # Retrofit API services, DTOs
│   └── repository/ # Repository implementations
├── domain/         # Business logic
│   ├── filter/     # Content filtering engine
│   ├── model/      # Domain models
│   └── usecase/    # Use cases
├── ui/             # Presentation layer
│   ├── components/ # Reusable Compose components
│   ├── navigation/ # Navigation graph
│   ├── screens/    # Feature screens
│   └── theme/      # Material 3 theming
└── di/             # Hilt dependency injection
```

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp + Moshi
- **Database**: Room
- **Authentication**: Google Sign-In SDK
- **Video Playback**: android-youtube-player
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines + Flow

## Setup

### Prerequisites

1. Android Studio Hedgehog (2023.1.1) or newer
2. JDK 17
3. Android SDK 34

### Configuration

1. Copy `local.properties.example` to `local.properties`
2. Set up Google Cloud Console:
   - Create a new project
   - Enable YouTube Data API v3
   - Create an API Key
   - Create OAuth 2.0 Client ID (Android)
3. Add your credentials to `local.properties`:
   ```properties
   youtube.api.key=YOUR_API_KEY
   google.client.id=YOUR_CLIENT_ID.apps.googleusercontent.com
   ```

### Build

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing configuration)
./gradlew assembleRelease
```

## Content Filtering

### Blocked Search Terms
- Searches matching blocked terms return "No videos found"
- Case-insensitive partial matching
- Managed in Settings > Blocked Search Terms

### Blocked Keywords
- Videos with matching title keywords are hidden
- Three match types:
  - **Exact Word**: Matches complete words only
  - **Contains**: Matches if keyword appears anywhere
  - **Starts With**: Matches words starting with keyword
- Managed in Settings > Blocked Title Keywords

### Blocked Channels
- All videos from blocked channels are hidden
- Block channels directly from the video player
- Managed in Settings > Blocked Channels

## PIN Protection

- 4-6 digit PIN protects settings access
- Access via long-press on the app logo (3 seconds)
- Lockout after failed attempts:
  - 3 attempts: 30 second lockout
  - 10 attempts: 5 minute lockout

## Sideloading

Since this app is not distributed via Play Store:

1. Build the release APK
2. Transfer to the target device
3. Enable "Install unknown apps" in device settings
4. Install the APK

Consider disabling/uninstalling the stock YouTube app and blocking youtube.com in the browser for complete control.

## License

Private use only. Not for distribution.
