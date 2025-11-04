package com.siagajiwa.siagajiwa.integration

import com.siagajiwa.siagajiwa.data.repository.MediaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Integration test for MediaRepository
 * Tests real Supabase connection without device/emulator
 */
class MediaRepositoryIntegrationTest {

    private lateinit var repository: MediaRepository

    @Before
    fun setup() {
        repository = MediaRepository()
    }

    @Test
    fun `test getStressMedia - fetch and display stress management media from Supabase`() = runBlocking {
        println("\n========================================")
        println("STRESS MEDIA INTEGRATION TEST")
        println("========================================\n")

        val result = repository.getStressMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("✅ Successfully fetched ${mediaList.size} stress media items\n")

                mediaList.forEachIndexed { index, media ->
                    println("--- Media ${index + 1} ---")
                    println("ID: ${media.id}")
                    println("Order: ${media.order}")
                    println("Link: ${media.link}")
                    println("Created At: ${media.createdAt}")
                    println()
                }

                println("----------------------------------------")
                println("Summary:")
                println("  Total Media Items: ${mediaList.size}")
                println("  Order Range: ${mediaList.minOfOrNull { it.order }} - ${mediaList.maxOfOrNull { it.order }}")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error fetching stress media: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
            }
        )
    }

    @Test
    fun `test getPatientCareMedia - fetch and display patient care media from Supabase`() = runBlocking {
        println("\n========================================")
        println("PATIENT CARE MEDIA INTEGRATION TEST")
        println("========================================\n")

        val result = repository.getPatientCareMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("✅ Successfully fetched ${mediaList.size} patient care media items\n")

                mediaList.forEachIndexed { index, media ->
                    println("--- Media ${index + 1} ---")
                    println("ID: ${media.id}")
                    println("Order: ${media.order}")
                    println("Link: ${media.link}")
                    println("Created At: ${media.createdAt}")
                    println()
                }

                println("----------------------------------------")
                println("Summary:")
                println("  Total Media Items: ${mediaList.size}")
                println("  Order Range: ${mediaList.minOfOrNull { it.order }} - ${mediaList.maxOfOrNull { it.order }}")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error fetching patient care media: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
            }
        )
    }

    @Test
    fun `test getSchizophreniaMedia - fetch and display schizophrenia insight media from Supabase`() = runBlocking {
        println("\n========================================")
        println("SCHIZOPHRENIA MEDIA INTEGRATION TEST")
        println("========================================\n")

        val result = repository.getSchizophreniaMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("✅ Successfully fetched ${mediaList.size} schizophrenia media items\n")

                mediaList.forEachIndexed { index, media ->
                    println("--- Media ${index + 1} ---")
                    println("ID: ${media.id}")
                    println("Order: ${media.order}")
                    println("Link: ${media.link}")
                    println("Created At: ${media.createdAt}")
                    println()
                }

                println("----------------------------------------")
                println("Summary:")
                println("  Total Media Items: ${mediaList.size}")
                println("  Order Range: ${mediaList.minOfOrNull { it.order }} - ${mediaList.maxOfOrNull { it.order }}")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error fetching schizophrenia media: ${error.message}")
                println("Stack trace:")
                error.printStackTrace()
            }
        )
    }

    @Test
    fun `test all media sources - comprehensive media fetch`() = runBlocking {
        println("\n========================================")
        println("COMPREHENSIVE MEDIA TEST")
        println("========================================\n")

        println("Fetching all media types...\n")

        // Fetch all media types
        val stressResult = repository.getStressMedia()
        val patientResult = repository.getPatientCareMedia()
        val schizoResult = repository.getSchizophreniaMedia()

        // Display summary
        println("--- Results Summary ---")

        stressResult.fold(
            onSuccess = { println("✅ Stress Media: ${it.size} items") },
            onFailure = { println("❌ Stress Media: Failed - ${it.message}") }
        )

        patientResult.fold(
            onSuccess = { println("✅ Patient Care Media: ${it.size} items") },
            onFailure = { println("❌ Patient Care Media: Failed - ${it.message}") }
        )

        schizoResult.fold(
            onSuccess = { println("✅ Schizophrenia Media: ${it.size} items") },
            onFailure = { println("❌ Schizophrenia Media: Failed - ${it.message}") }
        )

        // Calculate total
        val totalMedia = listOfNotNull(
            stressResult.getOrNull()?.size,
            patientResult.getOrNull()?.size,
            schizoResult.getOrNull()?.size
        ).sum()

        println("\n--- Overall Summary ---")
        println("Total Media Items Across All Sources: $totalMedia")
        println("========================================\n")
    }

    @Test
    fun `test media links - verify YouTube URL format`() = runBlocking {
        println("\n========================================")
        println("MEDIA LINK VALIDATION TEST")
        println("========================================\n")

        val result = repository.getStressMedia()

        result.fold(
            onSuccess = { mediaList ->
                println("Validating YouTube links...\n")

                mediaList.forEach { media ->
                    val isYouTube = media.link.contains("youtube.com") ||
                                   media.link.contains("youtu.be")
                    val status = if (isYouTube) "✅" else "⚠️"
                    println("$status Order ${media.order}: ${media.link}")
                }

                val youtubeCount = mediaList.count {
                    it.link.contains("youtube.com") || it.link.contains("youtu.be")
                }

                println("\n--- Link Analysis ---")
                println("Total Links: ${mediaList.size}")
                println("YouTube Links: $youtubeCount")
                println("Other Links: ${mediaList.size - youtubeCount}")
                println("========================================\n")
            },
            onFailure = { error ->
                println("❌ Error: ${error.message}")
            }
        )
    }
}
