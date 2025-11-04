package com.siagajiwa.siagajiwa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.White
import com.siagajiwa.siagajiwa.utils.QuizCalculator
import com.siagajiwa.siagajiwa.viewmodel.KnowledgeQuizViewModel
import com.siagajiwa.siagajiwa.viewmodel.KnowledgeQuizUiState
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.*
import android.util.Log

// Color definitions from Figma
private val NeutralGray1 = Color(0xFF343434) // #343434
private val NeutralGray2 = Color(0xFF898989) // #898989
private val NeutralGray4 = Color(0xFFCACACA) // #CACACA
private val NeutralWhite = Color(0xFFFFFFFF) // #FFFFFF
private val PrimaryPurple = Color(0xFF3629B7) // #3629B7

@Composable
fun PatientKnowledgeQuizScreen(
    navController: NavHostController,
    viewModel: KnowledgeQuizViewModel = viewModel(),
    userViewModel: com.siagajiwa.siagajiwa.viewmodel.UserViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    val quizState by viewModel.quizState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    // Check auth state when screen loads - redirect to login if not authenticated
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Small delay to let session load
        val currentUser = com.siagajiwa.siagajiwa.data.SupabaseClient.auth.currentUserOrNull()
        Log.d("WawasanTestScreen", "Screen loaded - Auth state check:")
        Log.d("WawasanTestScreen", "  From SupabaseClient - User ID: ${currentUser?.id}")
        Log.d("WawasanTestScreen", "  From SupabaseClient - User Email: ${currentUser?.email}")
        Log.d("WawasanTestScreen", "  From UserViewModel - User: ${userState.user?.email}")
        Log.d("WawasanTestScreen", "  From UserViewModel - Is Logged In: ${userState.isLoggedIn}")

        // Redirect to login if user is not authenticated
        if (currentUser == null && userState.user == null) {
            Log.e("WawasanTestScreen", "  WARNING: User is NOT logged in - redirecting to login")
            navController.navigate("LoginScreen") {
                popUpTo("PatientKnowledgeQuizScreen") { inclusive = true }
            }
        } else if (currentUser == null && userState.user != null) {
            Log.w("WawasanTestScreen", "  NOTE: User in ViewModel but not in SupabaseClient - session timing issue")
        } else {
            Log.d("WawasanTestScreen", "  User is logged in successfully")
        }
    }

    // Load knowledge quiz when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadQuiz()
    }

    when (val state = quizState) {
        is KnowledgeQuizUiState.Loading -> {
            LoadingContent()
        }
        is KnowledgeQuizUiState.Success -> {
            val questions = state.questions
            QuizContent(
                navController = navController,
                viewModel = viewModel,
                questions = questions,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                userId = userState.user?.id  // Pass userId to QuizContent
            )
        }
        is KnowledgeQuizUiState.Error -> {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = PrimaryPurple,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Memuat pertanyaan quiz...",
                fontSize = 14.sp,
                color = DarkLight,
                fontWeight = FontWeight.Medium
            )
        }
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
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Oops!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gagal memuat quiz",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = DarkLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = NeutralGray2
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
            ) {
                Text(
                    text = "Coba Lagi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
            }
        }
    }
}

@Composable
private fun QuizContent(
    navController: NavHostController,
    viewModel: KnowledgeQuizViewModel,
    questions: List<com.siagajiwa.siagajiwa.data.QuizQuestion>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    userId: String?  // Add userId parameter
) {
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, String>()) }

    val totalQuestions = questions.size
    val answeredQuestions = selectedAnswers.size

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
            .background(Color(0xFFF5F5F5)) // Light gray background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Bar
            QuizNavigationBar(
                title = "Kuis Pengetahuan Umum",
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
                    QuestionCardItem(
                        questionNumber = index + 1,
                        questionText = question.text,
                        options = question.options,
                        selectedAnswer = selectedAnswers[question.id],
                        onAnswerSelected = { answer ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(question.id, answer)
                            }
                        }
                    )

                    if (index < questions.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
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
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Submit button with opacity based on completion
                Button(
                    onClick = {
                        if (allQuestionsAnswered) {
                            // Calculate quiz result using QuizCalculator
                            val result = QuizCalculator.calculateQuizResult(selectedAnswers, questions)

                            // Log the results for debugging
                            Log.d("KnowledgeQuiz", "Correct Answers: ${result.correctAnswers}")
                            Log.d("KnowledgeQuiz", "Total Questions: ${result.totalQuestions}")
                            Log.d("KnowledgeQuiz", "Percentage: ${result.percentage}%")
                            Log.d("KnowledgeQuiz", "Knowledge Level: ${result.knowledgeLevel}")

                            // Store quiz data and submit to database immediately
                            viewModel.storeQuizData(
                                userId = userId,
                                answers = selectedAnswers,
                                questions = questions
                            )

                            // Submit to database
                            viewModel.submitStoredQuiz(
                                onSuccess = {
                                    Log.d("WawasanTestScreen", "Quiz result saved successfully")
                                    // Navigate to result screen after successful save
                                    navController.navigate("QuizResultScreen/${result.correctAnswers}/${result.totalQuestions}")
                                },
                                onError = { error ->
                                    Log.e("WawasanTestScreen", "Failed to save quiz result: $error")
                                    // Still navigate to result screen even if save fails
                                    navController.navigate("QuizResultScreen/${result.correctAnswers}/${result.totalQuestions}")
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .width(165.dp)
                        .height(32.dp)
                        .alpha(if (allQuestionsAnswered) 1f else 0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allQuestionsAnswered) PrimaryPurple else Color(0xFFE0E0E0)
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
                        .background(PrimaryPurple)
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
private fun QuizNavigationBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
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
                tint = NeutralGray1,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = NeutralGray1
        )
    }
}

@Composable
private fun QuestionCardItem(
    questionNumber: Int,
    questionText: String,
    options: List<String>,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Question text
            Text(
                text = "$questionNumber. $questionText",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NeutralGray1,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            options.forEach { option ->
                OptionItem(
                    text = option,
                    isSelected = selectedAnswer == option,
                    onClick = { onAnswerSelected(option) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun OptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio button circle
        Box(
            modifier = Modifier
                .size(13.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) PrimaryPurple else NeutralGray4,
                    shape = CircleShape
                )
                .background(if (isSelected) PrimaryPurple else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Selected",
                    tint = White,
                    modifier = Modifier.size(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Option text
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = NeutralGray2,
            lineHeight = 16.sp
        )
    }
}
