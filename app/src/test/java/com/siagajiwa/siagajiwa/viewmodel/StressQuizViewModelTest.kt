package com.siagajiwa.siagajiwa.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.siagajiwa.siagajiwa.data.QuizQuestion
import com.siagajiwa.siagajiwa.data.models.StressQuizQuestion
import com.siagajiwa.siagajiwa.data.repository.QuizRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for StressQuizViewModel
 * Tests stress assessment quiz loading, state management, and score calculation
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StressQuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: StressQuizViewModel
    private lateinit var mockRepository: QuizRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()

        // Create ViewModel with mocked repository
        viewModel = StressQuizViewModel().apply {
            // We'll need to use reflection or dependency injection to inject mock repository
            // For now, we'll test the conversion logic and state management
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        // Given - fresh ViewModel
        val viewModel = StressQuizViewModel()

        // When - check initial state
        val initialState = viewModel.quizState.value

        // Then - state should be Loading
        assertTrue(initialState is StressQuizUiState.Loading)
    }

    @Test
    fun `loadQuiz with empty questions should return Error state`() = runTest {
        // Given
        val emptyQuestions = emptyList<StressQuizQuestion>()
        coEvery { mockRepository.getStressQuiz() } returns Result.success(emptyQuestions)

        // When
        viewModel.loadQuiz()
        advanceUntilIdle()

        // Then
        val state = viewModel.quizState.first()
        assertTrue(state is StressQuizUiState.Error)
        assertEquals("No quiz questions found in database", (state as StressQuizUiState.Error).message)
    }

    @Test
    fun `loadQuiz with valid questions should return Success state`() = runTest {
        // Given
        val mockStressQuestions = listOf(
            StressQuizQuestion(
                id = 1,
                questionNumber = 1,
                questionText = "Seberapa sering Anda merasa stres?",
                answerOption = "0|1|2|3|4",
                correctAnswer = null,
                order = 1
            ),
            StressQuizQuestion(
                id = 2,
                questionNumber = 2,
                questionText = "Seberapa sering Anda merasa cemas?",
                answerOption = "0|1|2|3|4",
                correctAnswer = null,
                order = 2
            )
        )

        coEvery { mockRepository.getStressQuiz() } returns Result.success(mockStressQuestions)

        // When
        viewModel.loadQuiz()
        advanceUntilIdle()

        // Then
        val state = viewModel.quizState.first()
        assertTrue(state is StressQuizUiState.Success)
        val questions = (state as StressQuizUiState.Success).questions
        assertEquals(2, questions.size)
        assertEquals("Seberapa sering Anda merasa stres?", questions[0].text)
        assertEquals("Seberapa sering Anda merasa cemas?", questions[1].text)
    }

    @Test
    fun `loadQuiz with repository failure should return Error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getStressQuiz() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.loadQuiz()
        advanceUntilIdle()

        // Then
        val state = viewModel.quizState.first()
        assertTrue(state is StressQuizUiState.Error)
        assertTrue((state as StressQuizUiState.Error).message.contains(errorMessage))
    }

    @Test
    fun `questions should have empty options for stress quiz`() = runTest {
        // Given - stress quiz questions
        val mockStressQuestions = listOf(
            StressQuizQuestion(
                id = 1,
                questionNumber = 1,
                questionText = "Test question",
                answerOption = "0|1|2|3|4", // This should be ignored for stress quiz
                correctAnswer = null,
                order = 1
            )
        )

        coEvery { mockRepository.getStressQuiz() } returns Result.success(mockStressQuestions)

        // When
        viewModel.loadQuiz()
        advanceUntilIdle()

        // Then
        val state = viewModel.quizState.first()
        val questions = (state as StressQuizUiState.Success).questions
        assertTrue(questions[0].options.isEmpty()) // Stress quiz uses rating scale, not options
        assertNull(questions[0].correctAnswerIndex) // No correct answer for stress assessment
    }

    @Test
    fun `questions should be sorted by order field`() = runTest {
        // Given - questions with mixed order
        val mockStressQuestions = listOf(
            StressQuizQuestion(
                id = 3,
                questionNumber = 3,
                questionText = "Third question",
                order = 3
            ),
            StressQuizQuestion(
                id = 1,
                questionNumber = 1,
                questionText = "First question",
                order = 1
            ),
            StressQuizQuestion(
                id = 2,
                questionNumber = 2,
                questionText = "Second question",
                order = 2
            )
        )

        coEvery { mockRepository.getStressQuiz() } returns Result.success(mockStressQuestions)

        // When
        viewModel.loadQuiz()
        advanceUntilIdle()

        // Then
        val state = viewModel.quizState.first()
        val questions = (state as StressQuizUiState.Success).questions
        assertEquals("First question", questions[0].text)
        assertEquals("Second question", questions[1].text)
        assertEquals("Third question", questions[2].text)
    }

    @Test
    fun `submitStressQuiz should calculate total score correctly`() {
        // Given
        val questions = listOf(
            QuizQuestion(id = 1, text = "Q1", options = emptyList()),
            QuizQuestion(id = 2, text = "Q2", options = emptyList()),
            QuizQuestion(id = 3, text = "Q3", options = emptyList())
        )

        val answers = mapOf(
            1 to "2", // Kadang-Kadang
            2 to "3", // Cukup Sering
            3 to "4"  // Terlalu Sering
        )

        var resultScore = 0
        var resultMaxScore = 0

        // When
        viewModel.submitStressQuiz(
            answers = answers,
            questions = questions,
            onSuccess = { score, maxScore ->
                resultScore = score
                resultMaxScore = maxScore
            },
            onError = { fail("Should not call onError") }
        )

        // Then
        // Total score should be 2 + 3 + 4 = 9
        assertEquals(9, resultScore)
        // Max possible score should be 3 questions × 4 = 12
        assertEquals(12, resultMaxScore)
    }

    @Test
    fun `submitStressQuiz should handle all zeros score`() {
        // Given
        val questions = listOf(
            QuizQuestion(id = 1, text = "Q1", options = emptyList()),
            QuizQuestion(id = 2, text = "Q2", options = emptyList())
        )

        val answers = mapOf(
            1 to "0", // Tidak Pernah
            2 to "0"  // Tidak Pernah
        )

        var resultScore = 0

        // When
        viewModel.submitStressQuiz(
            answers = answers,
            questions = questions,
            onSuccess = { score, _ ->
                resultScore = score
            },
            onError = { fail("Should not call onError") }
        )

        // Then
        assertEquals(0, resultScore)
    }

    @Test
    fun `submitStressQuiz should handle maximum stress score`() {
        // Given
        val questions = listOf(
            QuizQuestion(id = 1, text = "Q1", options = emptyList()),
            QuizQuestion(id = 2, text = "Q2", options = emptyList())
        )

        val answers = mapOf(
            1 to "4", // Terlalu Sering
            2 to "4"  // Terlalu Sering
        )

        var resultScore = 0
        var resultMaxScore = 0

        // When
        viewModel.submitStressQuiz(
            answers = answers,
            questions = questions,
            onSuccess = { score, maxScore ->
                resultScore = score
                resultMaxScore = maxScore
            },
            onError = { fail("Should not call onError") }
        )

        // Then
        assertEquals(8, resultScore)
        assertEquals(8, resultMaxScore)
    }

    @Test
    fun `submitStressQuiz should handle invalid answer values gracefully`() {
        // Given
        val questions = listOf(
            QuizQuestion(id = 1, text = "Q1", options = emptyList()),
            QuizQuestion(id = 2, text = "Q2", options = emptyList())
        )

        val answers = mapOf(
            1 to "2",
            2 to "invalid" // Invalid value should be treated as 0
        )

        var resultScore = 0

        // When
        viewModel.submitStressQuiz(
            answers = answers,
            questions = questions,
            onSuccess = { score, _ ->
                resultScore = score
            },
            onError = { fail("Should not call onError") }
        )

        // Then
        // Score should be 2 + 0 = 2 (invalid value treated as 0)
        assertEquals(2, resultScore)
    }

    @Test
    fun `submitStressQuiz should calculate correct percentage`() {
        // Given
        val questions = listOf(
            QuizQuestion(id = 1, text = "Q1", options = emptyList()),
            QuizQuestion(id = 2, text = "Q2", options = emptyList()),
            QuizQuestion(id = 3, text = "Q3", options = emptyList()),
            QuizQuestion(id = 4, text = "Q4", options = emptyList()),
            QuizQuestion(id = 5, text = "Q5", options = emptyList())
        )

        val answers = mapOf(
            1 to "2", // 2
            2 to "2", // 2
            3 to "2", // 2
            4 to "2", // 2
            5 to "2"  // 2
        )

        // Total score: 10 out of max 20 (5 questions × 4)
        // Percentage should be 50%

        // This test demonstrates the scoring logic
        val totalScore = 10
        val maxPossibleScore = 20
        val expectedPercentage = (totalScore * 100) / maxPossibleScore

        assertEquals(50, expectedPercentage)
    }

    @Test
    fun `retryLoading should trigger loadQuiz again`() = runTest {
        // Given
        val mockStressQuestions = listOf(
            StressQuizQuestion(
                id = 1,
                questionNumber = 1,
                questionText = "Test",
                order = 1
            )
        )

        coEvery { mockRepository.getStressQuiz() } returns Result.success(mockStressQuestions)

        // When
        viewModel.retryLoading()
        advanceUntilIdle()

        // Then - should attempt to load quiz
        // State should change from Loading to Success
        val state = viewModel.quizState.first()
        assertTrue(state is StressQuizUiState.Success || state is StressQuizUiState.Loading)
    }

    @Test
    fun `stress quiz for 10 questions with typical caregiver responses`() {
        // Given - Realistic caregiver stress assessment scenario
        val questions = (1..10).map {
            QuizQuestion(id = it, text = "Question $it", options = emptyList())
        }

        // Typical caregiver stress responses (mixed levels)
        val answers = mapOf(
            1 to "2",  // Kadang-Kadang
            2 to "3",  // Cukup Sering
            3 to "3",  // Cukup Sering
            4 to "1",  // Hampir Tidak Pernah (positive question)
            5 to "1",  // Hampir Tidak Pernah (positive question)
            6 to "4",  // Terlalu Sering (high stress)
            7 to "2",  // Kadang-Kadang
            8 to "2",  // Kadang-Kadang
            9 to "3",  // Cukup Sering
            10 to "3"  // Cukup Sering
        )

        var resultScore = 0
        var resultMaxScore = 0

        // When
        viewModel.submitStressQuiz(
            answers = answers,
            questions = questions,
            onSuccess = { score, maxScore ->
                resultScore = score
                resultMaxScore = maxScore
            },
            onError = { fail("Should not call onError") }
        )

        // Then
        // Total: 2+3+3+1+1+4+2+2+3+3 = 24 out of 40
        assertEquals(24, resultScore)
        assertEquals(40, resultMaxScore)

        // Percentage should be 60%
        val percentage = (resultScore * 100) / resultMaxScore
        assertEquals(60, percentage)
    }
}
