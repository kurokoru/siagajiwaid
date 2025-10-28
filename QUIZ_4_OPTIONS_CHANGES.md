# Quiz Screen Update: 4 Options with Long Text Support

## Summary
Updated the Stress Quiz (QuizScreen) to support **4 answer options** instead of 2, with support for **up to 10 words per option**.

## Changes Made

### 1. Data Model Update
**File:** `app/src/main/java/com/siagajiwa/siagajiwaid/data/models/QuizModels.kt`

**Changes:**
- Added `option3` field (String)
- Added `option4` field (String)

**Before:**
```kotlin
data class StressQuizQuestion(
    // ...
    val option1: String,
    val option2: String,
    // ...
)
```

**After:**
```kotlin
data class StressQuizQuestion(
    // ...
    val option1: String,
    val option2: String,
    val option3: String,  // NEW
    val option4: String,  // NEW
    // ...
)
```

### 2. ViewModel Update
**File:** `app/src/main/java/com/siagajiwa/siagajiwaid/viewmodel/QuizViewModel.kt`

**Changes:**
- Updated `convertStressQuizToPages()` to include all 4 options

**Before:**
```kotlin
options = listOf(q.option1, q.option2)
```

**After:**
```kotlin
options = listOf(q.option1, q.option2, q.option3, q.option4)
```

### 3. UI Layout Update
**File:** `app/src/main/java/com/siagajiwa/siagajiwaid/screens/QuizScreen.kt`

#### 3.1 Options Container Layout
Changed from horizontal Row to vertical Column for better readability with longer text.

**Before:**
```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    options.forEach { option ->
        QuizOption(
            option = option,
            isSelected = selectedAnswer == option,
            onSelected = { onAnswerSelected(option) },
            modifier = Modifier.weight(1f)
        )
    }
}
```

**After:**
```kotlin
Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    options.forEach { option ->
        QuizOption(
            option = option,
            isSelected = selectedAnswer == option,
            onSelected = { onAnswerSelected(option) }
        )
    }
}
```

#### 3.2 QuizOption Composable Redesign
Completely redesigned the option component to support longer text with better visual design.

**Key Features:**
- **Full-Width Cards:** Each option is now a full-width card instead of inline radio buttons
- **Better Visual Hierarchy:** Radio button on the left, text wraps naturally on the right
- **Clear Selection State:**
  - Selected: Purple border (2dp) + light purple background tint
  - Unselected: Gray border (1dp) + light gray background
- **Improved Touch Target:** Larger, easier to tap areas
- **Text Support:** Handles up to 10 words with proper line wrapping
- **Smooth Animations:** Animated transitions for border, background, and text color

**Design Specs:**
```kotlin
// Selected State
border: 2.dp, color: SecondaryPurple
background: SecondaryPurple.copy(alpha = 0.1f)
text color: SecondaryPurple
radio button: Filled with purple, white center dot

// Unselected State
border: 1.dp, color: Color(0xFFE0E0E0)
background: Color(0xFFF8F8F8)
text color: DarkLight
radio button: Empty with gray border
```

### 4. Import Addition
Added `import androidx.compose.foundation.border` to support border styling.

## Database Schema Update Required

### SQL Migration
To use the new 4-option format, update your Supabase `stress_quiz` table:

```sql
-- Add new columns to existing table
ALTER TABLE stress_quiz
ADD COLUMN option3 TEXT,
ADD COLUMN option4 TEXT;

-- Update existing rows (example - adjust as needed)
UPDATE stress_quiz
SET
    option3 = 'Option 3 text here',
    option4 = 'Option 4 text here'
WHERE option3 IS NULL;

-- Make columns required (after data is populated)
ALTER TABLE stress_quiz
ALTER COLUMN option3 SET NOT NULL,
ALTER COLUMN option4 SET NOT NULL;
```

### Sample Insert Statement
```sql
INSERT INTO stress_quiz (
    question_number,
    question_text,
    option1,
    option2,
    option3,
    option4,
    page_number,
    "order"
)
VALUES (
    1,
    'Apakah Anda merasa stres dalam seminggu terakhir?',
    'Tidak pernah merasa stres sama sekali',
    'Kadang-kadang merasa stres ringan saja',
    'Sering merasa stres dan sulit tidur',
    'Selalu merasa stres sangat berat setiap hari',
    1,
    1
);
```

## Visual Design Improvements

### Before (2 Options - Horizontal)
```
┌─────────────────────────────────────┐
│ 1. Question text here?              │
│                                     │
│  ○ Ya        ○ Tidak                │
└─────────────────────────────────────┘
```

### After (4 Options - Vertical)
```
┌─────────────────────────────────────┐
│ 1. Question text here?              │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ● Tidak pernah merasa stres... │ │ ← Selected
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ○ Kadang-kadang merasa stres..│ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ○ Sering merasa stres dan...  │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ○ Selalu merasa stres sangat..│ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

## Benefits

1. **More Answer Choices:** 4 options provide more nuanced responses
2. **Better Readability:** Vertical layout prevents text cramping
3. **Longer Text Support:** Each option can contain up to 10 words (descriptive answers)
4. **Improved UX:** Larger touch targets, clearer visual feedback
5. **Professional Appearance:** Card-based design looks more polished
6. **Accessibility:** Better spacing and contrast for all users
7. **Responsive:** Text wraps naturally on smaller screens

## Testing

### Build Status
✅ **Build Successful** - No compilation errors

### Manual Testing Checklist
- [ ] Quiz loads with 4 options per question
- [ ] Long text (10 words) displays correctly without overflow
- [ ] Selected option shows purple border and background
- [ ] Unselected options show gray border
- [ ] Tapping an option selects it correctly
- [ ] Animation transitions are smooth
- [ ] Progress tracking still works
- [ ] Page navigation still works
- [ ] Submit button appears when all answered

## Files Modified

1. ✅ `app/src/main/java/com/siagajiwa/siagajiwaid/data/models/QuizModels.kt`
2. ✅ `app/src/main/java/com/siagajiwa/siagajiwaid/viewmodel/QuizViewModel.kt`
3. ✅ `app/src/main/java/com/siagajiwa/siagajiwaid/screens/QuizScreen.kt`
4. ✅ `QUIZ_INTEGRATION.md` (updated documentation)
5. ✅ `app/src/main/java/com/siagajiwa/siagajiwaid/screens/SignupScreen.kt` (fixed typo: Rowange → Row)

## Backward Compatibility

⚠️ **Breaking Change**: This update requires database schema changes.

**Migration Path:**
1. Update database schema to add `option3` and `option4` columns
2. Populate new columns with appropriate data
3. Deploy app update
4. Old app versions will fail to load quiz if columns are missing

**Fallback Strategy:**
The app will fall back to local quiz data (QuizData.pages) if Supabase fetch fails, maintaining functionality even during transition.

## Next Steps

1. **Update Database:** Add option3 and option4 columns to stress_quiz table
2. **Add Content:** Populate all quiz questions with 4 meaningful options
3. **Test:** Thoroughly test with real data on device
4. **Update Local Fallback:** Update QuizData.kt to include 4 options for offline support
5. **User Testing:** Get feedback on new UI design and option clarity

## Notes

- The Patient Quiz (QuestionnaireScreen) still uses the rating scale (0-4) and is unchanged
- Only the Stress Quiz (QuizScreen) has been updated to 4 multiple-choice options
- The UI design follows Material Design 3 principles with custom purple theming
- All animations use `tween(300)` for consistent 300ms transition duration
