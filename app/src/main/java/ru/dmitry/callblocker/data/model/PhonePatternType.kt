package ru.dmitry.callblocker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class PhonePatternType(
    val pattern: String,
    val displayName: String
) {
    RUSSIAN_MOBILE_PLUS("+# (###) ###-##-##", "+# (###) ###-##-##"),
    RUSSIAN_MOBILE("# (###) ###-##-##", "# (###) ###-##-##"),
    RUSSIAN_TOLL_FREE("8 (800) ###-##-##", "8 (800) ###-##-##"),
    GENERIC("###-###-###-###", "###-###-###-###");

    companion object {
        fun fromPattern(pattern: String): PhonePatternType? {
            return entries.find { it.pattern == pattern }
        }
    }
}