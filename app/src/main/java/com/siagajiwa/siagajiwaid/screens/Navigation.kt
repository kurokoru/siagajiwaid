package com.siagajiwa.siagajiwaid.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.components.ForgotPasswordScreen
import com.siagajiwa.siagajiwaid.components.LoginScreen
import com.siagajiwa.siagajiwaid.components.ResetPasswordScreen
import com.siagajiwa.siagajiwaid.components.SignupScreen
import com.siagajiwa.siagajiwaid.screens.HomeScreen
import com.siagajiwa.siagajiwaid.screens.QuestionnaireScreen
import com.siagajiwa.siagajiwaid.screens.QuizScreen

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
        composable(
            route = "HomeScreen"
        ) {
            HomeScreen(navController)
        }
        composable(
            route = "PatientQuiz"
        ) {
            QuestionnaireScreen(navController)
        }
        composable(
            route = "QuizScreen"
        ) {
            QuizScreen(navController)
        }
        composable(
            route = "ActivityHistoryScreen"
        ) {
            ActivityHistoryScreen(navController)
        }
        composable(
            route = "QuizResultScreen"
        ) {
            QuizResultScreen(navController)
        }
        composable(
            route = "StressTestResultScreen"
        ) {
            StressTestResultScreen(navController)
        }
    }
}