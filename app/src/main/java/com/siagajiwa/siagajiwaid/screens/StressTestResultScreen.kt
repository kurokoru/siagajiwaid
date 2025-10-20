package com.siagajiwa.siagajiwaid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.withStyle
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

enum class StressLevel {
    RENDAH,
    SEDANG,
    TINGGI
}

@Composable
fun StressTestResultScreen(
    navController: NavHostController,
    stressLevel: StressLevel = StressLevel.RENDAH
) {
    var selectedTabIndex by remember { mutableIntStateOf(1) }

    // Determine the image, title, and color based on stress level
    val (imageRes, titleText, stressLevelText, stressLevelColor) = when (stressLevel) {
        StressLevel.RENDAH -> Quadruple(
            R.drawable.rendah,
            "Yeay..!!",
            "rendah",
            Color(0xFF4CAF50) // Green
        )
        StressLevel.SEDANG -> Quadruple(
            R.drawable.sedang,
            "Waduh..!!",
            "sedang",
            Color(0xFFFF9800) // Orange
        )
        StressLevel.TINGGI -> Quadruple(
            R.drawable.tinggi,
            "Waduh..!!",
            "tinggi",
            Color(0xFFE53935) // Red
        )
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
                    text = "Tingkat Stres Anda",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkLight,
                    modifier = Modifier.weight(1f)
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoticon Image
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Stress Level",
                    modifier = Modifier.size(180.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Title Text
                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stress Level Text
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = PurpleDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Tingkat stress anda ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = stressLevelColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(stressLevelText)
                        }
                    },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(120.dp))
            }

            // Back Button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Kembali",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
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

// Helper data class to hold four values
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Preview(showBackground = true)
@Composable
fun StressTestResultScreenPreview() {
    StressTestResultScreen(navController = rememberNavController(), stressLevel = StressLevel.RENDAH)
}

@Preview(showBackground = true)
@Composable
fun StressTestResultScreenSedangPreview() {
    StressTestResultScreen(navController = rememberNavController(), stressLevel = StressLevel.SEDANG)
}

@Preview(showBackground = true)
@Composable
fun StressTestResultScreenTinggiPreview() {
    StressTestResultScreen(navController = rememberNavController(), stressLevel = StressLevel.TINGGI)
}
