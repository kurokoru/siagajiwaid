package com.siagajiwa.siagajiwa.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.data.QuestionnaireData
import com.siagajiwa.siagajiwa.ui.theme.White
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.SecondaryPurple

@Composable
fun QuestionnaireScreen(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Search tab selected
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }

    // Flatten all questions from all pages into a single list
    val allQuestions = QuestionnaireData.pages.flatMap { it.questions }
    val totalQuestions = allQuestions.size
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
            NavigationBar(
                title = "Kuisioner untuk pengasuh",
                onBackClick = { navController.popBackStack() }
            )

            // Scrollable content with all questions
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
            ) {
                items(allQuestions.size) { index ->
                    val question = allQuestions[index]
                    QuestionCard(
                        questionNumber = question.id,
                        questionText = question.text,
                        selectedAnswer = selectedAnswers[question.id] ?: -1,
                        onAnswerSelected = { answer ->
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(question.id, answer)
                            }
                        }
                    )

                    if (index < allQuestions.size - 1) {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Submit button - shown when all questions are answered
                item {
                    if (allQuestionsAnswered) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                // Navigate to Stress Test Result Screen
                                navController.navigate("StressTestResultScreen")
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
                                text = "Submit Kuisioner",
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
            onItemSelected = { selectedTabIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

@Composable
fun NavigationBar(
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
                tint = Color(0xFF343434),
                modifier =Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF343434),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuestionCard(
    questionNumber: Int,
    questionText: String,
    selectedAnswer: Int,
    onAnswerSelected: (Int) -> Unit
) {
    val isAnswered = selectedAnswer >= 0
    val borderColor by animateColorAsState(
        targetValue = if (isAnswered) SecondaryPurple else Color.Transparent,
        animationSpec = tween(300),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isAnswered) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(15.dp))
                } else {
                    Modifier
                }
            ),
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkLight,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = questionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkLight,
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Rating Scale Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating Scale
            RatingScale(
                selectedAnswer = selectedAnswer,
                onAnswerSelected = onAnswerSelected
            )
        }
    }
}

@Composable
fun RatingScale(
    selectedAnswer: Int,
    onAnswerSelected: (Int) -> Unit
) {
    val options = QuestionnaireData.ratingOptions

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEachIndexed { index, option ->
            RatingOption(
                index = index,
                option = option,
                isSelected = selectedAnswer == index,
                onSelected = { onAnswerSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RatingOption(
    index: Int,
    option: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "option_scale"
    )

    val color by animateColorAsState(
        targetValue = if (isSelected) SecondaryPurple else Color(0xFFCACACA),
        animationSpec = tween(300),
        label = "option_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) SecondaryPurple else Color(0xFF979797),
        animationSpec = tween(300),
        label = "text_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 24.dp)
            ) { onSelected() }
            .padding(vertical = 4.dp)
    ) {
        // Radio Button with animation
        Box(
            modifier = Modifier
                .size(13.dp)
                .scale(scale)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Option Text
        Text(
            text = option,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionnaireScreenPreview() {
    QuestionnaireScreen(navController = rememberNavController())
}