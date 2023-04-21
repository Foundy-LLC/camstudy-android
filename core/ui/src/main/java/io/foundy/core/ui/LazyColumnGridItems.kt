package io.foundy.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.foundy.core.designsystem.theme.CamstudyTheme

fun LazyListScope.gridItems(
    count: Int,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    backgroundColor: Color? = null,
    itemContent: @Composable BoxScope.(Int) -> Unit,
) {
    gridItems(
        items = List(count) { it },
        nColumns = nColumns,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
        backgroundColor = backgroundColor,
        itemContent = itemContent,
    )
}

fun <T> LazyListScope.gridItems(
    items: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((item: T) -> Any)? = null,
    backgroundColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = if (items.isEmpty()) 0 else 1 + (items.count() - 1) / nColumns

    verticalPadding(height = contentPadding.calculateTopPadding(), color = backgroundColor)
    items(rows) { rowIndex ->
        val layoutDirection = LocalLayoutDirection.current
        Row(
            modifier = Modifier
                .background(color = backgroundColor ?: CamstudyTheme.colorScheme.systemBackground)
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection)
                ),
            horizontalArrangement = horizontalArrangement
        ) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < items.count()) {
                    val item = items[itemIndex]
                    androidx.compose.runtime.key(key?.invoke(item)) {
                        Box(
                            modifier = Modifier.weight(1f, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(this, item)
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
    verticalPadding(height = contentPadding.calculateBottomPadding(), color = backgroundColor)
}

private fun LazyListScope.verticalPadding(height: Dp, color: Color? = null) {
    item {
        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .background(color = color ?: CamstudyTheme.colorScheme.systemBackground)
        )
    }
}
