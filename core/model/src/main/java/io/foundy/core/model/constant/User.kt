package io.foundy.core.model.constant

object UserConstants {
    const val MaxNameLength = 20
    const val MaxIntroduceLength = 100
    const val MaxTagCount = 3
    const val MaxTagLength = 20

    fun hasNameValidCharacterSet(name: String): Boolean {
        val regex = Regex("^[a-zA-Z\\d가-힣ㄱ-ㅎㅏ-ㅣ]+$")
        return regex.matches(name)
    }
}
