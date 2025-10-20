package com.siagajiwa.siagajiwaid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.PurpleDark
import com.siagajiwa.siagajiwaid.ui.theme.White

@Composable
fun QuizResultScreen(
    navController: NavHostController,
    correctAnswers: Int = 18,
    totalQuestions: Int = 24
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }

    // Calculate percentage
    val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()

    // Determine performance level
    val performanceLevel = when {
        percentage >= 80 -> "Sangat Baik"
        percentage >= 60 -> "Cukup"
        percentage >= 40 -> "Kurang"
        else -> "Perlu Belajar"
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

            // Title
            Text(
                text = "Hasil Kuis Pengetahuan Umum",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkLight,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Performance Level
            Text(
                text = performanceLevel,
                fontSize = 55.sp,
                fontWeight = FontWeight.SemiBold,
                color = PurpleDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Result Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Percentage
                    Text(
                        text = "$percentage%",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PurpleDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Details
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = PurpleDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("$correctAnswers ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = DarkLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("dari")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = PurpleDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(" $totalQuestions ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = DarkLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("soal dijawab dengan")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = PurpleDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(" benar")
                            }
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Ulangi Tes Button (Outlined)
            OutlinedButton(
                onClick = {
                    // TODO: Navigate to quiz screen to restart
                    navController.navigate("QuizScreen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White,
                    contentColor = PurpleDark
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Ulangi Tes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simpan Button (Filled)
            Button(
                onClick = {
                    // TODO: Save result and navigate back
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDark
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Simpan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = White
                )
            }

            Spacer(modifier = Modifier.weight(1f))
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

@Preview(showBackground = true)
@Composable
fun QuizResultScreenPreview() {
    QuizResultScreen(
        navController = rememberNavController(),
        correctAnswers = 18,
        totalQuestions = 24
    )
}

@Preview(showBackground = true)
@Composable
fun QuizResultScreenHighScorePreview() {
    QuizResultScreen(
        navController = rememberNavController(),
        correctAnswers = 22,
        totalQuestions = 24
    )
}

@Preview(showBackground = true)
@Composable
fun QuizResultScreenLowScorePreview() {
    QuizResultScreen(
        navController = rememberNavController(),
        correctAnswers = 10,
        totalQuestions = 24
    )
}
