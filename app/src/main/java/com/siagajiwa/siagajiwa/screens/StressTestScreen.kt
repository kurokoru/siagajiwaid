package com.siagajiwa.siagajiwa.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.data.QuizQuestion
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.SecondaryPurple
import com.siagajiwa.siagajiwa.ui.theme.White
import com.siagajiwa.siagajiwa.utils.StressCalculator
import com.siagajiwa.siagajiwa.viewmodel.StressQuizViewModel
import com.siagajiwa.siagajiwa.viewmodel.StressQuizUiState
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    navController: NavHostController,
    viewModel: StressQuizViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    val quizState by viewModel.quizState.collectAsState()

    // Check auth state when screen loads - redirect to login if not authenticated
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Small delay to let session load
        val currentUser = com.siagajiwa.siagajiwa.data.SupabaseClient.auth.currentUserOrNull()
        Log.d("StressTestScreen", "Screen loaded - Auth state check:")
        Log.d("StressTestScreen", "  From SupabaseClient - User ID: ${currentUser?.id}")
        Log.d("StressTestScreen", "  From SupabaseClient - User Email: ${currentUser?.email}")

        // Redirect to login if user is not authenticated
        if (currentUser == null) {
            Log.e("StressTestScreen", "  WARNING: User is NOT logged in - redirecting to login")
            navController.navigate("LoginScreen") {
                popUpTo("QuizScreen") { inclusive = true }
            }
        } else {
            Log.d("StressTestScreen", "  ✅ User is logged in successfully")
        }
    }

    // Load STRESS quiz when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadQuiz()
    }

    when (val state = quizState) {
        is StressQuizUiState.Loading -> {
            LoadingContent()
        }
        is StressQuizUiState.Success -> {
            val questions = state.questions
            QuizScreenContent(
                navController = navController,
                viewModel = viewModel,
                questions = questions,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        }
        is StressQuizUiState.Error -> {
            ErrorContent(
                message = state.message,
                onRetry = { viewModel.retryLoading() }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = SecondaryPurple)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Error: $message", color = DarkLight)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun QuizScreenContent(
    navController: NavHostController,
    viewModel: StressQuizViewModel,
    questions: List<QuizQuestion>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, String>()) }

    val totalQuestions = questions.size
    val answeredQuestions = selectedAnswers.size
    val progress = answeredQuestions.toFloat() / totalQuestions

    // Check if all questions are answered
    val allQuestionsAnswered = selectedAnswers.size == totalQuestions

    // Scroll state for notification
    val listState = rememberLazyListState()
    var showScrollNotification by remember { mutableStateOf(false) }
    var isScrollActive by remember { mutableStateOf(false) }

    // Calculate scroll progress
    val scrollProgress = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty() || layoutInfo.totalItemsCount == 0) {
                0f
            } else {
                val firstVisibleItem = visibleItemsInfo.first()
                val firstItemIndex = firstVisibleItem.index.toFloat()
                val firstItemOffset = -firstVisibleItem.offset.toFloat()
                val firstItemSize = firstVisibleItem.size.toFloat()

                val scrolledItems = firstItemIndex + (firstItemOffset / firstItemSize)
                scrolledItems / layoutInfo.totalItemsCount.toFloat()
            }
        }
    }

    // Animate scroll indicator opacity
    val scrollIndicatorAlpha by animateFloatAsState(
        targetValue = if (isScrollActive) 1f else 0.3f,
        animationSpec = tween(durationMillis = 300),
        label = "scroll_indicator_alpha"
    )

    // Detect scroll events
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            showScrollNotification = true
            isScrollActive = true
            delay(2000) // Show notification for 2 seconds
            showScrollNotification = false
        }
    }

    // Reduce opacity after inactivity
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && isScrollActive) {
            delay(1000)
            isScrollActive = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Bar
            StressQuizNavigationBar(
                title = "Kuisioner untuk pengasuh",
                onBackClick = { navController.popBackStack() }
            )

            // Scrollable content with all questions
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
            ) {
                items(questions.size) { index ->
                    val question = questions[index]
                    // ADD THIS TO DEBUG:
                    Log.d("StressTest", "Index: $index, QuestionNumber: ${index + 1}, QuestionID: ${question.id}")
                    StressQuestionCard(
                        questionNumber = index + 1,
                        questionText = question.text,
                        selectedValue = selectedAnswers[question.id],
                        onValueSelected = { value ->
                            Log.d("StressTest", "Selected value $value for question ID: ${question.id}")
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(question.id, value)
                            }
                        }
                    )

                    if (index < questions.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Add spacing at the end to prevent content being hidden behind submit button
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

        // Bottom section with unanswered count and submit button - fixed position above bottom navigation
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp), // Height of bottom navigation
            color = White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show unanswered questions count
                val unansweredCount = totalQuestions - answeredQuestions
                if (unansweredCount > 0) {
                    Text(
                        text = "$unansweredCount dari $totalQuestions pertanyaan belum dijawab",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF343434),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Submit button with opacity based on completion
                Button(
                    onClick = {
                        if (allQuestionsAnswered) {
                            // Calculate stress level using StressCalculator
                            val result = StressCalculator.calculateStressLevel(selectedAnswers)

                            // Log the results for debugging
                            Log.d("StressTest", "Total Score: ${result.totalScore}")
                            Log.d("StressTest", "Stress Level: ${result.stressLevel}")

                            // Map StressLevel enum to result screen enum
                            val stressLevelParam = when (result.stressLevel) {
                                com.siagajiwa.siagajiwa.utils.StressLevel.LOW -> "RENDAH"
                                com.siagajiwa.siagajiwa.utils.StressLevel.MEDIUM -> "SEDANG"
                                com.siagajiwa.siagajiwa.utils.StressLevel.HIGH -> "TINGGI"
                            }

                            // Submit to backend via ViewModel
                            viewModel.submitStressQuiz(
                                answers = selectedAnswers,
                                questions = questions,
                                onSuccess = { score, maxScore ->
                                    // Navigate to result screen with stress level
                                    navController.navigate("StressTestResultScreen/$stressLevelParam")
                                },
                                onError = { error ->
                                    Log.e("StressTest", "Error submitting quiz: $error")
                                    // Still navigate to show results even if submission fails
                                    navController.navigate("StressTestResultScreen/$stressLevelParam")
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .width(165.dp)
                        .height(32.dp)
                        .alpha(if (allQuestionsAnswered) 1f else 0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allQuestionsAnswered) SecondaryPurple else Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    enabled = allQuestionsAnswered
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (allQuestionsAnswered) White else Color(0xFF898989)
                    )
                }
            }
        }

        // Scroll indicator on right side (always visible with opacity change)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .alpha(scrollIndicatorAlpha)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight(scrollProgress.value.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(2.dp))
                        .background(SecondaryPurple)
                )
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = selectedTabIndex,
            onItemSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

@Composable
fun StressQuizNavigationBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Back",
                tint = DarkLight,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkLight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StressQuestionCard(
    questionNumber: Int,
    questionText: String,
    selectedValue: String?,
    onValueSelected: (String) -> Unit
) {
    // Stress assessment options with values 0-4 (matching Figma design)
    val stressOptions = listOf(
        "0" to "Tidak\nPernah",
        "1" to "Sesekali\n",
        "2" to "Kadang\nKadang",
        "3" to "Cukup\nSering",
        "4" to "Terlalu\nSering"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Question text
            Text(
                text = "$questionNumber. $questionText",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DarkLight,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Container for line separator and circular indicators
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Fixed height to accommodate circles and labels
            ) {
                // Horizontal line separator - positioned at the top where circles should sit
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE0E0E0))
                        .align(Alignment.TopCenter)
                )

                // Horizontal stress rating options positioned on the line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    stressOptions.forEach { (value, label) ->
                        HorizontalStressOption(
                            value = value,
                            label = label,
                            isSelected = selectedValue == value,
                            onClick = { onValueSelected(value) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HorizontalStressOption(
    value: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        // Circular indicator - positioned so its center sits on the line
        // The circle is 13dp, so we offset it up by half (6.5dp) to center it on the line
        Box(
            modifier = Modifier
                .size(13.dp)
                .offset(y = (-6.5).dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFF0890FE) else Color(0xFFE0E0E0)
                )
        ) { }

        // Label text below the circle
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF0890FE) else Color(0xFF898989),
            lineHeight = 16.sp,
            modifier = Modifier.width(55.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuizOption(
    option: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "option_scale"
    )

    val radioColor by animateColorAsState(
        targetValue = if (isSelected) SecondaryPurple else Color(0xFFE0E0E0),
        animationSpec = tween(300),
        label = "radio_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) SecondaryPurple else Color(0xFF979797),
        animationSpec = tween(300),
        label = "text_color"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 32.dp)
            ) { onSelected() }
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        // Radio Button
        Box(
            modifier = Modifier
                .size(20.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            // Outer circle
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Transparent, CircleShape)
                    .then(
                        Modifier.background(
                            color = if (isSelected) Color.Transparent else Color.Transparent,
                            shape = CircleShape
                        )
                    )
            ) {
                // Border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .background(radioColor, CircleShape)
                )

                // Inner filled circle when selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .background(SecondaryPurple, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Option Text
        Text(
            text = option,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}


// ========================================
// Preview Functions - Submit Button States
// ========================================

// Preview showing last question with all answers filled and submit button enabled
@Preview(showBackground = true, name = "Last Question - All Answered", heightDp = 900)
@Composable
fun LastQuestionAllAnsweredPreview() {
    val mockQuestions = listOf(
        QuizQuestion(id = 1, text = "Question 1", options = emptyList()),
        QuizQuestion(id = 2, text = "Question 2", options = emptyList()),
        QuizQuestion(id = 3, text = "Question 3", options = emptyList()),
        QuizQuestion(id = 4, text = "Question 4", options = emptyList()),
        QuizQuestion(id = 5, text = "Question 5", options = emptyList()),
        QuizQuestion(id = 6, text = "Question 6", options = emptyList()),
        QuizQuestion(id = 7, text = "Question 7", options = emptyList()),
        QuizQuestion(id = 8, text = "Question 8", options = emptyList()),
        QuizQuestion(id = 9, text = "Question 9", options = emptyList()),
        QuizQuestion(
            id = 10,
            text = "Dalam sebulan terakhir, seberapa sering Anda merasa cemas, tegang, atau stres saat menghadapi kondisi anggota keluarga yang mengalami gangguan jiwa",
            options = emptyList()
        )
    )

    // All questions answered
    var selectedAnswers by remember {
        mutableStateOf(
            mapOf(
                1 to "2",
                2 to "3",
                3 to "1",
                4 to "4",
                5 to "2",
                6 to "3",
                7 to "1",
                8 to "2",
                9 to "3",
                10 to "3" // Last question answered
            )
        )
    }

    val totalQuestions = mockQuestions.size
    val answeredQuestions = selectedAnswers.size
    val allQuestionsAnswered = selectedAnswers.size == totalQuestions

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Bar
            StressQuizNavigationBar(
                title = "Kuisioner untuk pengasuh",
                onBackClick = { }
            )

            // Scrollable content - showing only last question
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
            ) {
                item {
                    StressQuestionCard(
                        questionNumber = 20,
                        questionText = mockQuestions[9].text,
                        selectedValue = selectedAnswers[10],
                        onValueSelected = { value ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(10, value)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Bottom section with submit button - fixed position above bottom navigation
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 57.dp), // Height of bottom navigation
            color = White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // No unanswered questions
                Spacer(modifier = Modifier.height(2.dp))

                // Submit button - fully enabled with full opacity
                Button(
                    onClick = { /* Submit action */ },
                    modifier = Modifier
                        .width(165.dp)
                        .height(32.dp)
                        .alpha(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryPurple
                    ),
                    shape = RoundedCornerShape(15.dp),
                    enabled = true
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = 1,
            onItemSelected = { },
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = rememberNavController()
        )
    }
}

// Preview showing partial answers with disabled submit button
@Preview(showBackground = true, name = "Last Question - Partial Answers", heightDp = 900)
@Composable
fun LastQuestionPartialAnswersPreview() {
    val mockQuestions = listOf(
        QuizQuestion(id = 1, text = "Question 1", options = emptyList()),
        QuizQuestion(id = 2, text = "Question 2", options = emptyList()),
        QuizQuestion(id = 3, text = "Question 3", options = emptyList()),
        QuizQuestion(id = 4, text = "Question 4", options = emptyList()),
        QuizQuestion(id = 5, text = "Question 5", options = emptyList()),
        QuizQuestion(id = 6, text = "Question 6", options = emptyList()),
        QuizQuestion(id = 7, text = "Question 7", options = emptyList()),
        QuizQuestion(id = 8, text = "Question 8", options = emptyList()),
        QuizQuestion(id = 9, text = "Question 9", options = emptyList()),
        QuizQuestion(
            id = 10,
            text = "Dalam sebulan terakhir, seberapa sering Anda merasa cemas, tegang, atau stres saat menghadapi kondisi anggota keluarga yang mengalami gangguan jiwa",
            options = emptyList()
        )
    )

    // Only 5 questions answered (5 unanswered)
    var selectedAnswers by remember {
        mutableStateOf(
            mapOf(
                1 to "2",
                2 to "3",
                3 to "1",
                4 to "4",
                5 to "2",
                6 to "3",
                7 to "1",
                8 to "2",
                9  to "1"
            )
        )
    }

    val totalQuestions = mockQuestions.size
    val answeredQuestions = selectedAnswers.size
    val unansweredCount = totalQuestions - answeredQuestions
    val allQuestionsAnswered = false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Bar
            StressQuizNavigationBar(
                title = "Kuisioner untuk pengasuh",
                onBackClick = { }
            )

            // Scrollable content - showing only last question
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
            ) {
                item {
                    StressQuestionCard(
                        questionNumber = 20,
                        questionText = mockQuestions[9].text,
                        selectedValue = null, // Last question not answered
                        onValueSelected = { value ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(10, value)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Bottom section with unanswered count and disabled submit button - fixed position above bottom navigation
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 57.dp), // Height of bottom navigation
            color = White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show unanswered questions count
                Text(
                    text = "$unansweredCount dari $totalQuestions pertanyaan belum dijawab",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF343434),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Submit button - disabled with reduced opacity
                Button(
                    onClick = { /* Submit action disabled */ },
                    modifier = Modifier
                        .width(165.dp)
                        .height(32.dp)
                        .alpha(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    enabled = false
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF898989)
                    )
                }
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = 1,
            onItemSelected = { },
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = rememberNavController()
        )
    }
}
