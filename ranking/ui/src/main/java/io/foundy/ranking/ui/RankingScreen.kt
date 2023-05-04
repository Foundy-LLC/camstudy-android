package io.foundy.ranking.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyTab
import io.foundy.core.designsystem.component.CamstudyTabRow
import io.foundy.core.designsystem.component.CamstudyText
import io.foundy.core.designsystem.theme.CamstudyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun RankingRoute() {
    val pagerState = rememberPagerState(0)

    RankingScreen(
        pagerState = pagerState
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RankingScreen(
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
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
        HorizontalPager(pageCount = RankingTabDestination.values.size, state = pagerState) { page ->
            RankingContent(tab = RankingTabDestination.values[page])
        }
    }
}

@Composable
private fun RankingContent(
    tab: RankingTabDestination
) {
    CamstudyText(text = stringResource(id = tab.labelRes))
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun RankingScreenPreview() {
    val pagerState = rememberPagerState(0)

    CamstudyTheme {
        RankingScreen(
            pagerState = pagerState
        )
    }
}
