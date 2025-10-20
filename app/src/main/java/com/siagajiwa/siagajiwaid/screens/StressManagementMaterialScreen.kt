package com.siagajiwa.siagajiwaid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.White

@Composable
fun StressManagementMaterialScreen(navController: NavHostController) {
    // List of stress management images - ordered by number
    val stressImages = listOf(
        R.drawable.stress_1,
        R.drawable.stress_2,
        R.drawable.stress_3,
        R.drawable.stress_4,
        R.drawable.stress_5,
        R.drawable.stress_6,
        R.drawable.stress_7,
        R.drawable.stress_8,
        R.drawable.stress_9,
        R.drawable.stress_10,
        R.drawable.stress_11,
        R.drawable.stress_12
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
            StressNavigationBar(
                title = "Manajemen Stres",
                onBackClick = { navController.popBackStack() }
            )

            // Content - Vertical Scrollable Images
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add top spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Display images in vertical scroll
                items(stressImages) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Stress Management Material",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(276f / 390f), // Maintain aspect ratio from design
                        contentScale = ContentScale.Fit
                    )
                }

                // Add bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StressNavigationBar(
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
            onClick = { onBackClick() },
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

@Preview(showBackground = true)
@Composable
fun StressManagementMaterialScreenPreview() {
    StressManagementMaterialScreen(navController = rememberNavController())
}
