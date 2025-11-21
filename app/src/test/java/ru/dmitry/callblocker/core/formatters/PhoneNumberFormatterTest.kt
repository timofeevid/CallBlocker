package ru.dmitry.callblocker.core.formatters

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.dmitry.callblocker.data.model.PhonePatternType

class PhoneNumberFormatterTest {

    @Test
    fun `getPhonePattern should return RUSSIAN_MOBILE for Russian mobile numbers`() {
        // Russian mobile numbers starting with 79
        assertEquals(
            PhonePatternType.RUSSIAN_MOBILE,
            PhoneNumberFormatter.getPhonePattern("79123456789")
        )
        
        // Russian mobile numbers starting with 89
        assertEquals(
            PhonePatternType.RUSSIAN_MOBILE,
            PhoneNumberFormatter.getPhonePattern("89123456789")
        )
        
        // Russian mobile numbers with formatting
        assertEquals(
            PhonePatternType.RUSSIAN_MOBILE,
            PhoneNumberFormatter.getPhonePattern("+7 (912) 345-67-89")
        )
    }

    @Test
    fun `getPhonePattern should return RUSSIAN_TOLL_FREE for Russian toll free numbers`() {
        // Russian toll free numbers starting with 8800
        assertEquals(
            PhonePatternType.RUSSIAN_TOLL_FREE,
            PhoneNumberFormatter.getPhonePattern("88001234567")
        )
        
        // Russian toll free numbers starting with 7800
        assertEquals(
            PhonePatternType.RUSSIAN_TOLL_FREE,
            PhoneNumberFormatter.getPhonePattern("78001234567")
        )
        
        // Russian toll free numbers with formatting
        assertEquals(
            PhonePatternType.RUSSIAN_TOLL_FREE,
            PhoneNumberFormatter.getPhonePattern("8 (800) 123-45-67")
        )
    }

    @Test
    fun `getPhonePattern should return GENERIC for other numbers`() {
        // Generic international number
        assertEquals(
            PhonePatternType.GENERIC,
            PhoneNumberFormatter.getPhonePattern("+1234567890")
        )
        
        // Number with wrong length for Russian patterns
        assertEquals(
            PhonePatternType.GENERIC,
            PhoneNumberFormatter.getPhonePattern("123456789")
        )
        
        // Non-Russian number with 11 digits
        assertEquals(
            PhonePatternType.GENERIC,
            PhoneNumberFormatter.getPhonePattern("12345678901")
        )
    }

    @Test
    fun `getPhonePattern should handle edge cases`() {
        // Empty string
        assertEquals(
            PhonePatternType.GENERIC,
            PhoneNumberFormatter.getPhonePattern("")
        )
        
        // Only non-digit characters
        assertEquals(
            PhonePatternType.GENERIC,
            PhoneNumberFormatter.getPhonePattern("()- ")
        )
        
        // Very short number
        assertEquals(
            PhonePatternType.GENERIC,
            PhoneNumberFormatter.getPhonePattern("12")
        )
    }
}