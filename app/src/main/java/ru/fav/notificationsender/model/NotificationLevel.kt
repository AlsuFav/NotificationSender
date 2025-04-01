package ru.fav.notificationsender.model

enum class NotificationLevel(val displayName: String) {
    MAX("Max"),
    HIGH("High"),
    DEFAULT("Default"),
    LOW("Low");

    companion object {
        fun fromString(value: String): NotificationLevel {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: DEFAULT
        }
    }
}
