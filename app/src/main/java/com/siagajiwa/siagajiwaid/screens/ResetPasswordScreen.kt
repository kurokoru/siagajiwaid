package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.ForgotPasswordHeadingTextComponent
import com.siagajiwa.siagajiwaid.components.ImageComponent
import com.siagajiwa.siagajiwaid.components.MyButton
import com.siagajiwa.siagajiwaid.components.PasswordInputComponent
import com.siagajiwa.siagajiwaid.components.TextInfoComponent

@Composable
fun ResetPasswordScreen(navController: NavHostController) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column {
            ImageComponent(image = R.drawable.chunky_bat)
            Spacer(modifier = Modifier.height(10.dp))
            ForgotPasswordHeadingTextComponent(action = "Reset")
            TextInfoComponent(
                textVal = "Don't worry, strange things happen. Please enter the email address associated with your account."
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column {
                PasswordInputComponent(labelVal = "Password")
                Spacer(modifier = Modifier.height(15.dp))
                PasswordInputComponent(labelVal = "Confirm new password")
            }
            MyButton(labelVal = "Submit", navController = navController)
        }
    }
}