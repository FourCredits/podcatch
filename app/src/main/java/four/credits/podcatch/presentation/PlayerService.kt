package four.credits.podcatch.presentation

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.R
import four.credits.podcatch.domain.PlayManager
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class PlayerService : LifecycleService() {
    private var started = false
    private lateinit var playManager: PlayManager
    private lateinit var player: Player

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (!started) start()
        when (intent?.action) {
            Actions.Play.toString() -> play()
            Actions.Pause.toString() -> pause()
            Actions.Exit.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        playManager = (application as PodcatchApplication).playManager
        player = (application as PodcatchApplication).player
        super.onCreate()
    }

    override fun onDestroy() {
        started = false
        super.onDestroy()
    }

    private fun buildIntent(action: Actions) =
        Intent(this, PlayerService::class.java).also {
            it.action = action.toString()
        }.let {
            PendingIntent.getService(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

    private fun start() {
        // TODO: extract constants
        val mediaSession = MediaSession.Builder(this, player).build()
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            // TODO: actually display something about the episode here
            .setContentTitle("Playing episode")
            // TODO: extract string resources
            // TODO: make the pause and play buttons take the same place
            .addAction(R.drawable.play_arrow, "Play", buildIntent(Actions.Play))
            .addAction(R.drawable.pause, "Pause", buildIntent(Actions.Pause))
            .addAction(
                R.drawable.close,
                "exit player",
                buildIntent(Actions.Exit)
            )
            // TODO: once play and pause are in the same place, make this
            //  only one number
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession)
                    .setShowActionsInCompactView(0, 1)
            )
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                1,
                notification,
            )
        }
        started = true
    }

    private fun pause() {
        lifecycleScope.launch { playManager.pause() }
    }

    private fun play() {
        lifecycleScope.launch { playManager.playCurrent() }
    }

    enum class Actions { Play, Pause, Exit }

    companion object {
        const val channelId = "player"
    }
}
