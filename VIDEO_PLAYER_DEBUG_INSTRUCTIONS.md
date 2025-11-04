# Video Player Debug Instructions

## Issues Fixed

### Problem Identified
The WebView was being created but immediately disposed before the video could load. This was caused by:
1. Using `AnimatedVisibility` which caused premature recomposition
2. Unstable composition keys in LazyColumn
3. WebView lifecycle not properly managed

### Fixes Applied
1. ✅ **Removed AnimatedVisibility** - Replaced with simple `if` statement for better stability
2. ✅ **Added stable keys** - Each LazyColumn item now has a unique key based on video URL
3. ✅ **Improved WebView lifecycle** - Better tracking of WebView creation and disposal
4. ✅ **Enhanced logging** - Added detailed logs with emojis for easy identification

## Testing Instructions

### Step 1: Navigate to Video Screen
1. Open the app
2. Navigate to the Video Tutorial screen (usually from home screen)

### Step 2: Click on a Video
1. Tap on any video card
2. The video player should expand below the card
3. Video should start playing automatically

### Expected Behavior:
- ✅ Video card expands smoothly
- ✅ Black player area appears below the card
- ✅ Video loads and plays automatically
- ✅ Video metadata (title, views, description) appears below player
- ✅ Close button (X) in top-right corner

### Step 3: Monitor Logs
Open a terminal and run:
```bash
/Users/hafidhidayatullah/Library/Android/sdk/platform-tools/adb -s emulator-5554 logcat -s InlinePlayer:D InlinePlayer-Console:D
```

### What to Look For in Logs:

#### ✅ GOOD LOGS (Video Working):
```
🎬 Factory: Creating WebView for video ID: shfd8vZj-T4
📱 Update: WebView visibility set to VISIBLE
Page started loading: https://www.youtube.com/
Loading progress: 10%
Loading progress: 80%
Loading progress: 100%
Page finished loading: https://www.youtube.com/
```

#### ❌ BAD LOGS (Video Not Working):
```
🎬 Factory: Creating WebView for video ID: shfd8vZj-T4
🗑️ Release: Cleaning up WebView for video ID: shfd8vZj-T4
```
*Note: If Release happens immediately after Factory, the WebView is being destroyed too soon*

## Troubleshooting

### Issue: Blank Screen Still Appears

**Check 1: Verify Video ID Extraction**
Look for this log line:
```
Extracted video ID: [ID] from URL: [URL]
```
The video ID should be 11 characters (e.g., `shfd8vZj-T4`)

**Check 2: Check for WebView Errors**
Run:
```bash
adb -s emulator-5554 logcat | grep -E "(chromium|cr_)"
```
Look for any error messages about WebView or Chromium

**Check 3: Check Internet Connection**
Ensure the emulator/device has internet access:
```bash
adb -s emulator-5554 shell ping -c 3 youtube.com
```

**Check 4: Try Different Video**
Some videos may have embedding restrictions. Try clicking on a different video.

### Issue: Video Loads But Doesn't Play

**Possible Causes:**
1. **Autoplay blocked** - Some devices block autoplay. User may need to tap the play button
2. **YouTube restrictions** - Video might not allow embedding
3. **Network issues** - Slow connection might cause buffering

**Solution:**
Try removing `autoplay=1` from the iframe URL (line 540 in VideoScreen.kt):
```kotlin
src="https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1&playsinline=1"
```

### Issue: App Crashes

Run:
```bash
adb -s emulator-5554 logcat | grep -E "(FATAL|AndroidRuntime)"
```

Look for crash logs and share them for further debugging.

## Advanced Debugging

### Full Logcat Dump
To see all logs related to the video player:
```bash
adb -s emulator-5554 logcat -s InlinePlayer:* chromium:E cr_*:E
```

### Clear Logs and Test
```bash
# Clear logs
adb -s emulator-5554 logcat -c

# Navigate to video screen and click a video

# Dump logs
adb -s emulator-5554 logcat -d | grep InlinePlayer > video_debug.log
```

### WebView Debugging
Enable WebView debugging by adding this to MainActivity onCreate (if needed):
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

Then use Chrome DevTools:
1. Open Chrome browser
2. Go to `chrome://inspect`
3. Find your device
4. Click "inspect" on the WebView

## Quick Test Commands

```bash
# Install app
./gradlew installDebug

# Launch app
adb -s emulator-5554 shell am start -n com.siagajiwa.siagajiwa/.MainActivity

# Monitor logs in real-time
adb -s emulator-5554 logcat -s InlinePlayer:D

# Check for errors
adb -s emulator-5554 logcat | grep -E "(ERROR|FATAL|InlinePlayer)"
```

## Video URLs Being Used

The app currently uses these three videos:
1. `https://www.youtube.com/watch?v=shfd8vZj-T4` (Relaksasi Otot Progresif)
2. `https://www.youtube.com/watch?v=hanFFhhEGlk` (Relaksasi Napas Dalam)
3. `https://www.youtube.com/watch?v=E7gvUyajGqE` (Terapi Imajinasi Terpadu)

You can test these URLs in a browser to ensure they're working:
```bash
# Test video 1
adb -s emulator-5554 shell am start -a android.intent.action.VIEW -d "https://www.youtube.com/watch?v=shfd8vZj-T4"
```

## Known Limitations

1. **Autoplay may not work on all devices** - Some Android versions block autoplay
2. **Some videos may not embed** - YouTube videos with embedding disabled won't play
3. **Emulator performance** - Hardware acceleration may not work perfectly in emulator

## Success Criteria

✅ Video player appears when clicking video card
✅ Video loads (you see YouTube player UI)
✅ Video plays automatically or has visible play button
✅ Video metadata (title, views, description) is visible
✅ Close button works to collapse player
✅ Can open different videos
✅ Only one video plays at a time

## Next Steps If Still Not Working

1. Capture logs while clicking on a video
2. Share the logs
3. Test on a physical device (not emulator)
4. Try using the full-screen VideoPlayerScreen as fallback
