package io.foundy.room.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.ui.media.MediaManager
import io.foundy.room.ui.peer.PeerConnectionFactoryWrapper
import io.foundy.room.ui.screen.PermissionRequestScreen
import io.foundy.room.ui.viewmodel.RoomViewModel

@AndroidEntryPoint
class RoomActivity : ComponentActivity() {

    private val viewModel: RoomViewModel by viewModels()

    private var _mediaManager: MediaManager? = null
    private val mediaManager: MediaManager get() = requireNotNull(_mediaManager)

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
}
