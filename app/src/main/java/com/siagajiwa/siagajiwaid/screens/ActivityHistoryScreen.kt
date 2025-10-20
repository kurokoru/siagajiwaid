package com.siagajiwa.siagajiwaid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.PurpleDark
import com.siagajiwa.siagajiwaid.ui.theme.White

// Data classes for history items
data class StressHistoryItem(
    val date: String,
    val time: String,
    val level: String,
    val levelColor: Color,
    val iconRes: Int
)

data class QuizHistoryItem(
    val date: String,
    val time: String,
    val result: String,
    val percentage: Int,
    val resultColor: Color
)

@Composable
fun ActivityHistoryScreen(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(1) } // Bottom nav (Riwayat)
    var selectedHistoryTab by remember { mutableIntStateOf(0) } // 0 = Tingkat Stres, 1 = Skor Wawasan

    // Sample data for stress history
    val stressHistory = listOf(
        StressHistoryItem("30/10/2019", "08:30", "Tinggi", Color(0xFFFF4267), R.drawable.tinggi),
        StressHistoryItem("30/10/2019", "04:27", "Rendah", Color(0xFF27AE60), R.drawable.rendah),
        StressHistoryItem("20/10/2019", "18:01", "Sedang", Color(0xFFFFAF2A), R.drawable.sedang),
        StressHistoryItem("20/10/2019", "18:01", "Sedang", Color(0xFFFFAF2A), R.drawable.sedang),
        StressHistoryItem("20/10/2019", "18:01", "Sedang", Color(0xFFFFAF2A), R.drawable.sedang),
        StressHistoryItem("20/10/2019", "18:01", "Sedang", Color(0xFFFFAF2A), R.drawable.sedang),
    )

    // Sample data for quiz history
    val quizHistory = listOf(
        QuizHistoryItem("30/10/2019", "08:30", "Cukup", 75, PurpleDark),
        QuizHistoryItem("30/10/2019", "08:30", "Cukup", 75, PurpleDark),
        QuizHistoryItem("30/10/2019", "08:30", "Cukup", 75, PurpleDark),
        QuizHistoryItem("30/10/2019", "08:30", "Cukup", 75, PurpleDark),
        QuizHistoryItem("30/10/2019", "08:30", "Cukup", 75, PurpleDark),
        QuizHistoryItem("30/10/2019", "08:30", "Cukup", 75, PurpleDark),
        QuizHistoryItem("30/10/2019", "04:27", "Kurang", 25, PurpleDark),
        QuizHistoryItem("20/10/2019", "18:01", "Kurang", 50, PurpleDark)
    )

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
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
                    text = "Riwayat Aktifitas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Tingkat Stres Tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedHistoryTab = 0 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tingkat Stres",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                if (selectedHistoryTab == 0) Color(0xFFFFAF2A) else Color(0xFFE0E0E0),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Skor Wawasan Tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedHistoryTab = 1 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Skor Wawasan",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                if (selectedHistoryTab == 1) Color(0xFFFFAF2A) else Color(0xFFE0E0E0),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on selected tab
            if (selectedHistoryTab == 0) {
                // Stress History List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(stressHistory) { item ->
                        StressHistoryCard(item)
                    }
                }
            } else {
                // Quiz History List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(quizHistory) { item ->
                        QuizHistoryCard(item)
                    }
                }
            }

            Spacer(modifier = Modifier.height(92.dp))
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
fun StressHistoryCard(item: StressHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Date and Stress Level
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Tanggal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF979797)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.time,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF979797)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tingkat stres",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF979797)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.level,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = item.levelColor
                )
            }

            // Right side - Emoticon
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = "Stress Level Icon",
                modifier = Modifier.size(70.dp)
            )
        }
    }
}

@Composable
fun QuizHistoryCard(item: QuizHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Date and Result
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Tanggal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF979797)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.time,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF979797)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hasil Tes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF979797)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.result,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = item.resultColor
                )
            }

            // Right side - Percentage
            Text(
                text = "${item.percentage}%",
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold,
                color = PurpleDark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityHistoryScreenPreview() {
    ActivityHistoryScreen(navController = rememberNavController())
}

