package com.scottyab.challenge.data.datasource.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.scottyab.challenge.R
import com.scottyab.challenge.presentation.common.AndroidResources
import com.scottyab.challenge.presentation.snapshots.SnapshotTrackerBroadcastReceiver
import com.scottyab.challenge.presentation.snapshots.SnapshotsActivity

class LocationServiceNotificationHelper(
    private val context: Context,
    private val androidResources: AndroidResources,
    private val notificationManager: NotificationManager
) {

    fun createNotification(): Notification {
        createChannelIfNeeded()

        val openSnapshotsActivityPendingIntent =
            SnapshotsActivity.openFromNotificationPendingIntent(context)

        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_notification_clear_all,
            androidResources.string(R.string.notification_action_stop),
            SnapshotTrackerBroadcastReceiver.createStopPendingIntent(context)
        ).build()

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle(androidResources.string(R.string.notification_title))
            .setContentInfo(androidResources.string(R.string.notification_content))
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(openSnapshotsActivityPendingIntent)
            .setSmallIcon(IconCompat.createWithResource(context, R.drawable.ic_notification_location))
            .addAction(stopAction)
            .build()
    }

    private fun createChannelIfNeeded() {
        if (notificationManager.notificationChannels.any { it?.id == NOTIFICATION_CHANNEL_ID }) return

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            androidResources.string(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "LOCATION_NOTIFICATION_CHANNEL"
    }
}
