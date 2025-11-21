package ru.dmitry.callblocker.core.formatters

import ru.dmitry.callblocker.data.model.PhonePatternType

object PhoneNumberFormatter {

    /**
     * Formats a phone number according to the specified pattern type
     * @param phone The phone number to format
     * @param phonePatternType The pattern type to use for formatting
     * @return Formatted phone number
     */
    fun format(
        phone: String,
        phonePatternType: PhonePatternType
    ): String {
        val digits = phone.filter { it.isDigit() || it == '*' }
        val pattern = phonePatternType.pattern

        if (digits.isEmpty() || phone.length == 1) return phone

        var digitIndex = 0
        val result = StringBuilder()

        for (char in pattern) {
            when (char) {
                '#' -> {
                    if (digitIndex < digits.length) {
                        result.append(digits[digitIndex])
                        digitIndex++
                    }
                }

                '+' -> {
                    result.append(char)
                }

                else -> {
                    result.append(char)
                }
            }
        }

        while (digitIndex < digits.length) {
            result.append(digits[digitIndex])
            digitIndex++
        }

        return result.toString()
    }

    fun format(phone: String): String {
        val patternType = getPhonePattern(phone)
        return format(phone, patternType)
    }

    /**
     * Determines the most suitable phone pattern type for a given phone number
     * @param phoneNumber The phone number to analyze
     * @return The most suitable PhonePatternType based on the phone number characteristics
     */
    fun getPhonePattern(phoneNumber: String): PhonePatternType {
        val digits = phoneNumber.filter { it.isDigit() }
        
        // Russian mobile pattern: 11 digits starting with 7 or 8 followed by 9xx
        if (digits.length == 11) {
            val firstDigit = digits.firstOrNull()
            if (firstDigit == '7' || firstDigit == '8') {
                // Check if it's likely a mobile number (9xx)
                if (digits[1] == '9') {
                    return PhonePatternType.RUSSIAN_MOBILE
                }
                // Check if it's a toll-free number (8 800)
                if (digits.startsWith("8800") || digits.startsWith("7800")) {
                    return PhonePatternType.RUSSIAN_TOLL_FREE
                }
            }
        }
        return PhonePatternType.GENERIC
    }
}