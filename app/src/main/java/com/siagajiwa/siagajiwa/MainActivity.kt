package com.siagajiwa.siagajiwa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.siagajiwa.siagajiwa.data.SupabaseClient
import com.siagajiwa.siagajiwa.ui.theme.SiagajiwaidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Supabase with application context
        SupabaseClient.init(applicationContext)
        Log.d("MainActivity", "SupabaseClient initialized")

        // Check session on app start
        checkAuthSession("onCreate")

        setContent {
            SiagajiwaidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SiagajiwaidApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check session when app resumes
        checkAuthSession("onResume")
    }

    private fun checkAuthSession(source: String) {
        val currentUser = SupabaseClient.auth.currentUserOrNull()
        Log.d("MainActivity", "[$source] Auth session check:")
        Log.d("MainActivity", "  User: ${currentUser?.email ?: "NOT LOGGED IN"}")
        Log.d("MainActivity", "  User ID: ${currentUser?.id ?: "null"}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SiagajiwaidTheme {
        SiagajiwaidApp()
    }
}