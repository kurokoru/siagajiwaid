package com.siagajiwa.siagajiwa.data.repository

import com.siagajiwa.siagajiwa.data.SupabaseClient
import com.siagajiwa.siagajiwa.data.models.MediaContent
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository {
    private val database = SupabaseClient.database

    /**
     * Fetch stress management media from stress_media table
     * Ordered by 'order' column in ascending order
     */
    suspend fun getStressMedia(): Result<List<MediaContent>> {
        return withContext(Dispatchers.IO) {
            try {
                val media = database.from("stress_media")
                    .select() {
                        order(column = "order", order = Order.ASCENDING)
                    }
                    .decodeList<MediaContent>()

                Result.success(media)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Fetch patient care media from perawatan_media table
     * Ordered by 'order' column in ascending order
     */
    suspend fun getPatientCareMedia(typePasien: String): Result<List<MediaContent>> {
        return withContext(Dispatchers.IO) {
            try {
                val media = database.from("pp_media")
                    .select() {
                        order(column = "order", order = Order.ASCENDING)
                        filter {
                            eq("folder", typePasien)
                        }
                    }
                    .decodeList<MediaContent>()

                Result.success(media)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Fetch schizophrenia insight media from skizo_media table
     * Ordered by 'order' column in ascending order
     */
    suspend fun getSchizophreniaMedia(): Result<List<MediaContent>> {
        return withContext(Dispatchers.IO) {
            try {
                val media = database.from("skizo_media")
                    .select() {
                        order(column = "order", order = Order.ASCENDING)
                    }
                    .decodeList<MediaContent>()

                Result.success(media)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
