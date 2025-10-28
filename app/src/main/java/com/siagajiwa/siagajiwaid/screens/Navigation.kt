package com.siagajiwa.siagajiwaid.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
            route = "StressTestResultScreen"
        ) {
            StressTestResultScreen(navController)
        }
        composable(
            route = "StressManagementMaterialScreen"
        ) {
            StressManagementMaterialScreen(navController)
        }
        composable(
            route = "PatientCareScreen"
        ) {
            PatientCareScreen(navController)
        }
        composable(
            route = "SchizophreniaInsightScreen"
        ) {
            SchizophreniaInsightScreen(navController)
        }
        composable(
            route = "VideoScreen"
        ) {
            VideoScreen(navController)
        }
        composable(
            route = "VideoPlayerScreen/{videoUrl}/{videoTitle}",
            arguments = listOf(
                navArgument("videoUrl") { type = NavType.StringType },
                navArgument("videoTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            val videoTitle = backStackEntry.arguments?.getString("videoTitle") ?: ""
            VideoPlayerScreen(
                navController = navController,
                videoUrl = videoUrl,
                videoTitle = videoTitle
            )
        }
        composable(
            route = "PatientKnowledgeQuizScreen"
        ) {
            PatientKnowledgeQuizScreen(navController)
        }
        composable(
            route = "QuizResultScreen/{correctAnswers}/{totalQuestions}",
            arguments = listOf(
                navArgument("correctAnswers") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val correctAnswers = backStackEntry.arguments?.getInt("correctAnswers") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
            QuizResultScreen(
                navController = navController,
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions
            )
        }
    }
}