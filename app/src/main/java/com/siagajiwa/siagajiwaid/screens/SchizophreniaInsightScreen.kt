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
fun SchizophreniaInsightScreen(navController: NavHostController) {
    // List of schizophrenia insight images - ordered by number
    val schizoImages = listOf(
        R.drawable.skizo_2,
        R.drawable.skizo_3,
        R.drawable.skizo_4
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
            Spacer(modifier = Modifier.height(44.dp))

            // Navigation Bar
            SchizophreniaInsightNavigationBar(
                title = "Wawasan Skizofrenia",
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
                items(schizoImages) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Schizophrenia Insight Material",
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
private fun SchizophreniaInsightNavigationBar(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .clickable { onBackClick() }
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = DarkLight
            )
        }

        // Title
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkLight
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SchizophreniaInsightScreenPreview() {
    SchizophreniaInsightScreen(navController = rememberNavController())
}
