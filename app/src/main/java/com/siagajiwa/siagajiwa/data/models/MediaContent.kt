package com.siagajiwa.siagajiwa.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaContent(
    @SerialName("id")
    val id: Int,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("link")
    val link: String,

    @SerialName("order")
    val order: Int
)
