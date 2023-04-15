package io.foundy.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.foundy.core.model.CropType

// TODO: 작물 유형 별로 아이콘 반환하는 확장 함수 구현하기

@Composable
fun CropType.getName(): String {
    return stringResource(
        id = when (this) {
            CropType.STRAWBERRY -> R.string.strawberry
            CropType.TOMATO -> R.string.tomato
            CropType.CARROT -> R.string.carrot
            CropType.PUMPKIN -> R.string.pumpkin
            CropType.CABBAGE -> R.string.cabbage
        }
    )
}
