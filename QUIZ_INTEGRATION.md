# Quiz Integration Documentation

## Overview
This document describes the integration of Supabase quiz data for the Stress Quiz and Patient Care Questionnaire screens.

## Recent Changes (v2.0)

### Stress Quiz UI Update
The Stress Quiz has been updated to support **4 answer options** (previously 2) with **longer text support** (up to 10 words per option):

**Key Changes:**
- ✅ Updated data model to include `option3` and `option4` fields
- ✅ Changed options layout from horizontal row to vertical column
- ✅ Redesigned option cards with full-width layout
- ✅ Added better visual feedback for selected options
- ✅ Improved text wrapping for longer option text
- ✅ Enhanced accessibility with larger touch targets

**UI Design:**
- Each option is now a full-width card with rounded corners
- Radio button on the left, option text on the right
- Selected state: Purple border (2dp) + light purple background
- Unselected state: Gray border (1dp) + light gray background
- Smooth animations for state transitions

## Database Tables

### 1. stress_quiz
Table for stress assessment quiz with multiple choice questions (4 options per question, each up to 10 words).

**Columns:**
- `id` (UUID): Unique identifier
- `created_at` (timestamp): Record creation time
- `question_number` (int): Question ID/number
- `question_text` (text): The question content
- `option1` (text): First answer option (max 10 words)
- `option2` (text): Second answer option (max 10 words)
- `option3` (text): Third answer option (max 10 words)
- `option4` (text): Fourth answer option (max 10 words)
- `page_number` (int): Page grouping for questions
- `order` (int): Display order (sorted ascending)

**Example Row:**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "created_at": "2025-01-15T10:30:00Z",
  "question_number": 1,
  "question_text": "Apakah Anda merasa stres dalam seminggu terakhir?",
  "option1": "Tidak pernah merasa stres sama sekali",
  "option2": "Kadang-kadang merasa stres ringan saja",
  "option3": "Sering merasa stres dan sulit tidur",
  "option4": "Selalu merasa stres sangat berat setiap hari",
  "page_number": 1,
  "order": 1
}
```

### 2. perawatan_quiz
Table for patient care questionnaire with rating scale questions (0-4 scale).

**Columns:**
- `id` (UUID): Unique identifier
- `created_at` (timestamp): Record creation time
- `question_number` (int): Question ID/number
- `question_text` (text): The question content
- `page_number` (int): Page grouping for questions
- `order` (int): Display order (sorted ascending)

**Example Row:**
```json
{
  "id": "223e4567-e89b-12d3-a456-426614174001",
  "created_at": "2025-01-15T10:30:00Z",
  "question_number": 1,
  "question_text": "Seberapa sering Anda memberikan dukungan emosional kepada pasien?",
  "page_number": 1,
  "order": 1
}
```

## Architecture

### Data Models
Location: `app/src/main/java/com/siagajiwa/siagajiwaid/data/models/QuizModels.kt`

**StressQuizQuestion:**
```kotlin
@Serializable
data class StressQuizQuestion(
    @SerialName("id") val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("question_number") val questionNumber: Int,
    @SerialName("question_text") val questionText: String,
    @SerialName("option1") val option1: String,
    @SerialName("option2") val option2: String,
    @SerialName("option3") val option3: String,
    @SerialName("option4") val option4: String,
    @SerialName("page_number") val pageNumber: Int,
    @SerialName("order") val order: Int
)
```

**PatientQuizQuestion:**
```kotlin
@Serializable
data class PatientQuizQuestion(
    @SerialName("id") val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("question_number") val questionNumber: Int,
    @SerialName("question_text") val questionText: String,
    @SerialName("page_number") val pageNumber: Int,
    @SerialName("order") val order: Int
)
```

### Repository Layer
Location: `app/src/main/java/com/siagajiwa/siagajiwaid/data/repository/QuizRepository.kt`

**Methods:**
- `getStressQuiz()`: Fetches stress quiz questions from `stress_quiz` table, ordered by `order` column
- `getPatientQuiz()`: Fetches patient quiz questions from `perawatan_quiz` table, ordered by `order` column

**Usage:**
```kotlin
val repository = QuizRepository()
val result = repository.getStressQuiz()
result.fold(
    onSuccess = { questions -> /* Handle questions */ },
    onFailure = { exception -> /* Handle error */ }
)
```

### ViewModel Layer
Location: `app/src/main/java/com/siagajiwa/siagajiwaid/viewmodel/QuizViewModel.kt`

**State Classes:**
```kotlin
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val pages: List<QuizPage>) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

sealed class PatientQuizUiState {
    object Loading : PatientQuizUiState()
    data class Success(val pages: List<QuestionnairePage>) : PatientQuizUiState()
    data class Error(val message: String) : PatientQuizUiState()
}
```

**Key Methods:**
- `loadStressQuiz()`: Loads stress quiz from Supabase, falls back to local data on error
- `loadPatientQuiz()`: Loads patient quiz from Supabase, falls back to local data on error
- `retryLoading(quizType)`: Retries loading for the specified quiz type
- `convertStressQuizToPages()`: Converts Supabase data to QuizPage format
- `convertPatientQuizToPages()`: Converts Supabase data to QuestionnairePage format

**Data Conversion:**
The ViewModel converts Supabase models to existing local data structures:
- Groups questions by `page_number`
- Sorts pages by `page_number`
- Maps fields to existing UI models

### UI Layer

#### QuizScreen (Stress Quiz)
Location: `app/src/main/java/com/siagajiwa/siagajiwaid/screens/QuizScreen.kt`

**Integration Points:**
1. Accepts `QuizViewModel` as parameter
2. Observes `stressQuizState` StateFlow
3. Shows three states: Loading, Success, Error
4. Calls `viewModel.loadStressQuiz()` on launch

**UI Features:**
- **4 Answer Options**: Each question displays 4 options in vertical layout
- **Long Text Support**: Each option supports up to 10 words
- **Card-Based Options**: Options displayed as full-width cards with radio buttons
- **Visual Feedback**: Selected options highlighted with purple border and background tint
- **Responsive Layout**: Vertical stacking for better readability with longer text

**Composables:**
- `QuizScreen`: Main entry point with state management
- `QuizLoadingContent`: Shows loading spinner with message
- `QuizErrorContent`: Shows error message with retry button
- `QuizContent`: Main quiz UI with questions, pagination, progress
- `QuizOption`: Redesigned card-based option with full-width layout and radio button

#### QuestionnaireScreen (Patient Quiz)
Location: `app/src/main/java/com/siagajiwa/siagajiwaid/screens/PatientQuiz.kt`

**Integration Points:**
1. Accepts `QuizViewModel` as parameter
2. Observes `patientQuizState` StateFlow
3. Shows three states: Loading, Success, Error
4. Calls `viewModel.loadPatientQuiz()` on launch

**Composables:**
- `QuestionnaireScreen`: Main entry point with state management
- `PatientQuizLoadingContent`: Shows loading spinner with message
- `PatientQuizErrorContent`: Shows error message with retry button
- `PatientQuizContent`: Main questionnaire UI with rating scale

## Features

### 1. Loading State
- Displays centered circular progress indicator
- Shows localized loading message
- Prevents user interaction during load

### 2. Success State
- Displays quiz questions grouped by pages
- Maintains all existing UI/UX features:
  - Animated page transitions
  - Progress tracking
  - Page indicators
  - Navigation controls
  - Submit button when all answered

### 3. Error State
- Shows user-friendly error message
- Provides retry button
- Uses fallback to local data automatically

### 4. Fallback Strategy
When Supabase data is unavailable:
- Empty response: Uses local data from `QuizData.pages` or `QuestionnaireData.pages`
- Network error: Uses local data from `QuizData.pages` or `QuestionnaireData.pages`
- No interruption to user experience

## Testing

### Build Test
```bash
./gradlew :app:assembleDebug
```

### Manual Testing Checklist
- [ ] Stress Quiz loads from Supabase
- [ ] Patient Quiz loads from Supabase
- [ ] Loading spinner appears during data fetch
- [ ] Questions display correctly
- [ ] Page navigation works
- [ ] Progress tracking works
- [ ] Error state shows on network failure
- [ ] Retry button works
- [ ] Fallback to local data on error

## Database Setup

### Creating Tables
```sql
-- Stress Quiz Table (with 4 options)
CREATE TABLE stress_quiz (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ DEFAULT now(),
    question_number INT NOT NULL,
    question_text TEXT NOT NULL,
    option1 TEXT NOT NULL,
    option2 TEXT NOT NULL,
    option3 TEXT NOT NULL,
    option4 TEXT NOT NULL,
    page_number INT NOT NULL,
    "order" INT NOT NULL
);

-- Patient Quiz Table
CREATE TABLE perawatan_quiz (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ DEFAULT now(),
    question_number INT NOT NULL,
    question_text TEXT NOT NULL,
    page_number INT NOT NULL,
    "order" INT NOT NULL
);
```

### Sample Data

**stress_quiz:**
```sql
INSERT INTO stress_quiz (question_number, question_text, option1, option2, option3, option4, page_number, "order")
VALUES
  (1, 'Apakah Anda merasa stres dalam seminggu terakhir?',
      'Tidak pernah merasa stres sama sekali',
      'Kadang-kadang merasa stres ringan saja',
      'Sering merasa stres dan sulit tidur',
      'Selalu merasa stres sangat berat setiap hari',
      1, 1),
  (2, 'Bagaimana kualitas tidur Anda belakangan ini?',
      'Tidur sangat nyenyak setiap malam',
      'Tidur cukup baik dengan sedikit gangguan',
      'Sering terbangun di malam hari',
      'Sangat sulit tidur dan sering insomnia',
      1, 2);
```

**perawatan_quiz:**
```sql
INSERT INTO perawatan_quiz (question_number, question_text, page_number, "order")
VALUES
  (1, 'Seberapa sering Anda memberikan dukungan emosional kepada pasien?', 1, 1),
  (2, 'Seberapa sering Anda membantu pasien dengan aktivitas harian?', 1, 2);
```

## Troubleshooting

### Issue: Quiz doesn't load from Supabase
**Solution:**
1. Check Supabase credentials in `.env` file
2. Verify tables exist in Supabase dashboard
3. Check network connectivity
4. Review Supabase logs for errors
5. App will automatically fallback to local data

### Issue: Questions display in wrong order
**Solution:**
- Verify `order` column values in database
- Questions are sorted by `order` ASC
- Check `page_number` for proper grouping

### Issue: Build fails with serialization errors
**Solution:**
- Ensure all `@SerialName` annotations match database column names exactly
- Verify kotlinx.serialization plugin is enabled
- Check that all fields are non-nullable or have defaults

## Dependencies

Required in `app/build.gradle.kts`:
```kotlin
// Supabase
implementation("io.github.jan-tennert.supabase:postgrest-kt:VERSION")
implementation("io.github.jan-tennert.supabase:realtime-kt:VERSION")

// Kotlinx Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:VERSION")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:VERSION")
```

## Future Enhancements

1. **Caching**: Implement local caching to reduce network requests
2. **Offline Support**: Save quiz responses locally and sync when online
3. **Analytics**: Track quiz completion rates and common answers
4. **Dynamic Scoring**: Calculate and display scores based on quiz responses
5. **Real-time Updates**: Use Supabase Realtime to update quiz content without app restart
6. **Multi-language**: Support for multiple languages in quiz content

## References

- Supabase Documentation: https://supabase.com/docs
- Kotlinx Serialization: https://github.com/Kotlin/kotlinx.serialization
- Jetpack Compose State Management: https://developer.android.com/jetpack/compose/state
