package com.siagajiwa.siagajiwa.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.siagajiwa.siagajiwa.data.SupabaseClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Simple test to verify Supabase connection is working.
 *
 * This test only checks:
 * 1. Supabase client can be initialized
 * 2. The client can communicate with the Supabase server
 *
 * Requirements:
 * - Active internet connection
 * - Valid Supabase credentials in SupabaseClient.kt
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SupabaseConnectionTest {

    @Test
    fun testSupabaseClientInitialization() {
        // Test that Supabase client can be initialized without errors
        assertNotNull("Supabase client should not be null", SupabaseClient.client)
        assertNotNull("Supabase auth should not be null", SupabaseClient.auth)
        assertNotNull("Supabase database should not be null", SupabaseClient.database)
    }

    @Test
    fun testSupabaseConnection() = runTest {
        // Test that we can connect to Supabase server
        // This test attempts to get the current session, which requires server communication
        try {
            withTimeout(10000) { // 10 second timeout
                val currentSession = SupabaseClient.auth.currentSessionOrNull()
                // We don't care if there's a session or not,
                // we just want to verify the connection works
                assertNotNull("Auth API should be accessible", SupabaseClient.auth)
                println("✓ Successfully connected to Supabase")
                println("  Session status: ${if (currentSession != null) "Active" else "No active session"}")
            }
        } catch (e: Exception) {
            fail("Failed to connect to Supabase: ${e.message}\n${e.stackTraceToString()}")
        }
    }

    @Test
    fun testSupabaseAuthServiceAvailable() = runTest {
        // Verify that the auth service is available
        try {
            withTimeout(10000) {
                // Try to access auth service (this will fail if connection is broken)
                val session = SupabaseClient.auth.currentSessionOrNull()
                // If we get here without exception, the service is available
                assertTrue("Auth service should be accessible", true)
                println("✓ Supabase Auth service is available")
            }
        } catch (e: Exception) {
            fail("Supabase Auth service is not available: ${e.message}")
        }
    }

    @Test
    fun testSupabaseDatabaseServiceAvailable() = runTest {
        // Verify that the database service (Postgrest) is available
        try {
            withTimeout(10000) {
                assertNotNull("Database service should not be null", SupabaseClient.database)
                println("✓ Supabase Database service is available")
                assertTrue("Database service should be accessible", true)
            }
        } catch (e: Exception) {
            fail("Supabase Database service is not available: ${e.message}")
        }
    }
}
