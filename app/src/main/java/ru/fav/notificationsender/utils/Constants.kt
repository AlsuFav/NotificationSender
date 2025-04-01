package ru.fav.notificationsender.utils

import android.app.NotificationManager
import ru.fav.notificationsender.R
import ru.fav.notificationsender.model.NotificationChannelData

object Constants {
    val notificationsChannelsData = listOf(
        NotificationChannelData(
            id = "low_channel_id",
            name = R.string.low_importance.toString(),
            importance = NotificationManager.IMPORTANCE_LOW,
        ),
        NotificationChannelData(
            id = "default_channel_id",
            name = R.string.default_importance.toString(),
            importance = NotificationManager.IMPORTANCE_DEFAULT,
        ),
        NotificationChannelData(
            id = "private_channel_id",
            name = R.string.high_importance.toString(),
            importance = NotificationManager.IMPORTANCE_HIGH,
        ),
        NotificationChannelData(
            id = "urgent_channel_id",
            name = R.string.max_importance.toString(),
            importance = NotificationManager.IMPORTANCE_MAX,
        )
    )
}