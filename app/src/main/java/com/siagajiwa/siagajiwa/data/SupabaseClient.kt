package com.siagajiwa.siagajiwa.data

import android.content.Context
import android.util.Log
import com.siagajiwa.siagajiwa.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object SupabaseClient {
    // Supabase credentials are loaded from .env file via BuildConfig
    // See .env.example for required variables
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY

    // Application context for session persistence
    private var applicationContext: Context? = null

    // Coroutine scope for session monitoring
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun init(context: Context) {
        applicationContext = context.applicationContext
        Log.d("SupabaseClient", "Initialized with application context")

        // Monitor session status changes
        scope.launch {
            auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        Log.d("SupabaseClient", "Session Status: AUTHENTICATED")
                        Log.d("SupabaseClient", "  User: ${status.session.user?.email}")
                        Log.d("SupabaseClient", "  User ID: ${status.session.user?.id}")
                        Log.d("SupabaseClient", "  Access Token: ${status.session.accessToken.take(20)}...")
                    }
                    is SessionStatus.LoadingFromStorage -> {
                        Log.d("SupabaseClient", "Session Status: LOADING FROM STORAGE")
                    }
                    is SessionStatus.NetworkError -> {
                        Log.e("SupabaseClient", "Session Status: NETWORK ERROR")
                    }
                    is SessionStatus.NotAuthenticated -> {
                        Log.w("SupabaseClient", "Session Status: NOT AUTHENTICATED")
                        if (status.isSignOut) {
                            Log.d("SupabaseClient", "  User signed out")
                        } else {
                            Log.d("SupabaseClient", "  No session found")
                        }
                    }
                }
            }
        }
    }

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            flowType = FlowType.PKCE
            scheme = "app"
            host = "supabase.com"
            // Session will be automatically persisted using platform-specific storage
            // On Android, this uses SharedPreferences
        }
        install(Postgrest)
    }

    val auth get() = client.auth
    val database get() = client.postgrest
}
