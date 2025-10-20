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
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.data.QuizData
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.SecondaryPurple
import com.siagajiwa.siagajiwaid.ui.theme.White

@Composable
fun QuizScreen(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Search tab selected
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, String>()) }
    var currentPageIndex by remember { mutableIntStateOf(0) }

    val pages = QuizData.pages
    val currentPage = pages[currentPageIndex]
    val totalPages = pages.size
    val totalQuestions = pages.sumOf { it.questions.size }
    val answeredQuestions = selectedAnswers.size
    val progress = answeredQuestions.toFloat() / totalQuestions
    val isLastPage = currentPageIndex == totalPages - 1

    // Check if all questions on current page are answered
    val currentPageAnswered = currentPage.questions.all { question ->
        selectedAnswers.containsKey(question.id)
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
            Spacer(modifier = Modifier.height(44.dp))

            // Navigation Bar
            QuizNavigationBar(
                title = "Quiz Untuk Pengasuh",
                onBackClick = { navController.popBackStack() }
            )

            // Content with page transition animation
            AnimatedContent(
                targetState = currentPageIndex,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { if (targetState > initialState) 300 else -300 }
                            ) togetherWith
                            fadeOut(animationSpec = tween(300)) +
                            slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { if (targetState > initialState) -300 else 300 }
                            )
                },
                label = "page_transition",
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val page = pages[pageIndex]
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    items(page.questions.size) { index ->
                        val question = page.questions[index]
                        QuizQuestionCard(
                            questionNumber = question.id,
                            questionText = question.text,
                            options = question.options,
                            selectedAnswer = selectedAnswers[question.id],
                            onAnswerSelected = { answer ->
                                selectedAnswers = selectedAnswers.toMutableMap().apply {
                                    put(question.id, answer)
                                }
                            }
                        )

                        if (index < page.questions.size - 1) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
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

            // Page Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(totalPages) { index ->
                    PageIndicatorDot(
                        isActive = index == currentPageIndex,
                        isCompleted = pages[index].questions.all { selectedAnswers.containsKey(it.id) },
                        onClick = { currentPageIndex = index }
                    )
                    if (index < totalPages - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Navigation Controls
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Show Submit button when all questions are answered
                if (answeredQuestions == totalQuestions) {
                    Button(
                        onClick = {
                            // Navigate to Quiz Result Screen
                            navController.navigate("QuizResultScreen")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
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

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Navigation arrows (always show unless on appropriate edge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    Button(
                        onClick = {
                            if (currentPageIndex > 0) {
                                currentPageIndex--
                            }
                        },
                        enabled = currentPageIndex > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F5F5),
                            disabledContainerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Previous",
                            modifier = Modifier.size(20.dp),
                            tint = if (currentPageIndex > 0) DarkLight else Color(0xFFCACACA)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kembali",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentPageIndex > 0) DarkLight else Color(0xFFCACACA)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Page Counter
                    Text(
                        text = "${currentPageIndex + 1}/$totalPages",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryPurple
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Next Button
                    Button(
                        onClick = {
                            if (currentPageIndex < totalPages - 1) {
                                currentPageIndex++
                            }
                        },
                        enabled = currentPageIndex < totalPages - 1,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentPageAnswered) SecondaryPurple else Color(0xFFF5F5F5),
                            disabledContainerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Selanjutnya",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentPageAnswered) White else Color(0xFF979797)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Next",
                            modifier = Modifier.size(20.dp),
                            tint = if (currentPageAnswered) White else Color(0xFF979797)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = selectedTabIndex,
            onItemSelected = { selectedTabIndex = it },
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
fun QuizNavigationBar(
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

// Preview for Page 1 - First page
@Preview(showBackground = true, name = "Quiz - Page 1")
@Composable
fun QuizScreenPage1Preview() {
    QuizScreen(navController = rememberNavController())
}

// Preview for Page 3 - Middle page
@Preview(showBackground = true, name = "Quiz - Page 3 (Middle)")
@Composable
fun QuizScreenPage3Preview() {
    QuizScreen(navController = rememberNavController())
}

// Preview for Page 5 - Last page with Submit button
@Preview(showBackground = true, name = "Quiz - Page 5 (Submit)")
@Composable
fun QuizScreenPage5Preview() {
    QuizScreen(navController = rememberNavController())
}

// Preview with progress - some questions answered
@Preview(showBackground = true, name = "Quiz - With Progress")
@Composable
fun QuizScreenProgressPreview() {
    QuizScreen(navController = rememberNavController())
}

// Preview with all questions answered - Submit enabled
@Preview(showBackground = true, name = "Quiz - All Answered")
@Composable
fun QuizScreenAllAnsweredPreview() {
    QuizScreen(navController = rememberNavController())
}
