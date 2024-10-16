package com.squad.update.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.squad.update.sync.R
import com.squad.update.core.designsystem.R as designSystemR

// All sync work needs an internet connection.
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType( NetworkType.CONNECTED )
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being run with a
 * foreground service.
 */
fun Context.syncForegroundInfo() = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification()
)

/**
 * Notification displayed on lower API levels when sync workers are being run with a foreground
 * service.
 */
private fun Context.syncWorkNotification(): Notification {
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
        val channel = NotificationChannel(
            SYNC_NOTIFICATION_CHANNEL_ID,
            getString( R.string.sync_work_notification_channel_name ),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString( R.string.sync_work_notification_channel_description )
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager? =
            getSystemService( Context.NOTIFICATION_SERVICE ) as? NotificationManager
        notificationManager?.createNotificationChannel( channel )
    }

    return NotificationCompat.Builder(
        this,
        SYNC_NOTIFICATION_CHANNEL_ID
    ).setContentTitle( getString( R.string.sync_work_notification_title ) )
        .setSmallIcon( designSystemR.drawable.core_designsystem_ic_placeholder_default )
        .setPriority( NotificationCompat.PRIORITY_DEFAULT )
        .build()
}

private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"
