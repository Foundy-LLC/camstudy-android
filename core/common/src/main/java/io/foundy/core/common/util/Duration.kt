package io.foundy.core.common.util

import android.text.format.DateUtils

fun Long.formatDuration(): String = if (this < 60) {
    this.toString()
} else {
    DateUtils.formatElapsedTime(this)
}

fun Int.formatDuration(): String = this.toLong().formatDuration()
