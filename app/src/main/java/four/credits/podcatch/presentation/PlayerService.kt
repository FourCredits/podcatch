package four.credits.podcatch.presentation

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import four.credits.podcatch.PodcatchApplication
import four.credits.podcatch.R
import four.credits.podcatch.domain.PlayManager
import kotlinx.coroutines.launch

class PlayerService() : LifecycleService() {
    private var started = false
    private lateinit var playManager: PlayManager

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
        super.onCreate()
    }

    override fun onDestroy() {
        started = false
        super.onDestroy()
    }

    private fun start() {
        // TODO: extract constants
        val notification = NotificationCompat.Builder(
            /* context = */ this,
            /* channelId = */ "player"
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Playing episode")
            .setContentText("TODO: update with currently playing thing")
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
}
