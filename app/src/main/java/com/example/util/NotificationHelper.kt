package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.GoalAlert

object NotificationHelper {
    const val CHANNEL_ID = "futbol_goal_alerts_channel"
    private const val CHANNEL_NAME = "Live Goal Alerts"
    private const val CHANNEL_DESCRIPTION = "Instant notifications when your favorite teams score goals"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250, 150, 400)
                enableLights(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun postGoalNotification(context: Context, goal: GoalAlert) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            goal.matchId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "⚽ GOOOAL! ${goal.team} (${goal.minute})"
        val content = "${goal.scorer} scores! ${goal.team} ${goal.newHomeScore} - ${goal.newAwayScore} ${goal.opponent}"
        val bigText = "🔥 GOOOAL! ${goal.scorer} scores at ${goal.minute}!\n" +
                "Live Score: ${goal.team} ${goal.newHomeScore} - ${goal.newAwayScore} ${goal.opponent}\n" +
                "Favorite team real-time alert via Futbol Pulse."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 150, 250, 150, 400))

        val notificationId = (System.currentTimeMillis() % 10000).toInt()
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled gracefully if permission revoked
        }
    }
}
