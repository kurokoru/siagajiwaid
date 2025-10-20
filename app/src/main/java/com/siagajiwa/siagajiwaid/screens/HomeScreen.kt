package com.siagajiwa.siagajiwaid.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.CustomBottomNavigation
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.PurpleDark
import com.siagajiwa.siagajiwaid.ui.theme.PurpleLight
import com.siagajiwa.siagajiwaid.ui.theme.White
import com.siagajiwa.siagajiwaid.ui.theme.SemanticRed
import com.siagajiwa.siagajiwaid.ui.theme.SemanticYellow
import com.siagajiwa.siagajiwaid.ui.theme.TextGray
import com.siagajiwa.siagajiwaid.ui.theme.SecondaryPurple

@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Space
            Spacer(modifier = Modifier.height(40.dp))

            // Header Section with Purple Background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleDark)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Hi, Melissa Jenner",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // White Content Section with rounded top corners
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = White,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp) // Space for bottom navigation
                ) {
                    item {
                        // Tabs and Stress Level Card
                        StressLevelCard(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            navController = navController
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        // Education Section Title
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = DarkLight.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                            Text(
                                text = "Edukasi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkLight,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = DarkLight.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    item {
                        // Category Grid
                        CategoryGrid()
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        // Bottom Navigation
        CustomBottomNavigation(
            selectedIndex = selectedBottomNavIndex,
            onItemSelected = { selectedBottomNavIndex = it },
            modifier = Modifier.align(Alignment.BottomCenter),
            navController = navController
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    // In a real app, you would use a real NavController instance.
    // For previews, we can use a placeholder or a mock.
    // Since NavHostController is an abstract class, we can't instantiate it directly.
    // For the purpose of this preview, we can pass null if the composable handles it gracefully,
    // or create a simple NavController instance if needed (e.g., using rememberNavController()).
    // However, since the navController is not used in the HomeScreen's logic, passing a dummy is fine.
//    HomeScreen(navController = NavHostController(androidx.compose.ui.platform.LocalContext.current))
}

@Composable
fun StressLevelCard(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 17.dp)
            .padding(top = 27.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 8.dp
        )
    ) {
        Column {
            // Custom Tab Row
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tingkat Stress Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true, color = Color(0xFFFF9500))
                        ) { onTabSelected(0) }
                ) {
                    Column {
                        Text(
                            text = "Tingkat Stres",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        if (selectedTab == 0) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Color(0xFFFF9500)
                            )
                        }
                    }
                }

                // Vertical Divider
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(52.dp),
                    color = PurpleLight.copy(alpha = 0.3f)
                )

                // Skor Wawasan Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true, color = Color(0xFFFF9500))
                        ) { onTabSelected(1) }
                ) {
                    Column {
                        Text(
                            text = "Skor Wawasan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        if (selectedTab == 1) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Color(0xFFFF9500)
                            )
                        }
                    }
                }
            }

            Divider(
                color = PurpleLight.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            // Content Area with animation
            AnimatedContent(
                targetState = selectedTab,
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
                label = "tab_content_animation"
            ) { selectedTabIndex ->
                if (selectedTabIndex == 0) {
                    // Tingkat Stres Tab Content
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Main content area with split layout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left side - "Tingkat Stres" text
                            Text(
                                text = "Tingkat Stres",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )

                            // Right side - "Tinggi" status
                            Text(
                                text = "Tinggi",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE74C3C), // Red color for "Tinggi"
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(
                            color = PurpleLight.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        // Bottom action text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true, color = SecondaryPurple)
                                ) { /* Navigate to stress test */ }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Klik Untuk Cek Tingkat Stres Anda",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SecondaryPurple,
                                textAlign = TextAlign.Center,
                                        modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)
                            ) {
                                navController.navigate("PatientQuiz")
                            }
                            )
                        }
                    }
                } else {
                    // Skor Wawasan Tab Content
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Main content area with split layout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left side - Knowledge info
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Pengetahuan",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Perawatan Pasien",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Cukup",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryPurple
                                )
                            }

                            // Right side - Score and progress
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "75%",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryPurple,
                                    textAlign = TextAlign.End
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "18 dari 24 soal dijawab dengan benar",
                                    fontSize = 12.sp,
                                    color = TextGray,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(
                            color = PurpleLight.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                        // Bottom action text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true, color = SecondaryPurple)
                                ) { /* Navigate to quiz */ }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Klik Untuk Tes Wawasan Anda",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SecondaryPurple,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true)
                            ) {
                                navController.navigate("QuizScreen")
                            }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StessLevelCardPreview() {
    // In a real app, you would use a real NavController instance.
    // For previews, we can use a placeholder or a mock.
    // Since NavHostController is an abstract class, we can't instantiate it directly.
    // For the purpose of this preview, we can pass null if the composable handles it gracefully,
    // or create a simple NavController instance if needed (e.g., using rememberNavController()).
    // However, since the navController is not used in the HomeScreen's logic, passing a dummy is fine.
//    HomeScreen(navController = NavHostController(androidx.compose.ui.platform.LocalContext.current))
    StressLevelCard(selectedTab = 1, onTabSelected = {}, navController = NavHostController(androidx.compose.ui.platform.LocalContext.current))
}

@Composable
fun CategoryGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // First Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "Perawatan\nPasien",
                icon = R.drawable.patient, // Will be replaced with correct icon
                modifier = Modifier.weight(1f)
            )
            CategoryCard(
                title = "Wawasan\nSkizofrenia",
                icon = R.drawable.learn, // Will be replaced with correct icon
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "Manajemen\nStress",
                icon = R.drawable.worker, // Will be replaced with correct icon
                modifier = Modifier.weight(1f)
            )
            CategoryCard(
                title = "Video\nTutorial",
                icon = R.drawable.video, // Will be replaced with correct icon
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .height(145.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = SecondaryPurple),
                onClick = { /* Handle category click */ }
            ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with subtle animation
            val iconScale by animateFloatAsState(
                targetValue = if (isPressed) 0.9f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "icon_scale"
            )

            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier
                    .size(50.dp)
                    .scale(iconScale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
