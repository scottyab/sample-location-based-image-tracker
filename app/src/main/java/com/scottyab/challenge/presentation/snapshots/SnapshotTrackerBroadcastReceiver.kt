package com.scottyab.challenge.presentation.snapshots

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.scottyab.challenge.domain.SnapshotTracker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SnapshotTrackerBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val snapshotTracker by inject<SnapshotTracker>()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_STOP_TRACKER) {
            snapshotTracker.stop()
        }
    }

    companion object {

        private const val STOP_PENDING_INTENT_RC = 1
        private const val ACTION_STOP_TRACKER = "ACTION_STOP_TRACKER"

        fun createStopPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
            context,
            STOP_PENDING_INTENT_RC,
            Intent(context, SnapshotTrackerBroadcastReceiver::class.java).apply {
                action = ACTION_STOP_TRACKER
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
