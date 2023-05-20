package io.foundy.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.foundy.core.ui.R

@Composable
fun Long.secToHourMinuteFormat(): String {
    val hours = this / 60 / 60
    val minutes = this / 60 % 60
    var result = ""

    if (hours > 0) {
        result += stringResource(R.string.hours_format, hours)
    }
    if (minutes > 0) {
        if (hours > 0) {
            result += " "
        }
        result += stringResource(R.string.minutes_format, minutes)
    }
    return result
}

@Composable
fun Int.secToHourMinuteFormat(): String = this.toLong().secToHourMinuteFormat()
