package com.siagajiwa.siagajiwa.utils

/**
 * Knowledge Level categories based on percentage of correct answers
 */
enum class KnowledgeLevel(val displayName: String) {
    BAIK("Baik"),       // 76-100%
    CUKUP("Cukup"),     // 56-76%
    KURANG("Kurang")    // <56%
}

/**
 * Result of quiz knowledge assessment
 */
data class QuizTestResult(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val knowledgeLevel: KnowledgeLevel
)

/**
 * Calculator for Knowledge Quiz Assessment
 * Determines knowledge level based on percentage of correct answers
 */
object QuizCalculator {

    /**
     * Calculate quiz result and knowledge level
     *
     * @param answers Map of question ID to selected answer
     * @param questions List of quiz questions with correct answers
     * @return QuizTestResult with score, percentage, and knowledge level
     */
    fun calculateQuizResult(
        answers: Map<Int, String>,
        questions: List<com.siagajiwa.siagajiwa.data.QuizQuestion>
    ): QuizTestResult {
        var correctCount = 0

        // Count correct answers
        questions.forEach { question ->
            val selectedAnswer = answers[question.id]
            val correctAnswerIndex = question.correctAnswerIndex

            if (selectedAnswer != null && correctAnswerIndex != null) {
                // Check if selected answer matches correct answer
                if (selectedAnswer == question.options.getOrNull(correctAnswerIndex)) {
                    correctCount++
                }
            }
        }

        val totalQuestions = questions.size
        val percentage = if (totalQuestions > 0) {
            (correctCount * 100) / totalQuestions
        } else {
            0
        }

        // Determine knowledge level based on percentage
        val knowledgeLevel = when {
            percentage >= 76 -> KnowledgeLevel.BAIK
            percentage >= 56 -> KnowledgeLevel.CUKUP
            else -> KnowledgeLevel.KURANG
        }

        return QuizTestResult(
            correctAnswers = correctCount,
            totalQuestions = totalQuestions,
            percentage = percentage,
            knowledgeLevel = knowledgeLevel
        )
    }
}
