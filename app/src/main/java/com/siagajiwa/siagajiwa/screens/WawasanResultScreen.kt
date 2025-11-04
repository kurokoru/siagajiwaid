package com.siagajiwa.siagajiwa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.PurpleDark
import com.siagajiwa.siagajiwa.ui.theme.White
import com.siagajiwa.siagajiwa.viewmodel.KnowledgeQuizViewModel
import com.siagajiwa.siagajiwa.utils.KnowledgeLevel

@Composable
fun QuizResultScreen(
    navController: NavHostController,
    correctAnswers: Int = 18,
    totalQuestions: Int = 24,
    viewModel: KnowledgeQuizViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }

    // Calculate percentage
    val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()

    // Determine knowledge level using the same logic as QuizCalculator
    val knowledgeLevel = when {
        percentage >= 76 -> KnowledgeLevel.BAIK
        percentage >= 56 -> KnowledgeLevel.CUKUP
        else -> KnowledgeLevel.KURANG
    }

    // Determine performance level
    val performanceLevel = knowledgeLevel.displayName

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 92.dp) // Space for bottom navigation
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(44.dp))

            // Navigation Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title
                Text(
                    text = "Hasil Kuis Pengetahuan Umum",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Performance Level
            Text(
                text = performanceLevel,
                fontSize = 55.sp,
                fontWeight = FontWeight.SemiBold,
                color = PurpleDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(37.dp))

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
                        .padding(vertical = 43.dp),
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

                    Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            // Access features section - only show for CUKUP and KURANG knowledge levels
            if (knowledgeLevel == KnowledgeLevel.CUKUP || knowledgeLevel == KnowledgeLevel.KURANG) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Silahkan mengakses fitur",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Perawatan Pasien",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = PurpleDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            navController.navigate("PatientCareScreen")
                        }
                    )

                    Spacer(modifier = Modifier.height(0.dp))

                    Text(
                        text = "Selayang Pandang Gangguan Jiwa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = PurpleDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            navController.navigate("SchizophreniaInsightScreen")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(27.dp))

            // Kembali Button (Filled)
            Button(
                onClick = {
                    // Navigate back to home
                    navController.navigate("HomeScreen") {
                        popUpTo("HomeScreen") { inclusive = false }
                    }
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
                    text = "Kembali",
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
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
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

// Simplified preview composable without navigation dependencies
@Composable
private fun QuizResultPreviewContent(
    correctAnswers: Int,
    totalQuestions: Int
) {
    // Calculate percentage
    val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()

    // Determine knowledge level
    val knowledgeLevel = when {
        percentage >= 76 -> KnowledgeLevel.BAIK
        percentage >= 56 -> KnowledgeLevel.CUKUP
        else -> KnowledgeLevel.KURANG
    }

    val performanceLevel = knowledgeLevel.displayName

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Title
            Text(
                text = "Hasil Kuis Pengetahuan Umum",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkLight
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Performance Level
            Text(
                text = performanceLevel,
                fontSize = 55.sp,
                fontWeight = FontWeight.SemiBold,
                color = PurpleDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(37.dp))

            // Result Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 43.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PurpleDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = PurpleDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                                append("$correctAnswers ")
                            }
                            withStyle(style = SpanStyle(color = DarkLight, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                                append("dari")
                            }
                            withStyle(style = SpanStyle(color = PurpleDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                                append(" $totalQuestions ")
                            }
                            withStyle(style = SpanStyle(color = DarkLight, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                                append("soal dijawab dengan")
                            }
                            withStyle(style = SpanStyle(color = PurpleDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
                                append(" benar")
                            }
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Conditional feature access section
            if (knowledgeLevel == KnowledgeLevel.CUKUP || knowledgeLevel == KnowledgeLevel.KURANG) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Silahkan mengakses fitur",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    Text(
                        text = "Perawatan Pasien",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = PurpleDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        textDecoration = TextDecoration.Underline
                    )

                    Text(
                        text = "Selayang Pandang Gangguan Jiwa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = PurpleDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } else {
                // Show placeholder for BAIK level to indicate no feature access
                Text(
                    text = "[No feature access shown for BAIK level]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(27.dp))

            // Kembali Button (Back to Home)
            Button(
                onClick = { /* Preview - no action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDark
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    text = "Kembali",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = White
                )
            }
        }
    }
}

// Preview for BAIK level (76-100%) - Should NOT show feature access section
@Preview(showBackground = true, name = "BAIK Level - No Feature Access", heightDp = 800)
@Composable
fun QuizResultScreenBaikPreview() {
    QuizResultPreviewContent(
        correctAnswers = 22,
        totalQuestions = 24  // 91.67% - BAIK
    )
}

// Preview for CUKUP level (56-76%) - Should show feature access section
@Preview(showBackground = true, name = "CUKUP Level - With Feature Access", heightDp = 800)
@Composable
fun QuizResultScreenCukupPreview() {
    QuizResultPreviewContent(
        correctAnswers = 16,
        totalQuestions = 24  // 66.67% - CUKUP
    )
}

// Preview for KURANG level (<56%) - Should show feature access section
@Preview(showBackground = true, name = "KURANG Level - With Feature Access", heightDp = 800)
@Composable
fun QuizResultScreenKurangPreview() {
    QuizResultPreviewContent(
        correctAnswers = 10,
        totalQuestions = 24  // 41.67% - KURANG
    )
}

// Edge case: Exactly 76% (boundary for BAIK) - Should NOT show feature access
@Preview(showBackground = true, name = "Edge: Exactly 76% - BAIK", heightDp = 800)
@Composable
fun QuizResultScreen76PercentPreview() {
    QuizResultPreviewContent(
        correctAnswers = 19,
        totalQuestions = 25  // 76% - BAIK
    )
}

// Edge case: Just below 76% (boundary for CUKUP) - Should show feature access
@Preview(showBackground = true, name = "Edge: 75% - CUKUP", heightDp = 800)
@Composable
fun QuizResultScreen75PercentPreview() {
    QuizResultPreviewContent(
        correctAnswers = 18,
        totalQuestions = 24  // 75% - CUKUP
    )
}
