package io.foundy.feature.room.ui

import android.Manifest
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.core.common.util.serializable
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.RoomOverview
import io.foundy.feature.room.ui.media.MediaManager
import io.foundy.feature.room.ui.receiver.AudioToggleReceiver
import io.foundy.feature.room.ui.receiver.VideoToggleReceiver
import io.foundy.feature.room.ui.room.PermissionRequestScreen
import io.foundy.feature.room.ui.room.RoomScreen
import io.foundy.feature.room.ui.room.RoomUiState
import io.foundy.feature.room.ui.room.RoomViewModel

@AndroidEntryPoint
class RoomActivity : ComponentActivity() {

    private val viewModel: RoomViewModel by viewModels()
    private val mediaManager: MediaManager get() = viewModel.mediaManager

    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else false
    }

    private var videoViewBounds = Rect()

    companion object {

        const val VIDEO_TOGGLE_ACTION = "video_toggle_action"
        const val AUDIO_TOGGLE_ACTION = "audio_toggle_action"

        fun getIntent(context: Context, roomOverview: RoomOverview): Intent {
            return Intent(context, RoomActivity::class.java).apply {
                putExtra("roomOverview", roomOverview)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter().apply {
                addAction(VIDEO_TOGGLE_ACTION)
                addAction(AUDIO_TOGGLE_ACTION)
            }
        )

        val roomOverview = requireNotNull(intent.serializable<RoomOverview>("roomOverview"))

        setContent {
            CamstudyTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )

                if (permissionsState.allPermissionsGranted) {
                    RoomScreen(
                        modifier = Modifier.onGloballyPositioned {
                            videoViewBounds = run {
                                val boundsInWindow = it.boundsInWindow()
                                Rect(
                                    boundsInWindow.left.toInt(),
                                    boundsInWindow.top.toInt(),
                                    boundsInWindow.right.toInt(),
                                    boundsInWindow.bottom.toInt()
                                )
                            }
                        },
                        roomOverview = roomOverview,
                        popBackStack = ::finish,
                        viewModel = viewModel,
                        mediaManager = mediaManager,
                    )
                } else {
                    PermissionRequestScreen(
                        shouldShowRationale = permissionsState.shouldShowRationale,
                        onRequestClick = permissionsState::launchMultiplePermissionRequest
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createVideoRemoteAction(): RemoteAction {
        val enabledVideo = mediaManager.enabledLocalVideo
        val videoIcon = Icon.createWithResource(
            applicationContext,
            if (enabledVideo) {
                R.drawable.baseline_videocam_24
            } else {
                R.drawable.baseline_videocam_off_24
            }
        )
        val videoTitle = if (enabledVideo) {
            getString(R.string.turn_off_video)
        } else {
            getString(R.string.turn_on_video)
        }
        val videoPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            if (enabledVideo) 0 else 1,
            Intent(applicationContext, VideoToggleReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return RemoteAction(videoIcon, videoTitle, videoTitle, videoPendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAudioRemoteAction(): RemoteAction {
        val enabledAudio = mediaManager.enabledLocalAudio
        val audioIcon = Icon.createWithResource(
            applicationContext,
            if (enabledAudio) {
                R.drawable.baseline_mic_24
            } else {
                R.drawable.baseline_mic_off_24
            }
        )
        val audioTitle = if (enabledAudio) {
            getString(R.string.turn_off_mic)
        } else {
            getString(R.string.turn_on_mic)
        }
        val audioPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            if (enabledAudio) 0 else 1,
            Intent(applicationContext, AudioToggleReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return RemoteAction(audioIcon, audioTitle, audioTitle, audioPendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPipParams(): PictureInPictureParams? {
        return PictureInPictureParams.Builder()
            .setSourceRectHint(videoViewBounds)
            .setAspectRatio(Rational(16, 9))
            .setActions(listOf(createVideoRemoteAction(), createAudioRemoteAction()))
            .build()
    }

    private fun tryToEnterPictureInPictureMode() {
        val uiState = viewModel.container.stateFlow.value
        if (!isPipSupported || uiState !is RoomUiState.StudyRoom) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildPipParams()?.let(::enterPictureInPictureMode)
        }
    }

    override fun onUserLeaveHint() {
        tryToEnterPictureInPictureMode()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.updatePictureInPictureMode(isPipMode = isInPictureInPictureMode)
        // When user click the close button in PIP
        if (!isInPictureInPictureMode && lifecycle.currentState == Lifecycle.State.CREATED) {
            mediaManager.disconnect()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private fun updatePictureInPictureParams() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildPipParams()?.let(::setPictureInPictureParams)
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                VIDEO_TOGGLE_ACTION -> {
                    mediaManager.toggleVideo(!mediaManager.enabledLocalVideo)
                    updatePictureInPictureParams()
                }
                AUDIO_TOGGLE_ACTION -> {
                    mediaManager.toggleMicrophone(!mediaManager.enabledLocalAudio)
                    updatePictureInPictureParams()
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
