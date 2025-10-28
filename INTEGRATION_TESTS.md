# Integration Tests Documentation

## Overview
This document describes the integration tests for QuizRepository and MediaRepository that fetch real data from Supabase.

## Test Location
```
app/src/androidTest/java/com/siagajiwa/siagajiwaid/integration/RepositoryIntegrationTest.kt
```

## Available Tests

### Quiz Repository Tests

#### 1. `testStressQuizIntegration()`
Fetches and displays stress quiz questions from Supabase `stress_quiz` table.

**Output includes:**
- Total number of questions
- Each question with:
  - Database ID
  - Question number
  - Page number
  - Order
  - Question text
  - All 4 answer options (A, B, C, D)
  - Correct answer marked with ✓
- Summary with pages and questions per page

#### 2. `testPatientQuizIntegration()`
Fetches and displays patient care quiz questions from Supabase `perawatan_quiz` table.

**Output includes:**
- Total number of questions
- Each question with:
  - Database ID
  - Question number
  - Page number
  - Order
  - Question text (with 0-4 rating scale)
- Summary with pages and questions per page

### Media Repository Tests

#### 3. `testStressMediaIntegration()`
Fetches and displays stress management media from Supabase `stress_media` table.

**Output includes:**
- Total media items
- Each media with:
  - Database ID
  - Order
  - YouTube/media link
  - Creation timestamp
- Link analysis (YouTube vs other links)

#### 4. `testPatientCareMediaIntegration()`
Fetches and displays patient care media from Supabase `perawatan_media` table.

**Output includes:**
- Total media items
- Media details and links
- Link analysis

#### 5. `testSchizophreniaMediaIntegration()`
Fetches and displays schizophrenia insight media from Supabase `skizo_media` table.

**Output includes:**
- Total media items
- Media details and links
- Link analysis

#### 6. `testAllRepositoriesComprehensive()`
Comprehensive test that fetches data from ALL sources and displays a grand summary.

**Output includes:**
- Status of all data sources (✅ success or ❌ failure)
- Total quiz questions across all quizzes
- Total media items across all media sources
- Grand total of all data items

## Running the Tests

### Method 1: Using Gradle (Without Device)
```bash
# Build the test APK
./gradlew :app:assembleDebugAndroidTest

# The tests require a device/emulator to run
# See Method 2 and 3 below
```

### Method 2: Using Gradle with Connected Device
```bash
# Make sure a device or emulator is running
adb devices

# Run all integration tests
./gradlew :app:connectedAndroidTest

# Run specific test
./gradlew :app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.siagajiwa.siagajiwaid.integration.RepositoryIntegrationTest#testStressQuizIntegration
```

### Method 3: Using Android Studio
1. Open the project in Android Studio
2. Navigate to: `app/src/androidTest/java/com/siagajiwa/siagajiwaid/integration/RepositoryIntegrationTest.kt`
3. Connect a device or start an emulator
4. Right-click on the test file or individual test method
5. Select "Run 'RepositoryIntegrationTest'" or "Run 'testStressQuizIntegration()'"
6. View output in the "Run" tab at the bottom of Android Studio

### Method 4: Using ADB (Manual APK Installation)
```bash
# Build the test APK
./gradlew :app:assembleDebugAndroidTest

# Install main app
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Install test APK
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

# Run tests via adb
adb shell am instrument -w -r \
  -e debug false \
  -e class com.siagajiwa.siagajiwaid.integration.RepositoryIntegrationTest \
  com.siagajiwa.siagajiwaid.test/androidx.test.runner.AndroidJUnitRunner
```

## Viewing Test Output

### In Android Studio
- Test results appear in the "Run" window
- Console output (with all the formatted data) appears in the Logcat window
- Filter Logcat by: `System.out` to see println statements

### In Terminal (Gradle)
```bash
# View test results
cat app/build/reports/androidTests/connected/index.html

# View logcat during test
adb logcat | grep "System.out"
```

## Example Output

### Stress Quiz Test Output:
```
============================================================
STRESS QUIZ INTEGRATION TEST - FETCHING FROM SUPABASE
============================================================

✅ SUCCESS: Fetched 20 stress quiz questions

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Question #1
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Database ID: 123e4567-e89b-12d3-a456-426614174000
Question Number: 1
Page: 1
Order: 1

Q: Bagaimana tidur anda semalam?

Answer Options:
   A. Nyenyak ✓ (CORRECT)
   B. Gelisah
   C. Sulit tidur
   D. Tidak bisa tidur sama sekali

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SUMMARY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Total Questions: 20
Pages: [1, 2, 3, 4, 5] (5 pages)
Questions per page:
  Page 1: 4 questions
  Page 2: 4 questions
  Page 3: 4 questions
  Page 4: 4 questions
  Page 5: 4 questions
============================================================
```

### Comprehensive Test Output:
```
============================================================
COMPREHENSIVE REPOSITORY INTEGRATION TEST
Testing all repositories and data sources
============================================================

📊 Fetching data from all sources...

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
RESULTS SUMMARY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📝 QUIZ DATA:
  ✅ Stress Quiz: 20 questions
  ✅ Patient Care Quiz: 15 questions

🎬 MEDIA DATA:
  ✅ Stress Media: 10 items
  ✅ Patient Care Media: 8 items
  ✅ Schizophrenia Media: 12 items

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
GRAND TOTAL
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 Total Quiz Questions: 35
🎬 Total Media Items: 30
📦 Total Data Items: 65
============================================================
```

## Requirements

### Prerequisites
- Android device or emulator connected
- Supabase credentials configured in `.env` file
- Network connection to Supabase
- Android SDK installed

### Dependencies
All dependencies are already configured in `app/build.gradle.kts`:
- Supabase Postgrest KT
- Kotlinx Coroutines
- AndroidX Test Runner
- JUnit 4

## Troubleshooting

### Issue: Tests fail with "No devices found"
**Solution:** Connect a physical device or start an Android emulator before running tests.

### Issue: Tests fail with connection errors
**Solution:**
1. Check `.env` file has correct Supabase credentials
2. Verify network connection
3. Check Supabase dashboard for service status

### Issue: Can't see println output
**Solution:**
- In Android Studio: Open Logcat and filter by "System.out"
- In Terminal: Use `adb logcat | grep "System.out"` while tests run

### Issue: Tests timeout
**Solution:** Increase timeout in test configuration or check network speed.

## CI/CD Integration

### GitHub Actions Example
```yaml
- name: Run Integration Tests
  run: ./gradlew connectedAndroidTest

- name: Upload Test Results
  uses: actions/upload-artifact@v2
  with:
    name: test-results
    path: app/build/reports/androidTests/
```

## Notes

- These are **instrumented tests** that require an Android runtime environment
- Tests use real Supabase data (not mocked)
- Each test is independent and can run in any order
- Tests are read-only and won't modify Supabase data
- Output is formatted for easy reading with emojis and separators

## Next Steps

To add more integration tests:
1. Add new test methods in `RepositoryIntegrationTest.kt`
2. Follow the same pattern: fetch data, fold result, print formatted output
3. Use `println()` for output (appears in Logcat/console)
4. Run and verify output format
