package com.siagajiwa.siagajiwa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwa.R
import com.siagajiwa.siagajiwa.components.CustomBottomNavigation
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.White

data class PatientCareCategory(
    val id: Int,
    val title: String,
    val icon: Int
)

@Composable
fun PatientCareScreen(
    navController: NavHostController
) {
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) }

    // Patient care categories
    val categories = listOf(
        PatientCareCategory(1, "Pasien dengan perilaku kekerasan", R.drawable.ic_patient),
        PatientCareCategory(2, "Pasien dengan halusinasi", R.drawable.ic_patient),
        PatientCareCategory(3, "Pasien dengan isolasi sosial", R.drawable.ic_patient),
        PatientCareCategory(4, "Pasien dengan harga diri rendah", R.drawable.ic_patient),
        PatientCareCategory(5, "Pasien dengan waham", R.drawable.ic_patient),
        PatientCareCategory(6, "Pasien dengan kurang perawatan diri", R.drawable.ic_patient),
        PatientCareCategory(7, "Pasien dengan risiko melukai diri", R.drawable.ic_patient)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar Spacer
            Spacer(modifier = Modifier.height(40.dp))

            // Navigation Bar
            PatientCareNavigationBar(
                title = "Perawatan Pasien",
                onBackClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content - List of categories
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(categories) { category ->
                    PatientCareCategoryCard(
                        category = category,
                        onClick = {
                            // Handle category click - navigate to detail screen
                            // TODO: Navigate to category detail screen
                        }
                    )
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
private fun PatientCareNavigationBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkLight
        )
    }
}

@Composable
private fun PatientCareCategoryCard(
    category: PatientCareCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Patient icon
            Icon(
                painter = painterResource(id = category.icon),
                contentDescription = category.title,
                tint = Color(0xFF4942AF),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Category title
            Text(
                text = category.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF343434)
            )
        }
    }
}
