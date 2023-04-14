package io.foundy.home.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.foundy.dashboard.ui.DashboardRoute
import io.foundy.room_list.ui.RoomListRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun MainTabRoute(
    navigator: DestinationsNavigator,
) {
    val pagerState = rememberPagerState(0)

    HomeTabScreen(pagerState = pagerState, navigator = navigator)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeTabScreen(
    pagerState: PagerState,
    navigator: DestinationsNavigator
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            for (destination in MainTabDestination.values) {
                val index = MainTabDestination.indexOf(destination)
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = stringResource(id = destination.labelRes)) }
                )
            }
        }
        HorizontalPager(pageCount = MainTabDestination.values.size, state = pagerState) { page ->
            when (MainTabDestination.values[page]) {
                MainTabDestination.Dashboard -> DashboardRoute()
                MainTabDestination.StudyRooms -> RoomListRoute(navigator = navigator)
            }
        }
    }
}
