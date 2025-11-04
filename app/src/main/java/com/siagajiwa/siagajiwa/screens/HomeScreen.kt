package com.siagajiwa.siagajiwa.screens

import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.PurpleDark
import com.siagajiwa.siagajiwa.ui.theme.PurpleLight
import com.siagajiwa.siagajiwa.ui.theme.White
import com.siagajiwa.siagajiwa.ui.theme.SemanticRed
import com.siagajiwa.siagajiwa.ui.theme.SemanticYellow
import com.siagajiwa.siagajiwa.ui.theme.SemanticGreen
import com.siagajiwa.siagajiwa.ui.theme.TextGray
import com.siagajiwa.siagajiwa.ui.theme.SecondaryPurple
import com.siagajiwa.siagajiwa.viewmodel.UserViewModel
import com.siagajiwa.siagajiwa.viewmodel.UserUiState

@Composable
fun HomeScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) }
    val uiState by userViewModel.uiState.collectAsState()

    // Check auth state when screen loads - redirect to login if not authenticated
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100) // Small delay to let session load
        val currentUser = com.siagajiwa.siagajiwa.data.SupabaseClient.auth.currentUserOrNull()
        Log.d("HomeScreen", "Screen loaded - Auth state check:")
        Log.d("HomeScreen", "  From SupabaseClient - User ID: ${currentUser?.id}")
        Log.d("HomeScreen", "  From SupabaseClient - User Email: ${currentUser?.email}")
        Log.d("HomeScreen", "  From UserViewModel - User: ${uiState.user?.email}")
        Log.d("HomeScreen", "  From UserViewModel - Full Name: ${uiState.user?.fullName}")
        Log.d("HomeScreen", "  From UserViewModel - Is Logged In: ${uiState.isLoggedIn}")

        // Redirect to login if user is not authenticated
        if (currentUser == null && uiState.user == null) {
            Log.e("HomeScreen", "  WARNING: User is NOT logged in - redirecting to login")
            navController.navigate("LoginScreen") {
                popUpTo("HomeScreen") { inclusive = true }
            }
        } else {
            Log.d("HomeScreen", "  ✅ User is logged in successfully")

            // Ensure user profile is loaded with full name
            if (uiState.user?.fullName.isNullOrBlank() && currentUser != null) {
                Log.w("HomeScreen", "  ⚠️ Full name is missing, loading user profile...")
                userViewModel.loadUserProfile(currentUser.id)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Space
            Spacer(modifier = Modifier.height(20.dp))

            // Header Section with Purple Background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleDark)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Hi, ${uiState.user?.fullName ?: "User"}",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                            navController = navController,
                            uiState = uiState
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
                            HorizontalDivider(
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
                            HorizontalDivider(
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
                        CategoryGrid(navController)
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    var selectedTab by remember { mutableIntStateOf(1) }
    var selectedBottomNavIndex by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Space
            Spacer(modifier = Modifier.height(20.dp))

            // Header Section with Purple Background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleDark)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Hi, John Doe",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                    contentPadding = PaddingValues(bottom = 90.dp) // Space for bottom navigation
                ) {
                    item {
                        // Preview version of StressLevelCard
                        StressLevelCardPreview(
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
                            HorizontalDivider(
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
                            HorizontalDivider(
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
                        CategoryGrid(navController)
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

@Composable
private fun StressLevelCardPreview(
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        if (selectedTab == 0) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Color(0xFFFF9500)
                            )
                        }
                    }
                }

                // Vertical Divider
                HorizontalDivider(
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
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        if (selectedTab == 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Color(0xFFFF9500)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = PurpleLight.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            // Content Area
            if (selectedTab == 0) {
                // Tingkat Stres Tab Content
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tingkat Stres",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Rendah",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = PurpleLight.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true, color = SecondaryPurple)
                            ) { }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Klik Untuk Cek Tingkat Stres Anda",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryPurple,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Skor Wawasan Tab Content
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pengetahuan Perawatan Pasien",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Baik",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = SecondaryPurple
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "85%",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryPurple,
                                textAlign = TextAlign.End
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "20 dari 24 soal dijawab dengan benar",
                                fontSize = 9.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        color = PurpleLight.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true, color = SecondaryPurple)
                            ) { }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Klik Untuk Tes Wawasan Anda",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryPurple,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StressLevelCard(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    uiState: UserUiState
) {
    val user = uiState.user
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
                            fontSize = 14.sp,
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
                            fontSize = 12.sp,
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

                            // Right side - Stress Level from latest stress_result
                            Text(
                                text = uiState.latestStressResult?.stressLevel ?: "Belum diukur",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (uiState.latestStressResult?.stressLevel?.lowercase()) {
                                    "rendah" -> SemanticGreen
                                    "sedang" -> SemanticYellow
                                    "tinggi" -> SemanticRed
                                    else -> TextGray
                                },
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
                                navController.navigate("QuizScreen")
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
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Pengetahuan Perawatan Pasien",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Calculate knowledge level from latest quiz result
                                val knowledgeLevel = when {
                                    uiState.latestQuizResult == null -> "Belum diukur"
                                    uiState.latestQuizResult.percentage >= 75 -> "Baik"
                                    uiState.latestQuizResult.percentage >= 50 -> "Cukup"
                                    else -> "Kurang"
                                }

                                Text(
                                    text = knowledgeLevel,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryPurple
                                )
                            }

                            // Right side - Score and progress from latest quiz result
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "${uiState.latestQuizResult?.percentage ?: 0}%",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryPurple,
                                    textAlign = TextAlign.End
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (uiState.latestQuizResult != null) {
                                        "${uiState.latestQuizResult.quizScore} dari ${uiState.latestQuizResult.totalQuestions} soal dijawab dengan benar"
                                    } else {
                                        "Belum mengikuti tes"
                                    },
                                    fontSize = 8.sp,
                                    color = DarkLight,
                                    textAlign = TextAlign.Center,
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
                                navController.navigate("PatientKnowledgeQuizScreen")
                            }
                            )
                        }
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun StessLevelCardPreview() {
//    // In a real app, you would use a real NavController instance.
//    // For previews, we can use a placeholder or a mock.
//    // Since NavHostController is an abstract class, we can't instantiate it directly.
//    // For the purpose of this preview, we can pass null if the composable handles it gracefully,
//    // or create a simple NavController instance if needed (e.g., using rememberNavController()).
//    // However, since the navController is not used in the HomeScreen's logic, passing a dummy is fine.
//    //HomeScreen(navController = NavHostController(androidx.compose.ui.platform.LocalContext.current))
////    StressLevelCard(selectedTab = 1, onTabSelected = {}, navController = NavHostController(androidx.compose.ui.platform.LocalContext.current))
////}

@Composable
fun CategoryGrid(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "Perawatan\nPasien",
                icon = R.drawable.patient,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("PatientCareScreen") }
            )
            CategoryCard(
                title = "Selayang Pandang\nGangguan Jiwa",
                icon = R.drawable.learn,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("SchizophreniaInsightScreen") }
            )
        }

        // Second Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "Manajemen\nStress",
                icon = R.drawable.worker,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("StressManagementMaterialScreen") }
            )
            CategoryCard(
                title = "Video\nTutorial",
                icon = R.drawable.video,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("VideoScreen") }
            )
        }

        // Third Row - Play Video Demos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCard(
                title = "Play Video\n(Native)",
                icon = R.drawable.video,
                modifier = Modifier.weight(1f),
                onClick = {
                    // Test video - Relaksasi Otot Progresif
                    val testVideoUrl = "https://www.youtube.com/watch?v=shfd8vZj-T4"
                    val testVideoTitle = "Relaksasi Otot Progresif"
                    val encodedUrl = java.net.URLEncoder.encode(testVideoUrl, "UTF-8")
                    val encodedTitle = java.net.URLEncoder.encode(testVideoTitle, "UTF-8")
                    navController.navigate("NativeVideoPlayerScreen/$encodedUrl/$encodedTitle")
                }
            )
            CategoryCard(
                title = "Alternative\nPlayer",
                icon = R.drawable.video,
                modifier = Modifier.weight(1f),
                onClick = {
                    // Test direct video URL (supports MP4, HLS, DASH, etc.)
                    // Example: BigBuckBunny sample video
                    val testVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    val testVideoTitle = "Alternative Player Demo (ExoPlayer)"
                    val encodedUrl = java.net.URLEncoder.encode(testVideoUrl, "UTF-8")
                    val encodedTitle = java.net.URLEncoder.encode(testVideoTitle, "UTF-8")
                    navController.navigate("AlternativeVideoPlayerScreen/$encodedUrl/$encodedTitle")
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
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
            .aspectRatio(1f) // Makes it square
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = SecondaryPurple),
                onClick = onClick
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
                    .size(70.dp)
                    .scale(iconScale)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
