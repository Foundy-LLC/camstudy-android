package io.foundy.core.model

enum class CropType(val requiredDay: Int, val maxLevel: Int) {
    CARROT(requiredDay = 3, maxLevel = 3),
    TOMATO(requiredDay = 5, maxLevel = 5),
    STRAWBERRY(requiredDay = 7, maxLevel = 5),
    PUMPKIN(requiredDay = 9, maxLevel = 5),
    CABBAGE(requiredDay = 11, maxLevel = 5)
}
