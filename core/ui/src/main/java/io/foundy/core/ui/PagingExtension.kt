package io.foundy.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private const val InitFetchFlag = Long.MIN_VALUE

@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(
    context: CoroutineContext = EmptyCoroutineContext
): Pair<LazyPagingItems<T>, () -> Unit> {
    var fetchFlag by remember { mutableStateOf(InitFetchFlag) }
    val refresh: () -> Unit = { fetchFlag += 1 }
    val lazyPagingItems = collectAsLazyPagingItems(context = context)

    LaunchedEffect(fetchFlag) {
        launch(Dispatchers.Main) {
            if (fetchFlag != InitFetchFlag) {
                lazyPagingItems.retry()
            }
        }
    }

    return lazyPagingItems to refresh
}
