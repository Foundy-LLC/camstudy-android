package io.foundy.friend.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyTab
import io.foundy.core.designsystem.component.CamstudyTabRow
import io.foundy.friend.ui.component.FriendListContent
import io.foundy.friend.ui.component.FriendRecommendContent
import io.foundy.friend.ui.component.RequestedFriendContent
import io.foundy.friend.ui.navigation.FriendTabDestination
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun FriendRoute(
    viewModel: FriendViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState(0)
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect {
        when (it) {
            is FriendSideEffect.Message -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.content ?: context.getString(it.defaultStringRes)
                )
            }
        }
    }

    FriendScreen(
        pagerState = pagerState,
        snackbarHostState = snackbarHostState,
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FriendScreen(
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    uiState: FriendUiState
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        val coroutineScope = rememberCoroutineScope()

        Column(Modifier.padding(padding)) {
            CamstudyTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                for (destination in FriendTabDestination.values) {
                    val index = FriendTabDestination.indexOf(destination)
                    CamstudyTab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = stringResource(id = destination.labelRes)
                    )
                }
            }
            HorizontalPager(
                pageCount = FriendTabDestination.values.size,
                state = pagerState
            ) { page ->
                when (FriendTabDestination.values[page]) {
                    FriendTabDestination.List -> FriendListContent(
                        uiState = uiState.friendListTabUiState
                    )
                    FriendTabDestination.Recommend -> FriendRecommendContent(
                        uiState = uiState.friendRecommendTabUiState
                    )
                    FriendTabDestination.Requested -> RequestedFriendContent(
                        uiState = uiState.requestedFriendTabUiState
                    )
                }
            }
        }
    }
}
