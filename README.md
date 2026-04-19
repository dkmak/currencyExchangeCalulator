# **Currency Exchange Calculator** - Darryl Mak

### Description:
1. **Convert between currencies** - Calculate conversions between USDc and a selected currency using live ask/bid market rates.
2. **Change quote currency** - Pick from supported currencies in a bottom sheet and update the calculator with the selected market.

https://github.com/user-attachments/assets/c99f6383-c52f-466e-90e6-2dc69df181c7

### Setup Instructions
#### Prerequisites
- Java/JDK Version: 11
- •Android SDK
    - Compile SDK: 36
    - Minimum SDK: 24
- Gradle Version: Android Gradle Plugin 8.13.1

#### Open & Run Project
1. Open Android Studio
2. Select "Open" and choose the root project directory
3. Let Gradle sync complete
4. Run the `app` configuration on an emulator or Android device
5. run unit tests with `./gradlew testDebugUnitTest`

## Technical Details

### Architecture
**MVVM** architecture
- Clear separation of concerns between UI, domain, presentation, and data layers

### UI
- **Jetpack Compose**
- Main exchange calculator screen with bid/ask-based conversion
- Bottom sheet currency picker
- Proper loading and error states
- User can set default preferred currency exchange rate.

### Networking
- Integrate with [DolarApp API](https://api.dolarapp.dev/)
- Handle network errors gracefully.
- Proper error messages for the user.


### Testing
You can run the current test suite with `./gradlew clean testDebugUnitTest`.

---

## API Reference
**Base URL:** `https://api.dolarapp.dev/`

| Endpoint                                   | Description | Example |
|--------------------------------------------|---------|--------|
| `GET /v1/tickers?currencies=MXN`           | Fetch ticker/book data for a selected currency | `https://api.dolarapp.dev/v1/tickers?currencies=MXN` |
| (UNAVAILABLE) `GET /v1/tickers-currencies` | Returns available currencies. Declared in the API interface, though the current client uses a hardcoded list | `https://api.dolarapp.dev/v1/tickers-currencies` |
