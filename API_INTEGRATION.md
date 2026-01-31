# Delhi Metro API Integration

## API Overview

The DeskBy9 Metro app integrates with the official Delhi Metro Rail Corporation (DMRC) API to provide real-time journey planning features.

**Base URL:** `https://backend.delhimetrorail.com/api/v2/en/`

## Available Endpoints

### 1. Get All Stations
```
GET /station_list
```

Returns a list of all Delhi Metro stations with their codes, names, and facilities.

**Response:**
```json
[
  {
    "id": 52,
    "station_name": "ADARSH NAGAR",
    "station_code": "AHNR",
    "station_facility": [...]
  }
]
```

### 2. Get All Lines
```
GET /line_list
```

Returns information about all metro lines including colors, codes, and status.

**Response:**
```json
[
  {
    "id": 1,
    "name": "Line 1",
    "line_color": "Red Line",
    "line_code": "LN1",
    "primary_color_code": "#c0282c",
    "start_station": "RITHALA",
    "end_station": "SHAHEED STHAL ( NEW BUS ADDA)",
    "status": "Normal Service"
  }
]
```

### 3. Calculate Route
```
GET /station_route/{from_code}/{to_code}/{filter}/{timestamp}
```

Calculates the route between two stations.

**Parameters:**
- `from_code`: Origin station code (e.g., "NDI")
- `to_code`: Destination station code (e.g., "HCC")
- `filter`: Route optimization
  - `least-distance`: Shortest physical distance
  - `minimum-interchange`: Fewest line changes
- `timestamp`: Current time in ISO format (e.g., "2026-01-31T13:47:28.596")

**Example:**
```
GET /station_route/NDI/HCC/least-distance/2026-01-31T13:47:28.596
```

**Response:**
```json
{
  "stations": 23,
  "from": "NEW DELHI (Yellow & Airport Line)",
  "to": "MILLENNIUM CITY CENTRE GURUGRAM",
  "total_time": "0:52:30",
  "fare": 54,
  "route": [
    {
      "line": "Yellow Line",
      "line_no": 2,
      "path": [
        {"name": "NEW DELHI (Yellow & Airport Line)", "status": ""},
        {"name": "RAJIV CHOWK", "status": ""},
        ...
      ]
    }
  ]
}
```

## Implementation Details

### Data Flow

1. **Station List**: Fetched once and cached in `MetroRepositoryImpl`
2. **Station Search**: Performed locally on cached station list
3. **Nearest Station**: Calculated using Haversine formula on cached stations with coordinates
4. **Route Calculation**: Calls API with both filters (`least-distance` and `minimum-interchange`) to provide multiple route options

### Key Components

#### DTOs (`data/remote/dto/`)
- `StationDto`: Maps to API station response
- `LineDto`: Maps to API line response
- `RouteResponseDto`: Maps to API route calculation response
- `RouteLineDto`, `RoutePathStationDto`: Nested route structures

#### Mappers (`data/remote/dto/Mappers.kt`)
- `StationDto.toDomain()`: Converts API station to domain model
- `RouteResponseDto.toDomain()`: Converts API route to domain model with segments

#### Repository (`data/repository/MetroRepositoryImpl.kt`)
- Implements caching for station data
- Provides local search functionality
- Calculates nearest station using Haversine formula
- Fetches both route types (least-distance and minimum-interchange)

### Station Codes

Station codes are used as identifiers for route calculation. Examples:
- `NDI` - New Delhi
- `HCC` - Millennium City Centre Gurugram (Huda City Centre)
- `AHNR` - Adarsh Nagar
- `AIIMS` - AIIMS

The complete list is available via the `/station_list` endpoint.

## Testing

### Test API Endpoints

```bash
# Get all stations
curl 'https://backend.delhimetrorail.com/api/v2/en/station_list'

# Get all lines
curl 'https://backend.delhimetrorail.com/api/v2/en/line_list'

# Calculate route (New Delhi to Huda City Centre)
curl 'https://backend.delhimetrorail.com/api/v2/en/station_route/NDI/HCC/least-distance/2026-01-31T13:47:28.596'

# Calculate route with minimum interchanges
curl 'https://backend.delhimetrorail.com/api/v2/en/station_route/NDI/HCC/minimum-interchange/2026-01-31T13:47:28.596'
```

### Build and Run

```bash
# Build the project
./gradlew build

# Install on device/emulator
./gradlew installDebug

# Run the app
./gradlew run
```

## Features Implemented

### Phase 1: Journey Planning

- ✅ **Set Destination**: Search and select destination station
- ✅ **Set Origin**: 
  - Automatic GPS-based nearest station detection
  - Manual station selection
- ✅ **Route Calculation**: Multiple route options
  - Least distance route
  - Minimum interchange route
- ✅ **Route Display**: Detailed route information with:
  - Total duration
  - Number of stations
  - Transfer points
  - Fare calculation

## Notes

- The API does not require authentication
- Station coordinates may not be available for all stations in the `/station_list` endpoint
- The app caches station data to minimize API calls
- Route calculation requires valid station codes (not station names)
- Timestamp parameter is required for route calculation but doesn't affect the result

## Future Enhancements

- Add station coordinates from external source for better GPS accuracy
- Implement route caching for faster repeated queries
- Add first/last train timings (if API provides this data)
- Support for real-time service status updates
