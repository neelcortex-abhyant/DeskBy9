# Delhi Metro API - Quick Reference

## Base URL
```
https://backend.delhimetrorail.com/api/v2/en/
```

## Common Station Codes

| Station Name | Code |
|--------------|------|
| New Delhi | NDI |
| Rajiv Chowk | RJC |
| Kashmere Gate | KSG |
| Huda City Centre (Millennium City Centre Gurugram) | HCC |
| Noida City Centre | NCC |
| Dwarka Sector 21 | DS21 |
| Vaishali | VSH |
| AIIMS | AIIMS |
| Hauz Khas | HKS |
| Connaught Place | CP |

## Quick API Calls

### Get All Stations
```bash
curl 'https://backend.delhimetrorail.com/api/v2/en/station_list'
```

### Get All Lines
```bash
curl 'https://backend.delhimetrorail.com/api/v2/en/line_list'
```

### Calculate Route (Shortest Distance)
```bash
curl 'https://backend.delhimetrorail.com/api/v2/en/station_route/NDI/HCC/least-distance/2026-01-31T14:00:00.000'
```

### Calculate Route (Minimum Interchanges)
```bash
curl 'https://backend.delhimetrorail.com/api/v2/en/station_route/NDI/HCC/minimum-interchange/2026-01-31T14:00:00.000'
```

## Response Examples

### Station List Response
```json
{
  "id": 52,
  "station_name": "ADARSH NAGAR",
  "station_code": "AHNR",
  "station_facility": [...]
}
```

### Route Response
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
        {"name": "RAJIV CHOWK", "status": ""}
      ]
    }
  ]
}
```

## Usage in Android App

### 1. Get Stations
```kotlin
val stations = apiService.getStations()
// Returns: List<StationDto>
```

### 2. Calculate Route
```kotlin
val route = apiService.calculateRoute(
    fromStationCode = "NDI",
    toStationCode = "HCC",
    filter = "least-distance", // or "minimum-interchange"
    timestamp = Instant.now().toString()
)
// Returns: RouteResponseDto
```

### 3. Convert to Domain Model
```kotlin
val domainRoute = route.toDomain(RouteType.SHORTEST_DISTANCE)
// Returns: Route (domain model)
```

## Filters

| Filter | Description | Use Case |
|--------|-------------|----------|
| `least-distance` | Shortest physical route | Minimize travel distance |
| `minimum-interchange` | Fewest line changes | Avoid transfers |

## Notes

- ✅ No API key required
- ✅ No rate limiting (as of now)
- ✅ CORS enabled for web requests
- ⚠️ Timestamp format: ISO 8601 (e.g., "2026-01-31T14:00:00.000")
- ⚠️ Use station codes, not station names, for route calculation
