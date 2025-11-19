package ru.dmitry.callblocker.core.formatters

import java.util.regex.Pattern

object PhoneNumberFormatter {

    private const val RUSSIAN_MOBILE_PATTERN = "^([+]?7|8)\\D*\\(?\\d{3}\\)?\\D*\\d{3}\\D*\\d{2}\\D*\\d{2}$"
    private const val GENERIC_PATTERN = ".*\\d.*"
    
    private val PHONE_PATTERNS = listOf(
        PhonePattern(RUSSIAN_MOBILE_PATTERN) { number ->
            formatRussianMobile(number)
        },
        PhonePattern(GENERIC_PATTERN) { number ->
            formatGeneric(number)
        }
    )

    fun format(phoneNumber: String): String {
        if (phoneNumber.isBlank()) return ""
        
        for (pattern in PHONE_PATTERNS) {
            if (Pattern.matches(pattern.regex, phoneNumber.trim())) {
                return pattern.formatter(phoneNumber.trim())
            }
        }
        return phoneNumber.filter { it.isDigit() }
    }
    
    private fun formatRussianMobile(number: String): String {
        val digits = number.filter { it.isDigit() }
        val isGoodNumber = digits.length == 11 && (digits.startsWith("7") || digits.startsWith("8"))
        return if (isGoodNumber) {
            val beginNumber = when {
                digits.startsWith("7") -> "+7"
                digits.startsWith("8") -> "8"
                else -> ""
            }
            val normalized = digits.substring(1) // Remove first digit (7 or 8)

            // normalized is 10 digits: 4951234567
            buildString {
                append(beginNumber)              // 8 or +7
                append(" (")
                append(normalized.substring(0, 3)) // 495
                append(") ")
                append(normalized.substring(3, 6)) // 123
                append("-")
                append(normalized.substring(6, 8)) // 45
                append("-")
                append(normalized.substring(8, 10)) // 67
            }
        } else {
            formatGeneric(digits)
        }
    }
    
    private fun formatGeneric(number: String): String {
        val digits = number.filter { it.isDigit() }
        if (digits.isEmpty()) return ""
        return buildString { appendDash(digits) }
    }

    private fun StringBuilder.appendDash(digits: String): StringBuilder {
        for (i in digits.indices) {
            if (i > 0 && i % 3 == 0 && i != digits.lastIndex) {
                append("-")
            }
            append(digits[i])
        }
        return this
    }
    
    private data class PhonePattern(
        val regex: String,
        val formatter: (String) -> String
    )
}