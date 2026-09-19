package com.smartremote.pro.domain.models

data class RemoteCommand(
    val id: String,
    val name: String,
    val hexCode: String? = null,
    val prontoCode: String? = null,
    val frequency: Int = 38000,
    val pattern: IntArray? = null,
    val networkEndpoint: String? = null,
    val payloadJson: String? = null,
    val description: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoteCommand

        if (id != other.id) return false
        if (name != other.name) return false
        if (hexCode != other.hexCode) return false
        if (frequency != other.frequency) return false
        if (pattern != null) {
            if (other.pattern == null) return false
            if (!pattern.contentEquals(other.pattern)) return false
        } else if (other.pattern != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (hexCode?.hashCode() ?: 0)
        result = 31 * result + frequency
        result = 31 * result + (pattern?.contentHashCode() ?: 0)
        return result
    }
}
