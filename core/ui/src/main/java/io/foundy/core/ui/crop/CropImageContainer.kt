package io.foundy.core.ui.crop

import androidx.annotation.DrawableRes
import io.foundy.core.designsystem.icon.CamstudyIcon
import io.foundy.core.designsystem.icon.asCamstudyIcon
import io.foundy.core.model.CropGrade
import io.foundy.core.model.CropType

internal class CropImageContainer(
    type: CropType,
    growingDrawables: List<Int>,
    @DrawableRes notFreshDrawable: Int,
    @DrawableRes freshDrawable: Int,
    @DrawableRes silverDrawable: Int,
    @DrawableRes goldDrawable: Int,
    @DrawableRes diamondDrawable: Int
) {

    private val icons = growingDrawables.map { it.asCamstudyIcon() }
    private val notFreshIcon = notFreshDrawable.asCamstudyIcon()
    private val freshIcon = freshDrawable.asCamstudyIcon()
    private val silverIcon = silverDrawable.asCamstudyIcon()
    private val goldIcon = goldDrawable.asCamstudyIcon()
    private val diamondIcon = diamondDrawable.asCamstudyIcon()

    init {
        require(growingDrawables.size == type.maxLevel)
    }

    val maxGrowingLevelIcon: CamstudyIcon
        get() = icons.last()

    fun getGrowingImageBy(level: Int): CamstudyIcon {
        val index = level - 1
        require(index < icons.size) {
            "인덱스를 초과하는 레벨이 전달되었습니다. 이미지 리소스의 갯수를 확인하거나 레벨 값이 맞는 지 확인하세요."
        }
        return icons[index]
    }

    fun getHarvestedImageBy(grade: CropGrade): CamstudyIcon {
        return when (grade) {
            CropGrade.NOT_FRESH -> notFreshIcon
            CropGrade.FRESH -> freshIcon
            CropGrade.SILVER -> silverIcon
            CropGrade.GOLD -> goldIcon
            CropGrade.DIAMOND -> diamondIcon
        }
    }
}
