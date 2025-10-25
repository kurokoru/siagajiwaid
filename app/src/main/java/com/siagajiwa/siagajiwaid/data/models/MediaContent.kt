package com.siagajiwa.siagajiwaid.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaContent(
    @SerialName("id")
    val id: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("link")
    val link: String,

    @SerialName("order")
    val order: Int
)
