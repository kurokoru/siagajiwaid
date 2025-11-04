# Media Integration Test Results

## Test Execution Summary

**Test Name:** `testGetPatientCareMediaWithAuthentication`
**Test File:** `app/src/androidTest/java/com/siagajiwa/siagajiwaid/integration/MediaIntegrationTest.kt`
**Device:** SM-A525F (Android 14)
**Date:** October 26, 2025

## Test Objective

Create an integration test to:
1. Login with user `vahyo@yopmail.com` (password: `123456`)
2. Fetch patient care media data from `perawatan_media` table using `getPatientCareMedia()`
3. Display the results in the test output/logcat

## Test Results

### ❌ Test Failed

**Error:** `Email not confirmed`

**Root Cause:**
The user account `vahyo@yopmail.com` exists in Supabase but the email address has not been confirmed. Supabase requires email confirmation before allowing authentication.

### Error Details

```
io.github.jan.supabase.gotrue.exception.AuthRestException: Email not confirmed
	at io.github.jan.supabase.gotrue.AuthImpl.checkErrorCodes(AuthImpl.kt:523)
	at io.github.jan.supabase.gotrue.AuthImpl.parseErrorResponse(AuthImpl.kt:491)
```

## Solutions

### Option 1: Confirm Email in Supabase Dashboard (Recommended)

1. Go to your Supabase project dashboard
2. Navigate to **Authentication** → **Users**
3. Find the user `vahyo@yopmail.com`
4. Click on the user
5. Look for "Email Confirmed" status and manually confirm the email

### Option 2: Disable Email Confirmation for Development

1. Go to your Supabase project dashboard
2. Navigate to **Authentication** → **Settings**
3. Under "Email Auth" section
4. Disable **"Enable email confirmations"**
5. This will allow users to log in without confirming their email (not recommended for production)

### Option 3: Use Auto-Confirm in Supabase Auth Settings

1. Go to **Authentication** → **Settings** → **Advanced**
2. Enable **"Confirm email"** to be skipped for new signups
3. Set the confirmation requirement based on your needs

### Option 4: Create a New Confirmed Test User

Create a new test user specifically for testing:

```sql
-- In Supabase SQL Editor
-- This creates a user and marks email as confirmed
INSERT INTO auth.users (
    instance_id,
    id,
    aud,
    role,
    email,
    encrypted_password,
    email_confirmed_at,
    raw_app_meta_data,
    raw_user_meta_data,
    created_at,
    updated_at,
    confirmation_token,
    recovery_token
) VALUES (
    '00000000-0000-0000-0000-000000000000',
    uuid_generate_v4(),
    'authenticated',
    'authenticated',
    'test@example.com',
    crypt('test123456', gen_salt('bf')),
    now(),  -- This confirms the email immediately
    '{"provider":"email","providers":["email"]}',
    '{}',
    now(),
    now(),
    '',
    ''
);
```

## Test Implementation

The integration test has been successfully created at:
`app/src/androidTest/java/com/siagajiwa/siagajiwaid/integration/MediaIntegrationTest.kt`

### Test Features

✅ **Login with Credentials** - Attempts to authenticate with Supabase
✅ **Error Logging** - Detailed error messages and stack traces
✅ **Session Verification** - Checks if user session is established
✅ **Media Fetching** - Calls `getPatientCareMedia()` from MediaRepository
✅ **Result Display** - Logs media content in formatted table
✅ **Data Validation** - Verifies media items are properly ordered
✅ **Cleanup** - Signs out user after test completion

### Additional Test Methods Included

1. **`testGetPatientCareMediaWithoutAuthentication`** - Tests media access without login (checks RLS policies)
2. **`testGetAllMediaTypes`** - Tests all three media types (patient care, stress, schizophrenia)
3. **`testMediaDataIntegrity`** - Validates data structure and content of media items

## How to Run the Test (Once Email is Confirmed)

### Command Line

```bash
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=siagajiwantegration.MediaIntegrationTest#testGetPatientCareMediaWithAuthentication
```

### Run All Media Tests

```bash
./gradlew :app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=siagajiwantegration.MediaIntegrationTest
```

### View Results

- **Test Report:** `app/build/reports/androidTests/connected/debug/index.html`
- **Logcat Output:** Use `adb logcat | grep MediaIntegrationTest`

## Expected Output (Once Fixed)

When the test passes, you should see output like this in logcat:

```
========================================
Starting Media Integration Test
========================================

--- Step 1: Authentication ---
Attempting to login with: vahyo@yopmail.com
✓ Login successful!
  User ID: [user-uuid]
  Email: vahyo@yopmail.com
✓ User session verified

--- Step 2: Fetching Patient Care Media ---
Calling getPatientCareMedia()...
✓ Media fetch successful!
  Total items: 5

--- Step 3: Media Content Details ---
┌─────────────────────────────────────────────────────────────────────────────┐
│                     PATIENT CARE MEDIA TABLE RESULTS                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ Item 1/5
│   ID: [media-id-1]
│   Order: 1
│   Link: https://...
│   Created At: 2025-10-25T...
├─────────────────────────────────────────────────────────────────────────────┤
...
└─────────────────────────────────────────────────────────────────────────────┘

✓ Media items are correctly ordered: [1, 2, 3, 4, 5]

========================================
Test Completed Successfully!
========================================
```

## Database Structure

The test accesses the following Supabase table:

### `perawatan_media` Table

| Column | Type | Description |
|--------|------|-------------|
| `id` | String | Unique identifier for media item |
| `created_at` | String (ISO 8601) | Timestamp when media was created |
| `link` | String | URL or path to media content |
| `order` | Int | Display order (ascending) |

## Next Steps

1. **Confirm the email for `vahyo@yopmail.com` in Supabase dashboard**
2. **Re-run the test** using the command above
3. **Check logcat** for detailed media data output
4. **Optionally:** Update test credentials in the test file if needed

## Test File Location

`/Users/hafidhidayatullah/AndroidStudioProjects/siagajiwaid/app/src/androidTest/java/com/siagajiwa/siagajiwaid/integration/MediaIntegrationTest.kt`
