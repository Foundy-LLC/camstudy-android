package io.foundy.room.ui

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.peer.PeerConnectionFactoryWrapper
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
        fun getIntent(context: Context, roomId: String): Intent {
            return Intent(context, RoomActivity::class.java).apply {
                putExtra("roomId", roomId)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    private fun updatedPipParams(): PictureInPictureParams? {
        return PictureInPictureParams.Builder()
            .setSourceRectHint(videoViewBounds)
            .setAspectRatio(Rational(16, 9))
            // TODO: Action 버튼들 추가하기
            .build()
    }

    override fun onUserLeaveHint() {
        val uiState = viewModel.container.stateFlow.value
        if (!isPipSupported || uiState !is RoomUiState.StudyRoom) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            updatedPipParams()?.let(::enterPictureInPictureMode)
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
}
