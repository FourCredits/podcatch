package four.credits.podcatch.presentation

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
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
    private lateinit var playManager: PlayManager
    private lateinit var player: Player
    private lateinit var session: MediaSession

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {
        updateNotification()
        when (intent?.action) {
            Actions.Play.toString() -> launch { playManager.playCurrent() }
            Actions.Pause.toString() -> launch { playManager.pause() }
            Actions.Close.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun launch(action: suspend () -> Unit) {
        lifecycleScope.launch { action() }
    }

    override fun onCreate() {
        playManager = (application as PodcatchApplication).playManager
        player = (application as PodcatchApplication).player
        session = MediaSession.Builder(this, player).build()
        super.onCreate()
    }

    private fun updateNotification() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                playerNotificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(playerNotificationId, notification)
        }
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, playerChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            // TODO: actually display something about the episode here
            .setContentTitle("Playing episode")
            // TODO: make the pause and play buttons take the same place
            .addActions(actions())
            // TODO: once play and pause are in the same place, make this
            //  only one number
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(session)
                    .setShowActionsInCompactView(0, 1)
            ).build()

    private fun actions(): List<Triple<Int, CharSequence?, PendingIntent?>> {
        val playAction = Triple(
            R.drawable.play_arrow,
            getString(R.string.play_episode),
            buildIntent(Actions.Play),
        )
        val pauseAction = Triple(
            R.drawable.pause,
            getString(R.string.pause_episode),
            buildIntent(Actions.Pause),
        )
        val closeAction = Triple(
            R.drawable.close,
            getString(R.string.close_player),
            buildIntent(Actions.Close),
        )
        return listOf(playAction, pauseAction, closeAction)
    }

    private fun buildIntent(action: Actions) =
        Intent(this, PlayerService::class.java).also {
            it.action = action.toString()
        }.let {
            PendingIntent.getService(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

    enum class Actions { Play, Pause, Close }
}

private fun NotificationCompat.Builder.addActions(
    actions: Iterable<Triple<Int, CharSequence?, PendingIntent?>>,
) = this.apply { actions.forEach { it.applyTo(::addAction) } }

private fun <A, B, C, D> Triple<A, B, C>.applyTo(f: (A, B, C) -> D): D =
    f(this.first, this.second, this.third)
