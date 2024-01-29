package io.foundy.ranking.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import io.found.user.ui.UserProfileDialog
import io.foundy.core.designsystem.component.CamstudyDivider
import io.foundy.core.designsystem.component.CamstudyFilterChip
import io.foundy.core.designsystem.component.CamstudyTab
import io.foundy.core.designsystem.component.CamstudyTabRow
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import io.foundy.core.model.OrganizationOverview
import io.foundy.core.ui.pullrefresh.RefreshableContent
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
    var clickedUserId: String? by remember { mutableStateOf(null) }
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
        }
    }

    RankingScreen(
        pagerState = pagerState,
        snackbarHostState = snackbarHostState,
        clickedUserId = clickedUserId,
        showUserProfileDialog = { clickedUserId = it },
        hideUserProfileDialog = { clickedUserId = null },
        uiState = uiState
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    pagerState: PagerState,
    snackbarHostState: SnackbarHostState,
    clickedUserId: String?,
    showUserProfileDialog: (id: String) -> Unit,
    hideUserProfileDialog: () -> Unit,
    uiState: RankingUiState
) {
    val coroutineScope = rememberCoroutineScope()

    if (clickedUserId != null) {
        UserProfileDialog(
            userId = clickedUserId,
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
            if (uiState.organizations.isNotEmpty()) {
                OrganizationChipRow(
                    organizations = uiState.organizations,
                    selectedOrganization = uiState.selectedOrganization,
                    onOrganizationClick = uiState.onSelectOrganization
                )
            }
            HorizontalPager(
                pageCount = RankingTabDestination.values.size,
                state = pagerState
            ) { page ->
                RankingContent(
                    uiState = uiState.getCurrentTabUiStateBy(RankingTabDestination.values[page]),
                    onClickUser = showUserProfileDialog
                )
            }
        }
    }
}

@Composable
private fun RankingContent(
    uiState: RankingTabUiState,
    onClickUser: (id: String) -> Unit,
) {
    val users = uiState.rankingFlow.collectAsLazyPagingItems()
    val currentUser = uiState.currentUserRanking
    val isLoading = uiState.isCurrentUserRankingLoading ||
        users.loadState.refresh is LoadState.Loading
    val titleTextStyle = CamstudyTheme.typography.titleMedium.copy(
        color = CamstudyTheme.colorScheme.systemUi07,
        fontWeight = FontWeight.Normal
    )

    LaunchedEffect(Unit) {
        if (uiState.shouldFetchRanking) {
            uiState.fetchRanking()
        }
    }

    RefreshableContent(
        modifier = Modifier.fillMaxSize(),
        refreshing = isLoading,
        onRefresh = {
            uiState.fetchRanking()
            users.refresh()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CamstudyTheme.colorScheme.systemBackground)
        ) {
            item {
                CamstudyDivider()
            }
            if (currentUser != null) {
                item {
                    CamstudyText(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        text = stringResource(R.string.my_ranking_title),
                        style = titleTextStyle
                    )
                    CamstudyDivider()
                    RankingTile(user = currentUser, isMe = true, onClick = onClickUser)
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                            .background(color = CamstudyTheme.colorScheme.systemUi01)
                    )
                }
            }
            item {
                CamstudyText(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = stringResource(R.string.ranking_list_title),
                    style = titleTextStyle
                )
                CamstudyDivider()
            }
            items(count = users.itemCount, key = users.itemKey { it.id }) { index ->
                val user = users[index] ?: return@items
                RankingTile(user = user, onClick = onClickUser)
            }
            // TODO: 에러 보이기
        }
    }
}

@Composable
private fun OrganizationChipRow(
    selectedOrganization: OrganizationOverview?,
    organizations: List<OrganizationOverview>,
    onOrganizationClick: (OrganizationOverview?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = CamstudyTheme.colorScheme.systemBackground),
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 16.dp)
    ) {
        item {
            CamstudyFilterChip(
                selected = selectedOrganization == null,
                onClick = { onOrganizationClick(null) },
                label = {
                    CamstudyText(text = stringResource(R.string.entire))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        itemsIndexed(organizations, key = { _, item -> item.id }) { index, organization ->
            CamstudyFilterChip(
                selected = selectedOrganization == organization,
                onClick = { onOrganizationClick(organization) },
                label = {
                    CamstudyText(text = organization.name)
                }
            )
            if (index != organizations.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
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
                totalRanking = RankingTabUiState(fetchRanking = {}),
                weeklyRanking = RankingTabUiState(fetchRanking = {}),
                onSelectOrganization = {}
            ),
            clickedUserId = null,
            showUserProfileDialog = {},
            hideUserProfileDialog = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}
