package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.ui.theme.BrandColor
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight


@Composable
fun ForgotPassword(navController: NavHostController) {
    Text(
        text = "Forgot Password?",
        color = DarkLight,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.clickable {
            navController.navigate("ForgotPassword")
        }
    )
}