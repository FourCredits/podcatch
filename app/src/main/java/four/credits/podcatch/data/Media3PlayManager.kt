package four.credits.podcatch.data

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import four.credits.podcatch.domain.Episode
import four.credits.podcatch.domain.PlayManager
import four.credits.podcatch.domain.PlayState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(UnstableApi::class)
class Media3PlayManager(
    private val context: Context,
    private val player: Player
): PlayManager {
    private val currentlyPlaying: MutableStateFlow<PlayState> =
        MutableStateFlow(PlayState.NotStarted)

    override fun currentlyPlaying(): StateFlow<PlayState> = currentlyPlaying

    override suspend fun play(episode: Episode) {
        if (currentlyPlaying.value.playingId != episode.id) {
            val download = getDownload(context, episode.link) ?: return
            val mediaItem = download.request.toMediaItem()
            player.setMediaItem(mediaItem)
        }
        player.play()
        currentlyPlaying.emit(PlayState.Playing(episode.id))
    }

    override suspend fun pause() {
        val id = currentlyPlaying.value.playingId ?: return
        currentlyPlaying.emit(PlayState.Paused(id))
        player.pause()
    }
}
