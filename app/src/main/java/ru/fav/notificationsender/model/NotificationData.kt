package ru.fav.notificationsender.model

data class NotificationData (
    val id: Int,
    val title: String,
    val message: String,
    val notificationType: NotificationType
)