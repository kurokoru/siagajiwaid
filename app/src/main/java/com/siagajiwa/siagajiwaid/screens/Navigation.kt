package com.siagajiwa.siagajiwaid.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.components.ForgotPasswordScreen
import com.siagajiwa.siagajiwaid.components.LoginScreen
import com.siagajiwa.siagajiwaid.components.ResetPasswordScreen
import com.siagajiwa.siagajiwaid.components.SignupScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "LoginScreen"
    ) {
        composable(
            route = "LoginScreen"
        ) {
            LoginScreen(
                navController
            )
        }
        composable(
            route = "ForgotPassword"
        ) {
            ForgotPasswordScreen(navController)
        }
        composable(route = "ResetPassword") {
            ResetPasswordScreen(navController)
        }
        composable(
            route = "SignupScreen"
        ) {
            SignupScreen(navController)
        }
    }
}