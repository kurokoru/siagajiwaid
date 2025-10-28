package com.siagajiwa.siagajiwaid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.SecondaryPurple
import com.siagajiwa.siagajiwaid.ui.theme.White
import com.siagajiwa.siagajiwaid.viewmodel.KnowledgeQuizViewModel
import com.siagajiwa.siagajiwaid.viewmodel.KnowledgeQuizUiState

// Color definitions from Figma
private val NeutralGray1 = Color(0xFF343434) // #343434
private val NeutralGray2 = Color(0xFF898989) // #898989
private val NeutralGray4 = Color(0xFFCACACA) // #CACACA
private val NeutralWhite = Color(0xFFFFFFFF) // #FFFFFF
private val PrimaryPurple = Color(0xFF3629B7) // #3629B7

@Composable
fun PatientKnowledgeQuizScreen(
    navController: NavHostController,
    viewModel: KnowledgeQuizViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    val quizState by viewModel.quizState.collectAsState()

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
                onTabSelected = { selectedTabIndex = it }
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
    questions: List<com.siagajiwa.siagajiwaid.data.QuizQuestion>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, String>()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSubmitError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var quizResult by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val totalQuestions = questions.size
    val answeredQuestions = selectedAnswers.size

    // Check if all questions are answered
    val allQuestionsAnswered = selectedAnswers.size == totalQuestions

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

                // Submit button - shown when all questions are answered
                item {
                    if (allQuestionsAnswered) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                isSubmitting = true
                                viewModel.submitKnowledgeQuiz(
                                    answers = selectedAnswers,
                                    questions = questions,
                                    onSuccess = { correct, total ->
                                        isSubmitting = false
                                        quizResult = Pair(correct, total)
                                        showSuccessDialog = true
                                    },
                                    onError = { error ->
                                        isSubmitting = false
                                        showSubmitError = error
                                    }
                                )
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryPurple
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Submit Quiz",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = White
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Question counter at bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                IconButton(
                    onClick = { /* Handle previous */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "Previous",
                        tint = NeutralGray4,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Current question / Total questions
                Text(
                    text = "1/${totalQuestions}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryPurple
                )

                // Next button
                IconButton(
                    onClick = { /* Handle next */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = "Next",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Show error message if submission fails
            if (showSubmitError != null) {
                Text(
                    text = showSubmitError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 8.dp)
                )
            }

            // Success Dialog
            if (showSuccessDialog && quizResult != null) {
                AlertDialog(
                    onDismissRequest = { },
                    title = {
                        Text(
                            text = "Quiz Selesai!",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "Skor Anda: ${quizResult!!.first}/${quizResult!!.second}\n" +
                                    "Persentase: ${(quizResult!!.first * 100) / quizResult!!.second}%"
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                navController.navigate("QuizResultScreen/${quizResult!!.first}/${quizResult!!.second}")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryPurple
                            )
                        ) {
                            Text("Lihat Hasil")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showSuccessDialog = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("Tutup")
                        }
                    }
                )
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = selectedTabIndex,
            onItemSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
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
            .height(53.dp)
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
