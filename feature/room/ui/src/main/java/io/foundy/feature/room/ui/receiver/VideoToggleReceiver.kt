package io.foundy.feature.room.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.foundy.feature.room.ui.RoomActivity

class VideoToggleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        LocalBroadcastManager.getInstance(context!!)
            .sendBroadcast(Intent(RoomActivity.VIDEO_TOGGLE_ACTION))
    }
}
