package com.siagajiwa.siagajiwaid.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model for Stress Quiz (stress_quiz table)
 * Multiple choice questions with 2 options
 */
@Serializable
data class StressQuizQuestion(
    @SerialName("id")
    val id: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("question_number")
    val questionNumber: Int,

    @SerialName("question_text")
    val questionText: String,

    @SerialName("option1")
    val option1: String,

    @SerialName("option2")
    val option2: String,

    @SerialName("page_number")
    val pageNumber: Int,

    @SerialName("order")
    val order: Int
)

/**
 * Model for Patient Care Quiz (perawatan_quiz table)
 * Rating scale questions (0-4 scale)
 */
@Serializable
data class PatientQuizQuestion(
    @SerialName("id")
    val id: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("question_number")
    val questionNumber: Int,

    @SerialName("question_text")
    val questionText: String,

    @SerialName("page_number")
    val pageNumber: Int,

    @SerialName("order")
    val order: Int
)
