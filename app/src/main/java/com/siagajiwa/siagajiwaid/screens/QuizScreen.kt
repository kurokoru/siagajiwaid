package com.siagajiwa.siagajiwaid.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.data.QuizQuestion
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.SecondaryPurple
import com.siagajiwa.siagajiwaid.ui.theme.White
import com.siagajiwa.siagajiwaid.viewmodel.StressQuizViewModel
import com.siagajiwa.siagajiwaid.viewmodel.StressQuizUiState

@Composable
fun QuizScreen(
    navController: NavHostController,
    viewModel: StressQuizViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    val quizState by viewModel.quizState.collectAsState()

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
                title = "Tes Tingkat Stres",
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
                    StressQuestionCard(
                        questionNumber = index + 1,
                        questionText = question.text,
                        selectedValue = selectedAnswers[question.id],
                        onValueSelected = { value ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(question.id, value)
                            }
                        }
                    )

                    if (index < questions.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Submit button - shown when all questions are answered
                item {
                    if (allQuestionsAnswered) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.submitStressQuiz(
                                    answers = selectedAnswers,
                                    questions = questions,
                                    onSuccess = { score, maxScore ->
                                        navController.navigate("QuizResultScreen/$score/$maxScore")
                                    },
                                    onError = { error ->
                                        // Show error
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryPurple
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Submit Quiz",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = White
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkLight
                    )
                    Text(
                        text = "$answeredQuestions/$totalQuestions pertanyaan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF979797)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SecondaryPurple,
                    trackColor = Color(0xFFE0E0E0),
                )
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = selectedTabIndex,
            onItemSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Home Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Color(0xFFCACACA), RoundedCornerShape(100.dp))
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
            .height(56.dp)
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
    // Stress assessment options with values 0-4
    val stressOptions = listOf(
        "0" to "Tidak Pernah",
        "1" to "Hampir Tidak Pernah",
        "2" to "Kadang-Kadang",
        "3" to "Cukup Sering",
        "4" to "Terlalu Sering"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkLight,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stress rating options
            stressOptions.forEach { (value, label) ->
                StressOptionItem(
                    value = value,
                    label = label,
                    isSelected = selectedValue == value,
                    onClick = { onValueSelected(value) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StressOptionItem(
    value: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) SecondaryPurple.copy(alpha = 0.1f)
                else Color(0xFFF5F5F5)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio button circle
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) SecondaryPurple else Color.Transparent
                )
                .then(
                    if (!isSelected) Modifier.then(
                        Modifier.clip(CircleShape).background(Color.Transparent)
                            .then(Modifier.clip(CircleShape))
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .then(
                            Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(White)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Value indicator
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) SecondaryPurple else DarkLight,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Label text
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) SecondaryPurple else DarkLight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuizQuestionCard(
    questionNumber: Int,
    questionText: String,
    options: List<String>,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    val isAnswered = selectedAnswer != null
    val borderColor by animateColorAsState(
        targetValue = if (isAnswered) SecondaryPurple else Color.Transparent,
        animationSpec = tween(300),
        label = "border_color"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$questionNumber.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = questionText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options
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
        }
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

@Composable
fun PageIndicatorDot(
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val size by animateDpAsState(
        targetValue = if (isActive) 12.dp else 8.dp,
        animationSpec = tween(300),
        label = "dot_size"
    )

    val color by animateColorAsState(
        targetValue = when {
            isCompleted -> SecondaryPurple
            isActive -> SecondaryPurple.copy(alpha = 0.7f)
            else -> Color(0xFFE0E0E0)
        },
        animationSpec = tween(300),
        label = "dot_color"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 12.dp)
            ) { onClick() }
    )
}

// ========================================
// Preview Functions for Stress Quiz
// ========================================

// Preview for Stress Question Card - No selection
@Preview(showBackground = true, name = "Stress Question - Unselected")
@Composable
fun StressQuestionCardPreview() {
    StressQuestionCard(
        questionNumber = 1,
        questionText = "Seberapa sering Anda merasa gugup dan stres?",
        selectedValue = null,
        onValueSelected = {}
    )
}

// Preview for Stress Question Card - With selection
@Preview(showBackground = true, name = "Stress Question - Selected (2)")
@Composable
fun StressQuestionCardSelectedPreview() {
    StressQuestionCard(
        questionNumber = 2,
        questionText = "Seberapa sering Anda mendapati diri Anda tidak mampu menghentikan atau mengendalikan kekhawatiran?",
        selectedValue = "2",
        onValueSelected = {}
    )
}

// Preview for Stress Question Card - High stress selected
@Preview(showBackground = true, name = "Stress Question - High Stress (4)")
@Composable
fun StressQuestionCardHighStressPreview() {
    StressQuestionCard(
        questionNumber = 3,
        questionText = "Seberapa sering Anda merasa kesulitan untuk rileks?",
        selectedValue = "4",
        onValueSelected = {}
    )
}

// Preview for Stress Option Item - Not selected
@Preview(showBackground = true, name = "Stress Option - Unselected")
@Composable
fun StressOptionItemPreview() {
    StressOptionItem(
        value = "2",
        label = "Kadang-Kadang",
        isSelected = false,
        onClick = {}
    )
}

// Preview for Stress Option Item - Selected
@Preview(showBackground = true, name = "Stress Option - Selected")
@Composable
fun StressOptionItemSelectedPreview() {
    StressOptionItem(
        value = "3",
        label = "Cukup Sering",
        isSelected = true,
        onClick = {}
    )
}

// Preview for all stress options
@Preview(showBackground = true, name = "All Stress Options")
@Composable
fun AllStressOptionsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pilih tingkat stres Anda:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkLight
        )

        StressOptionItem(value = "0", label = "Tidak Pernah", isSelected = false, onClick = {})
        StressOptionItem(value = "1", label = "Hampir Tidak Pernah", isSelected = false, onClick = {})
        StressOptionItem(value = "2", label = "Kadang-Kadang", isSelected = true, onClick = {})
        StressOptionItem(value = "3", label = "Cukup Sering", isSelected = false, onClick = {})
        StressOptionItem(value = "4", label = "Terlalu Sering", isSelected = false, onClick = {})
    }
}

// Preview for complete stress quiz screen
@Preview(showBackground = true, name = "Stress Quiz Screen")
@Composable
fun StressQuizScreenPreview() {
    QuizScreen(navController = rememberNavController())
}

// Preview with mock data - showing actual stress questions
@Preview(showBackground = true, name = "Stress Quiz - Mock Data", heightDp = 2000)
@Composable
fun StressQuizMockDataPreview() {
    val mockQuestions = listOf(
        QuizQuestion(
            id = 1,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa bingung atau panik karena ada hal yang terjadi tiba-tiba saat merawat anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 2,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa tidak bisa mengatur atau mengendalikan hal-hal penting saat merawat anggota keluarga yang mengalami gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 3,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa cemas, tegang, atau stres saat menghadapi kondisi anggota yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 4,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa percaya diri dan yakin bisa mengatasi masalah yang muncul saat merawat anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 5,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa semua berjalan dengan baik dalam perawatan anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 6,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa kewalahan karena terlalu banyak hal yang harus dilakukan dalam perawatan anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 7,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa bisa mengendalikan situasi sulit atau menyakitkan yang berkaitan dengan anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 8,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa bahagia dan puas dengan usaha Anda dalam merawat anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 9,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa kesal atau marah karena ada hal di luar kendali Anda yang memengaruhi perawatan anggota keluarga yang gangguan jiwa?",
            options = emptyList()
        ),
        QuizQuestion(
            id = 10,
            text = "Dalam 1 bulan terakhir, seberapa sering Anda merasa merawat anggota keluarga yang mengalami gangguan jiwa terlalu banyak beban sehingga Anda kewalahan?",
            options = emptyList()
        )
    )

    var selectedAnswers by remember { mutableStateOf(mapOf<Int, String>(
        1 to "2",  // Question 1 - Kadang-Kadang
        3 to "3",  // Question 3 - Cukup Sering
        6 to "4"   // Question 6 - Terlalu Sering
    )) }

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
                title = "Tes Tingkat Stres",
                onBackClick = { }
            )

            // Scrollable content with all questions
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
            ) {
                items(mockQuestions.size) { index ->
                    val question = mockQuestions[index]
                    StressQuestionCard(
                        questionNumber = index + 1,
                        questionText = question.text,
                        selectedValue = selectedAnswers[question.id],
                        onValueSelected = { value ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(question.id, value)
                            }
                        }
                    )

                    if (index < mockQuestions.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkLight
                    )
                    Text(
                        text = "${selectedAnswers.size}/10 pertanyaan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF979797)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = selectedAnswers.size.toFloat() / 10f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SecondaryPurple,
                    trackColor = Color(0xFFE0E0E0),
                )
            }
        }
    }
}
