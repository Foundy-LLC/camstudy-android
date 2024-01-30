package io.foundy.feature.home.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import io.foundy.core.designsystem.component.CamstudyTab
import io.foundy.core.designsystem.component.CamstudyTabRow
import io.foundy.feature.crop.ui.destinations.PlantCropRouteDestination
import io.foundy.feature.dashboard.ui.DashboardRoute
import io.foundy.feature.room_list.ui.RoomListRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun MainTabRoute(
    navigator: DestinationsNavigator,
    plantResultRecipient: OpenResultRecipient<Boolean>,
    navigateToCropTab: () -> Unit,
    navigateToRankingTab: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    val pagerState = rememberPagerState(0)

    HomeTabScreen(
        pagerState = pagerState,
        navigator = navigator,
        plantResultRecipient = plantResultRecipient,
        navigateToCropTab = navigateToCropTab,
        navigateToRankingTab = navigateToRankingTab,
        showSnackbar = showSnackbar
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTabScreen(
    pagerState: PagerState,
    plantResultRecipient: OpenResultRecipient<Boolean>,
    navigator: DestinationsNavigator,
    navigateToCropTab: () -> Unit,
    navigateToRankingTab: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        CamstudyTabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            for (destination in MainTabDestination.values) {
                val index = MainTabDestination.indexOf(destination)
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
        HorizontalPager(pageCount = MainTabDestination.values.size, state = pagerState) { page ->
            when (MainTabDestination.values[page]) {
                MainTabDestination.Dashboard -> DashboardRoute(
                    navigateToCropTab = navigateToCropTab,
                    navigateToPlantCrop = { navigator.navigate(PlantCropRouteDestination) },
                    navigateToRankingTab = navigateToRankingTab,
                    plantResultRecipient = plantResultRecipient,
                    showSnackbar = showSnackbar
                )
                MainTabDestination.StudyRooms -> RoomListRoute(navigator = navigator)
            }
        }
    }
}
