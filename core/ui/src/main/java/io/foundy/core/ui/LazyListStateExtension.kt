package io.foundy.core.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private const val VelocityThreshold = 0.8f

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) {
        mutableStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember(this) {
        mutableStateOf(firstVisibleItemScrollOffset)
    }
    var previousTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    var isScrollingUp by remember(this) {
        mutableStateOf(true)
    }
    return remember(this) {
        derivedStateOf {
            val currentTime = System.currentTimeMillis()
            val timeDelta = (currentTime - previousTime).toFloat()
            val currentScrollOffset = firstVisibleItemScrollOffset
            val scrollDelta = currentScrollOffset - previousScrollOffset
            val velocity = scrollDelta / timeDelta
            println(velocity)

            if (previousIndex != firstVisibleItemIndex) {
                isScrollingUp = previousIndex > firstVisibleItemIndex
            } else if (velocity < -VelocityThreshold) {
                isScrollingUp = true
            } else if (velocity > VelocityThreshold) {
                isScrollingUp = false
            }

            previousIndex = firstVisibleItemIndex
            previousScrollOffset = currentScrollOffset
            previousTime = currentTime

            return@derivedStateOf isScrollingUp
        }
    }.value
}
