package io.foundy.ranking.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyTab
import io.foundy.core.designsystem.component.CamstudyTabRow
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.ui.UserProfileDialog
import io.foundy.ranking.ui.component.RankingTile
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun RankingRoute(
    viewModel: RankingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(0)
    val uiState = viewModel.collectAsState().value
    var shouldShowUserProfileDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    viewModel.collectSideEffect {
        when (it) {
            is RankingSideEffect.ErrorMessage -> coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    it.message.content ?: context.getString(it.message.defaultRes)
                )
            }
            is RankingSideEffect.HideUserProfileDialog -> {
                shouldShowUserProfileDialog = false
            }
        }
    }

    RankingScreen(
        pagerState = pagerState,
        snackbarHostState = snackbarHostState,
        shouldShowUserProfileDialog = shouldShowUserProfileDialog,
        showUserProfileDialog = { shouldShowUserProfileDialog = true },
        hideUserProfileDialog = { shouldShowUserProfileDialog = false },
        uiState = uiState
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    pagerState: PagerState,
    snackbarHostState: SnackbarHostState,
    shouldShowUserProfileDialog: Boolean,
    showUserProfileDialog: () -> Unit,
    hideUserProfileDialog: () -> Unit,
    uiState: RankingUiState
) {
    val coroutineScope = rememberCoroutineScope()

    if (shouldShowUserProfileDialog) {
        UserProfileDialog(
            user = uiState.userToShowDialog,
            onCancel = hideUserProfileDialog
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CamstudyTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                for (destination in RankingTabDestination.values) {
                    val index = RankingTabDestination.indexOf(destination)
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
                pageCount = RankingTabDestination.values.size,
                state = pagerState
            ) { page ->
                RankingContent(
                    tab = RankingTabDestination.values[page],
                    uiState = uiState,
                    onClickUser = { id ->
                        showUserProfileDialog()
                        uiState.onClickUser(id)
                    }
                )
            }
        }
    }
}

@Composable
private fun RankingContent(
    tab: RankingTabDestination,
    uiState: RankingUiState,
    onClickUser: (id: String) -> Unit
) {
    val (users, currentUser, isLoading) = when (tab) {
        RankingTabDestination.Total -> Triple(
            uiState.totalRanking.rankingFlow.collectAsLazyPagingItems(),
            uiState.totalRanking.currentUserRanking,
            uiState.totalRanking.isCurrentUserRankingLoading
        )
        RankingTabDestination.Weekly -> Triple(
            uiState.weeklyRanking.rankingFlow.collectAsLazyPagingItems(),
            uiState.weeklyRanking.currentUserRanking,
            uiState.weeklyRanking.isCurrentUserRankingLoading
        )
        RankingTabDestination.Organization -> Triple(
            // TODO: 소속 랭킹 데이터로 바꾸기
            uiState.totalRanking.rankingFlow.collectAsLazyPagingItems(),
            uiState.totalRanking.currentUserRanking,
            uiState.totalRanking.isCurrentUserRankingLoading
        )
    }

    LaunchedEffect(Unit) {
        when (tab) {
            RankingTabDestination.Total -> {
                if (uiState.totalRanking.shouldFetchCurrentUserRanking) {
                    uiState.totalRanking.fetchCurrentUserRanking()
                }
            }
            RankingTabDestination.Weekly -> {
                if (uiState.weeklyRanking.shouldFetchCurrentUserRanking) {
                    uiState.weeklyRanking.fetchCurrentUserRanking()
                }
            }
            RankingTabDestination.Organization -> {
                // TODO: 구현
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            item { CircularProgressIndicator() }
        } else {
            item {
                if (currentUser != null) {
                    RankingTile(user = currentUser, isMe = true, onClick = onClickUser)
                }
            }
            items(users, key = { it.id }) { user ->
                if (user == null) {
                    return@items
                }
                RankingTile(user = user, onClick = onClickUser)
            }
        }
        // TODO: 로딩, 에러 보이기
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun RankingScreenPreview() {
    val pagerState = rememberPagerState(0)

    CamstudyTheme {
        RankingScreen(
            pagerState = pagerState,
            uiState = RankingUiState(
                totalRanking = RankingTabUiState(fetchCurrentUserRanking = {}),
                weeklyRanking = RankingTabUiState(fetchCurrentUserRanking = {}),
                onClickUser = {}
            ),
            shouldShowUserProfileDialog = false,
            showUserProfileDialog = {},
            hideUserProfileDialog = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
