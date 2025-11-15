package ru.dmitry.callblocker.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual transformation for phone number formatting.
 * Formats input according to pattern +*(***) ***-**-**
 * Fixed offset mapping issues and plus sign handling
 */
class PhoneNumberVisualTransformation : VisualTransformation {
    
    companion object {
        const val MAX_PHONE_DIGITS = 15
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= MAX_PHONE_DIGITS) {
            text.text.substring(0, MAX_PHONE_DIGITS)
        } else {
            text.text
        }
        
        val formatted = formatPhoneNumber(trimmed)
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                
                val originalText = if (offset <= trimmed.length) {
                    trimmed.substring(0, offset)
                } else {
                    trimmed
                }
                
                val formattedText = formatPhoneNumber(originalText)
                return formattedText.length.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                
                val safeOffset = offset.coerceAtMost(formatted.length)
                
                var digitCount = 0
                for (i in 0 until safeOffset) {
                    if (formatted[i].isDigit()) {
                        digitCount++
                    }
                }
                
                return digitCount.coerceAtMost(trimmed.length).coerceAtLeast(0)
            }
        }
        
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
    
    private fun formatPhoneNumber(text: String): String {
        val digits = text.filter { it.isDigit() }
        
        val startedWithPlus = text.isEmpty() || text.startsWith("+")
        val shouldAddPlus = startedWithPlus
        
        return when {
            digits.isEmpty() -> ""
            digits.length == 1 -> if (shouldAddPlus) "+${digits}" else digits
            digits.length <= 4 -> if (shouldAddPlus) "+${digits[0]}(${digits.substring(1)}" else digits
            digits.length <= 7 -> if (shouldAddPlus) "+${digits[0]}(${digits.substring(1, 4)}) ${digits.substring(4)}" else digits
            digits.length <= 10 -> if (shouldAddPlus) "+${digits[0]}(${digits.substring(1, 4)}) ${digits.substring(4, 7)}-${digits.substring(7)}" else digits
            digits.length <= 11 -> if (shouldAddPlus) "+${digits[0]}(${digits.substring(1, 4)}) ${digits.substring(4, 7)}-${digits.substring(7, 9)}-${digits.substring(9)}" else digits
            digits.length > 11 -> {
                formatLongNumber(digits)
            }
            else -> digits
        }
    }
    
    private fun formatLongNumber(digits: String): String {
        if (digits.isEmpty()) return ""
        
        val formatted = StringBuilder()
        for (i in digits.indices) {
            if (i > 0 && i % 3 == 0) {
                formatted.append("-")
            }
            formatted.append(digits[i])
        }
        return formatted.toString()
    }
    
    override fun equals(other: Any?): Boolean {
        if (other !is PhoneNumberVisualTransformation) return false
        return true
    }
    
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}