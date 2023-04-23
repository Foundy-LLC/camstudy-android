package io.foundy.core.ui.crop

import io.foundy.core.model.CropType
import io.foundy.core.ui.R

internal val CropType.cropImageContainer
    get() = when (this) {
        CropType.CARROT -> CarrotImage
        CropType.TOMATO -> TomatoImage
        CropType.STRAWBERRY -> StrawberryImage
        CropType.PUMPKIN -> PumpkinImage
        CropType.CABBAGE -> CabbageImage
    }

private val CarrotImage = CropImageContainer(
    type = CropType.CARROT,
    growingDrawables = listOf(
        R.drawable.plant_carrot_1,
        R.drawable.plant_carrot_2,
        R.drawable.plant_carrot_3,
    ),
    // TODO: 등급별 작물 이미지 나오면 수정하기
    notFreshDrawable = R.drawable.carrot_fresh,
    freshDrawable = R.drawable.carrot_fresh,
    silverDrawable = R.drawable.carrot_fresh,
    goldDrawable = R.drawable.carrot_fresh,
    diamondDrawable = R.drawable.carrot_fresh
)

private val TomatoImage = CropImageContainer(
    type = CropType.TOMATO,
    growingDrawables = listOf(
        R.drawable.plant_tomato_1,
        R.drawable.plant_tomato_2,
        R.drawable.plant_tomato_3,
        R.drawable.plant_tomato_4,
        R.drawable.plant_tomato_5
    ),
    // TODO: 등급별 작물 이미지 나오면 수정하기
    notFreshDrawable = R.drawable.tomato_fresh,
    freshDrawable = R.drawable.tomato_fresh,
    silverDrawable = R.drawable.tomato_fresh,
    goldDrawable = R.drawable.tomato_fresh,
    diamondDrawable = R.drawable.tomato_fresh
)

private val StrawberryImage = CropImageContainer(
    type = CropType.STRAWBERRY,
    growingDrawables = listOf(
        R.drawable.plant_strawberry_1,
        R.drawable.plant_strawberry_2,
        R.drawable.plant_strawberry_3,
        R.drawable.plant_strawberry_4,
        R.drawable.plant_strawberry_5
    ),
    // TODO: 등급별 작물 이미지 나오면 수정하기
    notFreshDrawable = R.drawable.strawberry_fresh,
    freshDrawable = R.drawable.strawberry_fresh,
    silverDrawable = R.drawable.strawberry_fresh,
    goldDrawable = R.drawable.strawberry_fresh,
    diamondDrawable = R.drawable.strawberry_fresh
)

private val PumpkinImage = CropImageContainer(
    type = CropType.PUMPKIN,
    growingDrawables = listOf(
        R.drawable.plant_pumpkin_1,
        R.drawable.plant_pumpkin_2,
        R.drawable.plant_pumpkin_3,
        R.drawable.plant_pumpkin_4,
        R.drawable.plant_pumpkin_5
    ),
    // TODO: 등급별 작물 이미지 나오면 수정하기
    notFreshDrawable = R.drawable.pumpkin_fresh,
    freshDrawable = R.drawable.pumpkin_fresh,
    silverDrawable = R.drawable.pumpkin_fresh,
    goldDrawable = R.drawable.pumpkin_fresh,
    diamondDrawable = R.drawable.pumpkin_fresh
)

private val CabbageImage = CropImageContainer(
    type = CropType.CABBAGE,
    growingDrawables = listOf(
        R.drawable.plant_cabbage_1,
        R.drawable.plant_cabbage_2,
        R.drawable.plant_cabbage_3,
        R.drawable.plant_cabbage_4,
        R.drawable.plant_cabbage_5
    ),
    // TODO: 등급별 작물 이미지 나오면 수정하기
    notFreshDrawable = R.drawable.cabbage_fresh,
    freshDrawable = R.drawable.cabbage_fresh,
    silverDrawable = R.drawable.cabbage_fresh,
    goldDrawable = R.drawable.cabbage_fresh,
    diamondDrawable = R.drawable.cabbage_fresh
)
