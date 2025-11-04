package com.siagajiwa.siagajiwa.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.siagajiwa.siagajiwa.components.ForgotPasswordScreen
import com.siagajiwa.siagajiwa.components.LoginScreen
import com.siagajiwa.siagajiwa.components.ResetPasswordScreen
import com.siagajiwa.siagajiwa.components.SignupScreen

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
            route = "StressTestResultScreen/{stressLevel}",
            arguments = listOf(
                navArgument("stressLevel") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val stressLevelString = backStackEntry.arguments?.getString("stressLevel") ?: "RENDAH"
            val stressLevel = when (stressLevelString) {
                "RENDAH" -> StressLevel.RENDAH
                "SEDANG" -> StressLevel.SEDANG
                "TINGGI" -> StressLevel.TINGGI
                else -> StressLevel.RENDAH
            }
            StressTestResultScreen(navController, stressLevel)
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
            route = "NativeVideoPlayerScreen/{videoUrl}/{videoTitle}",
            arguments = listOf(
                navArgument("videoUrl") { type = NavType.StringType },
                navArgument("videoTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            val videoTitle = backStackEntry.arguments?.getString("videoTitle") ?: ""
            NativeVideoPlayerScreen(
                navController = navController,
                videoUrl = videoUrl,
                videoTitle = videoTitle
            )
        }
        composable(
            route = "AlternativeVideoPlayerScreen/{videoUrl}/{videoTitle}",
            arguments = listOf(
                navArgument("videoUrl") { type = NavType.StringType },
                navArgument("videoTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            val videoTitle = backStackEntry.arguments?.getString("videoTitle") ?: ""
            AlternativeVideoPlayerScreen(
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

            // Get shared ViewModel from the parent quiz screen's backStackEntry
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("PatientKnowledgeQuizScreen")
            }
            val sharedViewModel: com.siagajiwa.siagajiwa.viewmodel.KnowledgeQuizViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)

            QuizResultScreen(
                navController = navController,
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                viewModel = sharedViewModel
            )
        }
        composable(
            route = "ProfileScreen"
        ) {
            ProfileScreen(navController)
        }
    }
}