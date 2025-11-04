package com.siagajiwa.siagajiwa.utils

/**
 * Stress level categories based on total score
 */
enum class StressLevel(val displayName: String) {
    LOW("Tingkat stres rendah"),
    MEDIUM("Tingkat stres sedang"),
    HIGH("Tingkat stres tinggi")
}

/**
 * Data class to hold stress test calculation results
 */
data class StressTestResult(
    val totalScore: Int,
    val maxScore: Int,
    val stressLevel: StressLevel,
    val percentage: Float
)

/**
 * Calculator for stress test scoring and level determination
 */
object StressCalculator {

    /**
     * Calculate stress test results based on user answers
     *
     * Scoring rules:
     * - 0-13: Low stress level (Tingkat stres rendah)
     * - 14-26: Medium stress level (Tingkat stres sedang)
     * - 27-40: High stress level (Tingkat stres tinggi)
     *
     * @param answers Map of question IDs to selected values (0-4)
     * @return StressTestResult containing score, level, and percentage
     */
    fun calculateStressLevel(answers: Map<Int, String>): StressTestResult {
        // Calculate total score by summing all answer values
        val totalScore = answers.values.sumOf { it.toIntOrNull() ?: 0 }

        // Maximum possible score (assuming 10 questions with max value of 4 each)
        val maxScore = answers.size * 4

        // Determine stress level based on total score
        val stressLevel = when (totalScore) {
            in 0..13 -> StressLevel.LOW
            in 14..26 -> StressLevel.MEDIUM
            in 27..40 -> StressLevel.HIGH
            else -> StressLevel.HIGH // Default to HIGH if score exceeds 40
        }

        // Calculate percentage (0-100)
        val percentage = if (maxScore > 0) {
            (totalScore.toFloat() / maxScore.toFloat()) * 100
        } else {
            0f
        }

        return StressTestResult(
            totalScore = totalScore,
            maxScore = maxScore,
            stressLevel = stressLevel,
            percentage = percentage
        )
    }

    /**
     * Get stress level description text
     */
    fun getStressLevelDescription(stressLevel: StressLevel): String {
        return when (stressLevel) {
            StressLevel.LOW -> "Anda memiliki tingkat stres yang rendah. Pertahankan pola hidup sehat dan terus jaga kesehatan mental Anda."
            StressLevel.MEDIUM -> "Anda memiliki tingkat stres yang sedang. Pertimbangkan untuk melakukan aktivitas relaksasi dan konsultasi jika diperlukan."
            StressLevel.HIGH -> "Anda memiliki tingkat stres yang tinggi. Sangat disarankan untuk berkonsultasi dengan profesional kesehatan mental."
        }
    }

    /**
     * Get color for stress level (in hex format)
     */
    fun getStressLevelColor(stressLevel: StressLevel): Long {
        return when (stressLevel) {
            StressLevel.LOW -> 0xFF4CAF50 // Green
            StressLevel.MEDIUM -> 0xFFFFA726 // Orange
            StressLevel.HIGH -> 0xFFEF5350 // Red
        }
    }

    /**
     * Validate that all questions have been answered
     */
    fun areAllQuestionsAnswered(answers: Map<Int, String>, totalQuestions: Int): Boolean {
        return answers.size == totalQuestions && answers.values.all { it.isNotEmpty() }
    }
}
