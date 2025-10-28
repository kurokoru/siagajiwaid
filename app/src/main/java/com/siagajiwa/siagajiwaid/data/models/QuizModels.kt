package com.siagajiwa.siagajiwaid.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model for Stress Quiz (stress_quiz table)
 * Multiple choice questions with 4 options (each up to 10 words)
 * Answer options are stored as pipe-separated string: "option1|option2|option3|option4"
 * Correct answer is the index (0-3) of the correct option
 */
@Serializable
data class StressQuizQuestion(
    @SerialName("id")
    val id: Int,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("question_number")
    val questionNumber: Int? = null,

    @SerialName("question_text")
    val questionText: String? = null,

    @SerialName("answer_option")
    val answerOption: String? = null, // Format: "option1|option2|option3|option4"

    @SerialName("correct_answer")
    val correctAnswer: Int? = null, // Index of correct answer (0-3)

    @SerialName("order")
    val order: Int? = null
) {
    /**
     * Parse the pipe-separated answer options into a list
     */
    fun getAnswerOptions(): List<String> {
        return answerOption?.split("|")?.map { it.trim() } ?: emptyList()
    }
}

/**
 * Model for Patient Care Quiz (perawatan_quiz table)
 * Rating scale questions (0-4 scale)
 */
@Serializable
data class PatientQuizQuestion(
    @SerialName("id")
    val id: Int,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("question_number")
    val questionNumber: Int? = null,

    @SerialName("question_text")
    val questionText: String? = null,

    @SerialName("answer_option")
    val answerOption: String? = null,

    @SerialName("correct_answer")
    val correctAnswer: Int? = null,

    @SerialName("order")
    val order: Int? = null
)
