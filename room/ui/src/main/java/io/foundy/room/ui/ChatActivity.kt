package io.foundy.room.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.example.domain.ChatMessage
import dagger.hilt.android.AndroidEntryPoint
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.room.ui.screen.ChatScreen
import io.foundy.room.ui.viewmodel.ChatViewModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    companion object {
        fun getIntent(context: Context, chatMessages: List<ChatMessage>): Intent {
            return Intent(context, ChatActivity::class.java).apply {
                putExtra("chatMessages", Json.encodeToString(chatMessages))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val chatMessages: List<ChatMessage> = Json.decodeFromString(
            requireNotNull(intent.getStringExtra("chatMessages"))
        )
        viewModel.bind(chatMessages = chatMessages)

        setContent {
            CamstudyTheme {
                ChatScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
