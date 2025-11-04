package com.siagajiwa.siagajiwa.data

data class QuizQuestion(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int? = null // Index of correct answer (0-based), null for non-graded questions
)

data class QuizPage(
    val pageNumber: Int,
    val questions: List<QuizQuestion>
)
