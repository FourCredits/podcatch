package four.credits.podcatch.data

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import four.credits.podcatch.domain.PlayManager
import four.credits.podcatch.domain.PlayState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class Media3PlayManager(private val player: Player): PlayManager {
    private val currentlyPlaying: MutableStateFlow<PlayState> =
        MutableStateFlow(PlayState.NotStarted)

    override fun currentlyPlaying(): Flow<PlayState> = currentlyPlaying

    override suspend fun play(id: Long, uri: String) {
        currentlyPlaying.value.takeIf { it.playingId != id }?.run {
            player.setMediaItem(MediaItem.fromUri(uri))
        }
        playInternal(id)
    }

    override suspend fun playCurrent() =
        currentlyPlaying.value.playingId?.let { playInternal(it) } ?: Unit

    private suspend fun playInternal(id: Long) {
        player.play()
        currentlyPlaying.emit(PlayState.Playing(id))
    }

    override suspend fun pause() {
        currentlyPlaying.value.playingId?.let {
            currentlyPlaying.emit(PlayState.Paused(it))
            player.pause()
        }
    }
}
