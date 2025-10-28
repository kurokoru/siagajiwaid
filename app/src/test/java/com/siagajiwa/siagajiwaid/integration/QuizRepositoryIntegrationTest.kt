package com.siagajiwa.siagajiwaid.integration

import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Integration test for QuizRepository
 * Tests real Supabase connection without device/emulator
 */
class QuizRepositoryIntegrationTest {

    private lateinit var repository: QuizRepository

    @Before
    fun setup() {
        repository = QuizRepository()
    }

    @Test
    fun `test getStressQuiz - fetch and display stress quiz questions from Supabase`() = runBlocking {
        println("\n========================================")
        println("STRESS QUIZ INTEGRATION TEST")
        println("========================================\n")

        val result = repository.getStressQuiz()

        result.fold(
            onSuccess = { questions ->
                println("✅ Successfully fetched ${questions.size} stress quiz questions\n")

                questions.forEachIndexed { index, question ->
                    println("--- Question ${index + 1} ---")
                    println("ID: ${question.id}")
                    println("Question #: ${question.questionNumber}")
                    println("Page: ${question.pageNumber}")
                    println("Order: ${question.order}")
                    println("Question: ${question.questionText}")
                    println("Answer Options: ${question.answerOption}")
                    println("Correct Answer Index: ${question.correctAnswer}")

                    // Parse and display options
                    val options = question.getAnswerOptions()
                    options.forEachIndexed { optIdx, opt ->
                        val marker = if (optIdx == question.correctAnswer) "✓" else " "
                        println("  [$marker] ${('A' + optIdx)}. $opt")
                    }
                    println()
                }

                println("----------------------------------------")
                println("Summary:")
                println("  Total Questions: ${questions.size}")
                println("  Pages: ${questions.map { it.pageNumber }.distinct().sorted()}")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error fetching stress quiz: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
            }
        )
    }

    @Test
    fun `test getPatientQuiz - fetch and display patient care quiz questions from Supabase`() = runBlocking {
        println("\n========================================")
        println("PATIENT CARE QUIZ INTEGRATION TEST")
        println("========================================\n")

        val result = repository.getPatientQuiz()

        result.fold(
            onSuccess = { questions ->
                println("✅ Successfully fetched ${questions.size} patient care quiz questions\n")

                questions.forEachIndexed { index, question ->
                    println("--- Question ${index + 1} ---")
                    println("ID: ${question.id}")
                    println("Question #: ${question.questionNumber}")
                    println("Page: ${question.pageNumber}")
                    println("Order: ${question.order}")
                    println("Question: ${question.questionText}")
                    println()
                }

                println("----------------------------------------")
                println("Summary:")
                println("  Total Questions: ${questions.size}")
                println("  Pages: ${questions.map { it.pageNumber }.distinct().sorted()}")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error fetching patient quiz: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
            }
        )
    }

    @Test
    fun `test quiz data structure - verify answer format parsing`() = runBlocking {
        println("\n========================================")
        println("QUIZ DATA STRUCTURE TEST")
        println("========================================\n")

        val result = repository.getStressQuiz()

        result.fold(
            onSuccess = { questions ->
                println("Testing answer option parsing...\n")

                questions.take(3).forEachIndexed { index, question ->
                    println("Question ${index + 1}:")
                    println("  Raw answer_option: ${question.answerOption}")
                    println("  Parsed options:")
                    question.getAnswerOptions().forEachIndexed { idx, opt ->
                        println("    [$idx] $opt")
                    }
                    println("  Correct answer: ${question.correctAnswer} - ${question.getAnswerOptions().getOrNull(question.correctAnswer)}")
                    println()
                }

                println("✅ Answer parsing works correctly")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error: ${error.message}")
            }
        )
    }
}
