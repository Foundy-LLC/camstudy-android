package io.foundy.room.ui

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.peer.PeerConnectionFactoryWrapper
import io.foundy.room.ui.receiver.VideoToggleReceiver
import io.foundy.room.ui.screen.PermissionRequestScreen
import io.foundy.room.ui.viewmodel.RoomUiState
import io.foundy.room.ui.viewmodel.RoomViewModel

@AndroidEntryPoint
class RoomActivity : ComponentActivity() {

    private val viewModel: RoomViewModel by viewModels()

    private var _mediaManager: MediaManager? = null
    private val mediaManager: MediaManager get() = requireNotNull(_mediaManager)

    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else false
    }

    private var videoViewBounds = Rect()

    companion object {

        const val VIDEO_TOGGLE_ACTION = "video_toggle_action"

        fun getIntent(context: Context, roomId: String): Intent {
            return Intent(context, RoomActivity::class.java).apply {
                putExtra("roomId", roomId)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter(VIDEO_TOGGLE_ACTION)
        )

        val id = requireNotNull(intent.getStringExtra("roomId"))
        _mediaManager = MediaManager(
            context = this,
            peerConnectionFactory = PeerConnectionFactoryWrapper(context = this),
            onToggleVideo = viewModel::onToggleVideo,
            onToggleAudio = viewModel::onToggleAudio,
            onToggleHeadset = viewModel::onToggleHeadset,
        )

        setContent {
            CamstudyTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )

                if (permissionsState.allPermissionsGranted) {
                    RoomContent(
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
                        id = id,
                        popBackStack = ::finish,
                        viewModel = viewModel,
                        mediaManager = mediaManager
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
    private fun buildPipParams(): PictureInPictureParams? {
        val enabled = mediaManager.enabledLocalVideo
        val icon = Icon.createWithResource(
            applicationContext,
            if (enabled) R.drawable.baseline_videocam_24 else R.drawable.baseline_videocam_off_24
        )
        val title = getString(if (enabled) R.string.turn_off_video else R.string.turn_on_video)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            if (enabled) 0 else 1,
            Intent(applicationContext, VideoToggleReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return PictureInPictureParams.Builder()
            .setSourceRectHint(videoViewBounds)
            .setAspectRatio(Rational(16, 9))
            .setActions(
                listOf(
                    RemoteAction(icon, title, title, pendingIntent)
                )
            )
            .build()
    }

    override fun onUserLeaveHint() {
        val uiState = viewModel.container.stateFlow.value
        if (!isPipSupported || uiState !is RoomUiState.StudyRoom) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildPipParams()?.let(::enterPictureInPictureMode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.updatePictureInPictureMode(isPipMode = isInPictureInPictureMode)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                VIDEO_TOGGLE_ACTION -> {
                    mediaManager.toggleVideo(!mediaManager.enabledLocalVideo)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        buildPipParams()?.let(::setPictureInPictureParams)
                    }
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}
