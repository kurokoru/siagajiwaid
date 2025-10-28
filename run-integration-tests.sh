#!/bin/bash

# Integration Test Runner Script
# This script builds and runs integration tests for QuizRepository and MediaRepository

set -e

echo "════════════════════════════════════════════════════════════"
echo "  SIAGA JIWA - INTEGRATION TEST RUNNER"
echo "════════════════════════════════════════════════════════════"
echo ""

# Check if device is connected
echo "📱 Checking for connected devices..."
DEVICES=$(adb devices | grep -v "List" | grep "device$" | wc -l)

if [ "$DEVICES" -eq 0 ]; then
    echo "❌ No Android device or emulator found!"
    echo ""
    echo "Please connect a device or start an emulator, then try again."
    echo ""
    echo "To start an emulator:"
    echo "  emulator -avd <emulator-name> &"
    echo ""
    echo "To list available emulators:"
    echo "  emulator -list-avds"
    exit 1
fi

echo "✅ Found $DEVICES device(s) connected"
echo ""

# Build test APKs
echo "🔨 Building test APKs..."
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful"
echo ""

# Show menu
echo "════════════════════════════════════════════════════════════"
echo "  SELECT TEST TO RUN"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "1. Test Stress Quiz (fetch from Supabase)"
echo "2. Test Patient Care Quiz (fetch from Supabase)"
echo "3. Test Stress Media (fetch from Supabase)"
echo "4. Test Patient Care Media (fetch from Supabase)"
echo "5. Test Schizophrenia Media (fetch from Supabase)"
echo "6. Test All Repositories (comprehensive)"
echo "7. Run ALL Tests"
echo "8. Exit"
echo ""
read -p "Enter your choice (1-8): " choice

case $choice in
    1)
        TEST_METHOD="testStressQuizIntegration"
        TEST_NAME="Stress Quiz Integration Test"
        ;;
    2)
        TEST_METHOD="testPatientQuizIntegration"
        TEST_NAME="Patient Care Quiz Integration Test"
        ;;
    3)
        TEST_METHOD="testStressMediaIntegration"
        TEST_NAME="Stress Media Integration Test"
        ;;
    4)
        TEST_METHOD="testPatientCareMediaIntegration"
        TEST_NAME="Patient Care Media Integration Test"
        ;;
    5)
        TEST_METHOD="testSchizophreniaMediaIntegration"
        TEST_NAME="Schizophrenia Media Integration Test"
        ;;
    6)
        TEST_METHOD="testAllRepositoriesComprehensive"
        TEST_NAME="Comprehensive Repository Test"
        ;;
    7)
        TEST_METHOD=""
        TEST_NAME="All Integration Tests"
        ;;
    8)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "❌ Invalid choice!"
        exit 1
        ;;
esac

echo ""
echo "════════════════════════════════════════════════════════════"
echo "  RUNNING: $TEST_NAME"
echo "════════════════════════════════════════════════════════════"
echo ""

# Clear logcat
adb logcat -c

# Run test in background and capture logcat
if [ -z "$TEST_METHOD" ]; then
    # Run all tests
    adb shell am instrument -w -r \
      -e debug false \
      -e class com.siagajiwa.siagajiwaid.integration.RepositoryIntegrationTest \
      com.siagajiwa.siagajiwaid.test/androidx.test.runner.AndroidJUnitRunner &
else
    # Run specific test
    adb shell am instrument -w -r \
      -e debug false \
      -e class com.siagajiwa.siagajiwaid.integration.RepositoryIntegrationTest#${TEST_METHOD} \
      com.siagajiwa.siagajiwaid.test/androidx.test.runner.AndroidJUnitRunner &
fi

TEST_PID=$!

# Wait a moment for test to start
sleep 2

# Stream logcat with filter
echo "📊 Test Output:"
echo "────────────────────────────────────────────────────────────"
adb logcat -v brief System.out:I *:S &
LOGCAT_PID=$!

# Wait for test to complete
wait $TEST_PID
TEST_RESULT=$?

# Stop logcat
kill $LOGCAT_PID 2>/dev/null

echo ""
echo "────────────────────────────────────────────────────────────"

if [ $TEST_RESULT -eq 0 ]; then
    echo "✅ Test completed successfully!"
else
    echo "❌ Test failed with exit code: $TEST_RESULT"
fi

echo "════════════════════════════════════════════════════════════"
echo ""
echo "📄 Full test report available at:"
echo "   app/build/reports/androidTests/connected/index.html"
echo ""
