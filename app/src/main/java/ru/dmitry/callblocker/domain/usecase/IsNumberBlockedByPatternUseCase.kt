package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.PatternRepository
import ru.dmitry.callblocker.data.model.PhonePattern

class IsNumberBlockedByPatternUseCase(
    private val patternRepository: PatternRepository
) {
    /**
     * Checks if a phone number is blocked by any pattern.
     * @param phoneNumber The phone number to check
     * @return true if the number matches a blocking pattern, false otherwise
     */
    operator fun invoke(phoneNumber: String): Boolean {
        val patterns = patternRepository.getPhonePatterns()
        return isBlockedByPattern(phoneNumber, patterns)
    }

    /**
     * Checks if a phone number is blocked by any pattern from the provided list.
     * @param phoneNumber The phone number to check
     * @param patterns The list of patterns to check against
     * @return true if the number matches a blocking pattern, false otherwise
     */
    private fun isBlockedByPattern(phoneNumber: String, patterns: List<PhonePattern>): Boolean {
        for (pattern in patterns) {
            // Skip negative patterns (allow patterns)
            if (pattern.isNegativePattern) continue
            
            // Check if the phone number matches the pattern
            if (matchesPattern(phoneNumber, pattern.pattern)) {
                return true // Found a matching blocking pattern
            }
        }
        return false // No matching blocking pattern found
    }

    /**
     * Checks if a phone number matches a pattern.
     * Pattern can contain digits and asterisks (*) as wildcards.
     * @param phoneNumber The phone number to check
     * @param pattern The pattern to match against
     * @return true if the phone number matches the pattern, false otherwise
     */
    private fun matchesPattern(phoneNumber: String, pattern: String): Boolean {
        // Remove spaces from both phone number and pattern for comparison
        val cleanPhoneNumber = phoneNumber.replace(" ", "")
        val cleanPattern = pattern.replace(" ", "")
        
        // If pattern is empty, it doesn't match anything
        if (cleanPattern.isEmpty()) return false
        
        // Convert pattern to regex
        val regexPattern = cleanPattern.replace("*", ".*")
        val regex = Regex("^$regexPattern$")
        
        return regex.matches(cleanPhoneNumber)
    }
}