package com.smartremote.pro.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smartremote.pro.domain.models.Channel

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey
    val number: Int,
    val name: String,
    val category: String,
    val language: String,
    val logoAsset: String,
    val isFavorite: Boolean,
    val lastWatchedTimestamp: Long?
) {
    fun toDomain(): Channel {
        return Channel(
            number = number,
            name = name,
            category = category,
            language = language,
            logoAsset = logoAsset,
            isFavorite = isFavorite,
            lastWatchedTimestamp = lastWatchedTimestamp
        )
    }

    companion object {
        fun fromDomain(channel: Channel): ChannelEntity {
            return ChannelEntity(
                number = channel.number,
                name = channel.name,
                category = channel.category,
                language = channel.language,
                logoAsset = channel.logoAsset,
                isFavorite = channel.isFavorite,
                lastWatchedTimestamp = channel.lastWatchedTimestamp
            )
        }
    }
}
