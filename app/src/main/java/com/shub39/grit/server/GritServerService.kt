package com.shub39.grit.server

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shub39.grit.R
import com.shub39.grit.app.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

const val PORT = "port"

class GritServerService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    private val gritServerRepository: GritServerRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification(null))
                val port = intent.getIntExtra(PORT, 8080)

                serviceScope.launch {
                    gritServerRepository.startServer(port)
                    observeServerState()
                }
            }

            ACTION_STOP -> {
                gritServerRepository.stopServer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun observeServerState() {
        serviceScope.launch {
            gritServerRepository.isRunning.collect { isRunning ->
                if (isRunning) {
                    val serverUrl = gritServerRepository.serverUrl.first()
                    val notificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, createNotification(serverUrl))
                }
            }
        }
    }

    private fun createNotification(serverUrl: String?): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = Intent(this, GritServerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.server_running))
            .setContentText(
                if (serverUrl != null) {
                    getString(R.string.local_server_notification_text, serverUrl)
                } else {
                    getString(R.string.local_server_starting)
                }
            )
            .setSmallIcon(R.drawable.round_analytics_24)
            .setContentIntent(pendingIntent)
            .addAction(
                0,
                getString(R.string.stop),
                stopPendingIntent
            )
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.server_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = getString(R.string.server_channel_description)
                setShowBadge(false)
            }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "local_server_channel"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.shub39.grit.ACTION_START_SERVER"
        const val ACTION_STOP = "com.shub39.grit.ACTION_STOP_SERVER"

        fun startService(
            context: Context,
            port: Int,
        ) {
            val intent =
                Intent(context, GritServerService::class.java).apply {
                    action = ACTION_START
                    putExtra(PORT, port)
                }
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent =
                Intent(context, GritServerService::class.java).apply {
                    action = ACTION_STOP
                }
            context.startService(intent)
        }
    }
}