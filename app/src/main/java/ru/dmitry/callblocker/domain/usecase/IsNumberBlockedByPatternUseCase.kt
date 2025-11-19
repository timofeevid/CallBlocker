package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.PatternRepository
import ru.dmitry.callblocker.data.model.PhonePattern

class IsNumberBlockedByPatternUseCase(
    private val patternRepository: PatternRepository
) {
    /**
     * Checks if a phone number is blocked by any pattern.
     * @param phoneNumber The phone number to check
     * @return true phone number will be blocked
     */
    operator fun invoke(phoneNumber: String): Boolean {
        val patterns = patternRepository.getPhonePatterns()
        return isBlockRequired(phoneNumber, patterns)
    }

    private fun isBlockRequired(phoneNumber: String, patterns: List<PhonePattern>): Boolean {
        val patternResult = patterns.firstOrNull { pattern -> matchesPattern(phoneNumber, pattern.pattern) }
        return patternResult?.isNegativePattern ?: DEFAULT_CALL_BEHAIVIOR
    }

    private fun matchesPattern(phoneNumber: String, pattern: String): Boolean {
        val cleanPhoneNumber = phoneNumber
            .filter { it.isDigit() }
            .replace(" ", "")
        val cleanPattern = pattern
            .replace(" ", "")
            .takeIf { it.isNotEmpty() }
            ?: return false

        val regexPattern = cleanPattern.replace("*", ".*")
        val regex = Regex("^$regexPattern$")

        return regex.matches(cleanPhoneNumber)
    }

    private companion object {
        /** By default block all unknown calls */
        const val DEFAULT_CALL_BEHAIVIOR = true
    }
}