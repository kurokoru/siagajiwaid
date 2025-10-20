package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomBottomNavigation(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    Surface(
        color = Color.White,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation Items
                NavigationItem(
                    icon = R.drawable.home,
                    label = "Home",
                    isSelected = selectedIndex == 0,
                    onClick = { onItemSelected(0) }
                )

                NavigationItem(
                    icon = R.drawable.appointment,
                    label = "Riwayat",
                    isSelected = selectedIndex == 1,
                    onClick = {
                        onItemSelected(1)
                        navController?.navigate("ActivityHistoryScreen")
                    }
                )

                NavigationItem(
                    icon = R.drawable.settings,
                    label = "Settings",
                    isSelected = selectedIndex == 2,
                    onClick = { onItemSelected(2) }
                )
            }

            // Bottom indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(horizontal = 80.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun NavigationItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        // Expanded view
        Surface(
            color = Color(0xFF3F32C6),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .height(56.dp)
                .widthIn(min = 120.dp)
                .clickable { onClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
    } else {
        // Icon only view
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomBottomNavigationPreview() {
    var selectedIndex by remember { mutableIntStateOf(1) }
    CustomBottomNavigation(selectedIndex = selectedIndex, onItemSelected = { index -> selectedIndex = index })
}