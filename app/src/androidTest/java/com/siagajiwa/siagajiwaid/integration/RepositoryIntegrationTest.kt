package com.siagajiwa.siagajiwaid.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwaid.data.repository.MediaRepository
import com.siagajiwa.siagajiwaid.data.repository.QuizRepository
import com.siagajiwa.siagajiwaid.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.json.JSONObject
import org.json.JSONArray

/**
 * Instrumented Integration Tests for Repositories
 * Run these tests to see real data from Supabase
 *
 * To run: ./gradlew connectedAndroidTest
 * Or run from Android Studio with a connected device/emulator
 */
@RunWith(AndroidJUnit4::class)
class RepositoryIntegrationTest {

    private val quizRepository = QuizRepository()
    private val mediaRepository = MediaRepository()
    private val userRepository = UserRepository()

    @Test
    fun testStressQuizIntegration() = runBlocking {
        println("\n" + "=".repeat(60))
        println("STRESS QUIZ INTEGRATION TEST - FETCHING FROM SUPABASE")
        println("=".repeat(60))

        val result = quizRepository.getStressQuiz()

        result.fold(
            onSuccess = { questions ->
                println("\n✅ SUCCESS: Fetched ${questions.size} stress quiz questions")
                println()

                questions.forEachIndexed { index, question ->
                    println("━".repeat(60))
                    println("Question #${index + 1}")
                    println("━".repeat(60))
                    println("Database ID: ${question.id}")
                    println("Question Number: ${question.questionNumber}")
                    // Page field removed from model
                    println("Order: ${question.order}")
                    println("\nQ: ${question.questionText}")
                    println("\nAnswer Options:")

                    val options = question.getAnswerOptions()
                    options.forEachIndexed { optIdx, opt ->
                        val label = ('A' + optIdx).toString()
                        val marker = if (optIdx == question.correctAnswer) " ✓ (CORRECT)" else ""
                        println("   $label. $opt$marker")
                    }
                    println()
                }

                println("━".repeat(60))
                println("SUMMARY")
                println("━".repeat(60))
                println("Total Questions: ${questions.size}")
                // Page grouping removed - pagination removed from quiz
                println("=".repeat(60) + "\n")
            },
            onFailure = { error ->
                println("\n❌ ERROR: Failed to fetch stress quiz")
                println("Error message: ${error.message}")
                println("Error type: ${error.javaClass.simpleName}")
                error.printStackTrace()
                println("=".repeat(60) + "\n")
            }
        )
    }

    @Test
    fun testPatientQuizIntegration() = runBlocking {
        println("\n" + "=".repeat(60))
        println("PATIENT CARE QUIZ INTEGRATION TEST - FETCHING FROM SUPABASE")
        println("=".repeat(60))

        val result = quizRepository.getPatientQuiz()

        result.fold(
            onSuccess = { questions ->
                println("\n✅ SUCCESS: Fetched ${questions.size} patient care quiz questions")
                println()

                questions.forEachIndexed { index, question ->
                    println("━".repeat(60))
                    println("Question #${index + 1}")
                    println("━".repeat(60))
                    println("Database ID: ${question.id}")
                    println("Question Number: ${question.questionNumber}")
                    // Page field removed from model
                    println("Order: ${question.order}")
                    println("\nQ: ${question.questionText}")
                    println("(Rating scale: 0-4)")
                    println()
                }

                println("━".repeat(60))
                println("SUMMARY")
                println("━".repeat(60))
                println("Total Questions: ${questions.size}")
                // Page grouping removed - pagination removed from quiz
                println("=".repeat(60) + "\n")
            },
            onFailure = { error ->
                println("\n❌ ERROR: Failed to fetch patient quiz")
                println("Error message: ${error.message}")
                println("Error type: ${error.javaClass.simpleName}")
                error.printStackTrace()
                println("=".repeat(60) + "\n")
            }
        )
    }

    @Test
    fun testStressMediaIntegration() = runBlocking {
        println("\n" + "=".repeat(60))
        println("STRESS MEDIA INTEGRATION TEST - FETCHING FROM SUPABASE")
        println("=".repeat(60))

        val result = mediaRepository.getStressMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("\n✅ SUCCESS: Fetched ${mediaList.size} stress media items")
                println()

                mediaList.forEachIndexed { index, media ->
                    println("━".repeat(60))
                    println("Media #${index + 1}")
                    println("━".repeat(60))
                    println("Database ID: ${media.id}")
                    println("Order: ${media.order}")
                    println("Link: ${media.link}")
                    println("Created At: ${media.createdAt}")
                    println()
                }

                println("━".repeat(60))
                println("SUMMARY")
                println("━".repeat(60))
                println("Total Media Items: ${mediaList.size}")
                println("Order Range: ${mediaList.minOfOrNull { it.order }} - ${mediaList.maxOfOrNull { it.order }}")

                // Analyze link types
                val youtubeCount = mediaList.count { it.link.contains("youtube.com") || it.link.contains("youtu.be") }
                println("\nLink Analysis:")
                println("  YouTube Links: $youtubeCount")
                println("  Other Links: ${mediaList.size - youtubeCount}")
                println("=".repeat(60) + "\n")
            },
            onFailure = { error ->
                println("\n❌ ERROR: Failed to fetch stress media")
                println("Error message: ${error.message}")
                println("Error type: ${error.javaClass.simpleName}")
                error.printStackTrace()
                println("=".repeat(60) + "\n")
            }
        )
    }

    @Test
    fun testPatientCareMediaIntegration() = runBlocking {
        println("\n" + "=".repeat(60))
        println("PATIENT CARE MEDIA INTEGRATION TEST - FETCHING FROM SUPABASE")
        println("=".repeat(60))

        val result = mediaRepository.getPatientCareMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("\n✅ SUCCESS: Fetched ${mediaList.size} patient care media items")
                println()

                mediaList.forEachIndexed { index, media ->
                    println("━".repeat(60))
                    println("Media #${index + 1}")
                    println("━".repeat(60))
                    println("Database ID: ${media.id}")
                    println("Order: ${media.order}")
                    println("Link: ${media.link}")
                    println("Created At: ${media.createdAt}")
                    println()
                }

                println("━".repeat(60))
                println("SUMMARY")
                println("━".repeat(60))
                println("Total Media Items: ${mediaList.size}")
                println("Order Range: ${mediaList.minOfOrNull { it.order }} - ${mediaList.maxOfOrNull { it.order }}")

                val youtubeCount = mediaList.count { it.link.contains("youtube.com") || it.link.contains("youtu.be") }
                println("\nLink Analysis:")
                println("  YouTube Links: $youtubeCount")
                println("  Other Links: ${mediaList.size - youtubeCount}")
                println("=".repeat(60) + "\n")
            },
            onFailure = { error ->
                println("\n❌ ERROR: Failed to fetch patient care media")
                println("Error message: ${error.message}")
                println("Error type: ${error.javaClass.simpleName}")
                error.printStackTrace()
                println("=".repeat(60) + "\n")
            }
        )
    }

    @Test
    fun testSchizophreniaMediaIntegration() = runBlocking {
        println("\n" + "=".repeat(60))
        println("SCHIZOPHRENIA MEDIA INTEGRATION TEST - FETCHING FROM SUPABASE")
        println("=".repeat(60))

        val result = mediaRepository.getSchizophreniaMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("\n✅ SUCCESS: Fetched ${mediaList.size} schizophrenia media items")
                println()

                mediaList.forEachIndexed { index, media ->
                    println("━".repeat(60))
                    println("Media #${index + 1}")
                    println("━".repeat(60))
                    println("Database ID: ${media.id}")
                    println("Order: ${media.order}")
                    println("Link: ${media.link}")
                    println("Created At: ${media.createdAt}")
                    println()
                }

                println("━".repeat(60))
                println("SUMMARY")
                println("━".repeat(60))
                println("Total Media Items: ${mediaList.size}")
                println("Order Range: ${mediaList.minOfOrNull { it.order }} - ${mediaList.maxOfOrNull { it.order }}")

                val youtubeCount = mediaList.count { it.link.contains("youtube.com") || it.link.contains("youtu.be") }
                println("\nLink Analysis:")
                println("  YouTube Links: $youtubeCount")
                println("  Other Links: ${mediaList.size - youtubeCount}")
                println("=".repeat(60) + "\n")
            },
            onFailure = { error ->
                println("\n❌ ERROR: Failed to fetch schizophrenia media")
                println("Error message: ${error.message}")
                println("Error type: ${error.javaClass.simpleName}")
                error.printStackTrace()
                println("=".repeat(60) + "\n")
            }
        )
    }

    @Test
    fun testAllRepositoriesComprehensive() = runBlocking {
        println("\n" + "=".repeat(60))
        println("COMPREHENSIVE REPOSITORY INTEGRATION TEST")
        println("Testing all repositories and data sources")
        println("=".repeat(60))

        val jsonResults = JSONObject()
        val errors = JSONArray()

        // Step 1: Authenticate
        println("\n🔐 Authenticating user...")
        val authResult = userRepository.signInWithEmail("vahyo@yopmail.com", "123456")

        authResult.fold(
            onSuccess = { userInfo ->
                println("✅ Authentication successful!")
                println("User ID: ${userInfo.id}")
                println("Email: ${userInfo.email}")
                jsonResults.put("authentication", JSONObject().apply {
                    put("success", true)
                    put("userId", userInfo.id)
                    put("email", userInfo.email)
                })
            },
            onFailure = { error ->
                println("❌ Authentication failed: ${error.message}")
                val authError = JSONObject().apply {
                    put("type", "authentication")
                    put("message", error.message)
                    put("stackTrace", error.stackTraceToString())
                }
                errors.put(authError)
                jsonResults.put("authentication", JSONObject().apply {
                    put("success", false)
                    put("error", error.message)
                })
                // Don't proceed if authentication fails
                println("\n" + "=".repeat(60))
                println("JSON RESULTS:")
                println("=".repeat(60))
                jsonResults.put("errors", errors)
                println(jsonResults.toString(2))
                return@runBlocking
            }
        )

        println("\n📊 Fetching data from all sources...\n")

        // Fetch all data
        val stressQuizResult = quizRepository.getStressQuiz()
        val patientQuizResult = quizRepository.getPatientQuiz()
        val stressMediaResult = mediaRepository.getStressMedia()
        val patientMediaResult = mediaRepository.getPatientCareMedia()
        val schizoMediaResult = mediaRepository.getSchizophreniaMedia()

        println("━".repeat(60))
        println("RESULTS SUMMARY")
        println("━".repeat(60))

        val quizData = JSONObject()
        val mediaData = JSONObject()

        // Quiz Results
        println("\n📝 QUIZ DATA:")
        stressQuizResult.fold(
            onSuccess = {
                println("  ✅ Stress Quiz: ${it.size} questions")
                quizData.put("stressQuiz", JSONObject().apply {
                    put("success", true)
                    put("count", it.size)
                })
            },
            onFailure = {
                println("  ❌ Stress Quiz: ${it.message}")
                quizData.put("stressQuiz", JSONObject().apply {
                    put("success", false)
                    put("error", it.message)
                })
                errors.put(JSONObject().apply {
                    put("type", "stressQuiz")
                    put("message", it.message)
                    put("stackTrace", it.stackTraceToString())
                })
            }
        )
        patientQuizResult.fold(
            onSuccess = {
                println("  ✅ Patient Care Quiz: ${it.size} questions")
                quizData.put("patientQuiz", JSONObject().apply {
                    put("success", true)
                    put("count", it.size)
                })
            },
            onFailure = {
                println("  ❌ Patient Care Quiz: ${it.message}")
                quizData.put("patientQuiz", JSONObject().apply {
                    put("success", false)
                    put("error", it.message)
                })
                errors.put(JSONObject().apply {
                    put("type", "patientQuiz")
                    put("message", it.message)
                    put("stackTrace", it.stackTraceToString())
                })
            }
        )

        // Media Results
        println("\n🎬 MEDIA DATA:")
        stressMediaResult.fold(
            onSuccess = {
                println("  ✅ Stress Media: ${it.size} items")
                mediaData.put("stressMedia", JSONObject().apply {
                    put("success", true)
                    put("count", it.size)
                })
            },
            onFailure = {
                println("  ❌ Stress Media: ${it.message}")
                mediaData.put("stressMedia", JSONObject().apply {
                    put("success", false)
                    put("error", it.message)
                })
                errors.put(JSONObject().apply {
                    put("type", "stressMedia")
                    put("message", it.message)
                    put("stackTrace", it.stackTraceToString())
                })
            }
        )
        patientMediaResult.fold(
            onSuccess = {
                println("  ✅ Patient Care Media: ${it.size} items")
                mediaData.put("patientCareMedia", JSONObject().apply {
                    put("success", true)
                    put("count", it.size)
                })
            },
            onFailure = {
                println("  ❌ Patient Care Media: ${it.message}")
                mediaData.put("patientCareMedia", JSONObject().apply {
                    put("success", false)
                    put("error", it.message)
                })
                errors.put(JSONObject().apply {
                    put("type", "patientCareMedia")
                    put("message", it.message)
                    put("stackTrace", it.stackTraceToString())
                })
            }
        )
        schizoMediaResult.fold(
            onSuccess = {
                println("  ✅ Schizophrenia Media: ${it.size} items")
                mediaData.put("schizophreniaMedia", JSONObject().apply {
                    put("success", true)
                    put("count", it.size)
                })
            },
            onFailure = {
                println("  ❌ Schizophrenia Media: ${it.message}")
                mediaData.put("schizophreniaMedia", JSONObject().apply {
                    put("success", false)
                    put("error", it.message)
                })
                errors.put(JSONObject().apply {
                    put("type", "schizophreniaMedia")
                    put("message", it.message)
                    put("stackTrace", it.stackTraceToString())
                })
            }
        )

        // Calculate totals
        val totalQuizQuestions = listOfNotNull(
            stressQuizResult.getOrNull()?.size,
            patientQuizResult.getOrNull()?.size
        ).sum()

        val totalMediaItems = listOfNotNull(
            stressMediaResult.getOrNull()?.size,
            patientMediaResult.getOrNull()?.size,
            schizoMediaResult.getOrNull()?.size
        ).sum()

        println("\n━".repeat(60))
        println("GRAND TOTAL")
        println("━".repeat(60))
        println("📊 Total Quiz Questions: $totalQuizQuestions")
        println("🎬 Total Media Items: $totalMediaItems")
        println("📦 Total Data Items: ${totalQuizQuestions + totalMediaItems}")

        // Add to JSON results
        jsonResults.put("quizData", quizData)
        jsonResults.put("mediaData", mediaData)
        jsonResults.put("totals", JSONObject().apply {
            put("quizQuestions", totalQuizQuestions)
            put("mediaItems", totalMediaItems)
            put("totalDataItems", totalQuizQuestions + totalMediaItems)
        })
        jsonResults.put("errors", errors)
        jsonResults.put("errorCount", errors.length())

        println("\n" + "=".repeat(60))
        println("JSON RESULTS:")
        println("=".repeat(60))
        println(jsonResults.toString(2))
        println("=".repeat(60) + "\n")
    }
}
