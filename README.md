# DeskBy9 Metro - Delhi Metro Journey Planning App

An Android application built with Kotlin and Jetpack Compose for planning metro journeys on the Delhi Metro network.

## Phase 1: Journey Planning (Current)

This phase focuses on pre-boarding trip planning:

- **Set Destination**: Enter target Metro Station
- **Set Origin**: 
  - Automatic: GPS-based nearest station detection
  - Manual: Select from station list
- **Route Calculation**: Display multiple route options (fastest, fewest transfers, shortest distance)
- **Route Selection**: Review and select the best route

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Location**: Google Play Services Location
- **Maps**: Google Maps SDK
- **Async**: Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/java/com/neelcortex/deskby9/metro/
├── data/
│   ├── location/          # Location services
│   ├── remote/            # API services and DTOs
│   └── repository/        # Repository implementations
├── di/                    # Dependency injection modules
├── domain/
│   ├── model/            # Domain models
│   └── repository/       # Repository interfaces
├── presentation/
│   └── journey/          # Journey planning UI
└── ui/theme/             # App theme and styling
```

## Setup

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Minimum Android version: 7.0 (API 24)

### Configuration

1. **Delhi Metro API**: Update the base URL in `AppModule.kt`:
   ```kotlin
   private const val BASE_URL = "YOUR_API_BASE_URL"
   ```

2. **Google Maps API Key**: Add your Maps API key to `local.properties`:
   ```
   MAPS_API_KEY=your_maps_api_key_here
   ```

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew installDebug
```

## Features

### Journey Planning Screen
- Origin selection with GPS auto-detection or manual selection
- Destination selection with search functionality
- Multiple route options display
- Detailed route view with station timeline
- Transfer point highlighting
- Fare calculation

### Permissions
- `ACCESS_FINE_LOCATION`: For GPS-based origin detection
- `ACCESS_COARSE_LOCATION`: For network-based location
- `INTERNET`: For API calls

## API Integration

The app expects a REST API with the following endpoints:

- `GET /stations` - Get all stations
- `GET /stations/search?q={query}` - Search stations
- `GET /stations/{id}` - Get station by ID
- `GET /stations/nearest?lat={lat}&lng={lng}` - Find nearest station
- `GET /routes?origin={id}&destination={id}` - Calculate routes
- `GET /routes/{id}` - Get route details

## Future Phases

- Phase 2: Live Journey Tracking
- Phase 3: Notifications and Alerts
- Phase 4: Offline Support

## License

Apache License 2.0
