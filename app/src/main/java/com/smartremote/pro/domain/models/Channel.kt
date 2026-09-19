package com.smartremote.pro.domain.models

data class Channel(
    val number: Int,
    val name: String,
    val category: String,
    val language: String = "All",
    val logoAsset: String = "",
    val isFavorite: Boolean = false,
    val lastWatchedTimestamp: Long? = null,
    val currentProgram: String? = null,
    val programTimeRange: String? = null
)
