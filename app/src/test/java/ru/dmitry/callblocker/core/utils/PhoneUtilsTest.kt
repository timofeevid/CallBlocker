package ru.dmitry.callblocker.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.dmitry.callblocker.core.formatters.PhoneNumberFormatter
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.data.model.PhonePatternType

class PhoneNumberFormatterTest {

    @Test
    fun `format Russian mobile number`() {
        val phone = "79991234567"
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.RUSSIAN_MOBILE)
        assertEquals("+7 (999) 123-45-67", result)
    }

    @Test
    fun `format Russian toll free number`() {
        val phone = "88001234567"
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.RUSSIAN_TOLL_FREE)
        assertEquals("8 (800) 123-45-67", result)
    }

    @Test
    fun `format generic number`() {
        val phone = "123456789012"
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.GENERIC)
        assertEquals("123-456-789-012", result)
    }

    @Test
    fun `format with extra characters`() {
        val phone = "+7 (999) 123-45-67"
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.RUSSIAN_MOBILE)
        assertEquals("+7 (999) 123-45-67", result)
    }

    @Test
    fun `format shorter number`() {
        val phone = "12345"
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.RUSSIAN_MOBILE)
        assertEquals("+1 (234) 5", result)
    }

    @Test
    fun `format longer number`() {
        val phone = "123456789012345"
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.RUSSIAN_MOBILE)
        assertEquals("+1 (234) 567-89-0112345", result)
    }

    @Test
    fun `format empty string`() {
        val phone = ""
        val result = PhoneNumberFormatter.format(phone, PhonePatternType.RUSSIAN_MOBILE)
        assertEquals("", result)
    }
    
    @Test
    fun `format phone pattern`() {
        val phonePattern = PhonePattern("+79991234567", false, PhonePatternType.RUSSIAN_MOBILE)
        val result = PhoneNumberFormatter.format(phonePattern.pattern, phonePattern.type)
        assertEquals("+7 (999) 123-45-67", result)
    }
    
    @Test
    fun `format phone pattern with different type`() {
        val phonePattern = PhonePattern("88001234567", true, PhonePatternType.RUSSIAN_TOLL_FREE)
        val result = PhoneNumberFormatter.format(phonePattern.pattern, phonePattern.type)
        assertEquals("8 (800) 123-45-67", result)
    }

    @Test
    fun `format moscow phone with 8`() {
        val phonePattern = PhonePattern("84957800771", true, PhonePatternType.RUSSIAN_MOBILE)
        val result = PhoneNumberFormatter.format(phonePattern.pattern, phonePattern.type)
        assertEquals("8 (495) 780-07-71", result)
    }
}