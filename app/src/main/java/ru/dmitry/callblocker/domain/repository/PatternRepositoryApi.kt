package ru.dmitry.callblocker.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.dmitry.callblocker.data.model.PhonePattern

interface PatternRepositoryApi {
    fun getPhonePatterns(): List<PhonePattern>
    fun savePhonePatterns(patterns: List<PhonePattern>)
    fun observePhonePatterns(): Flow<List<PhonePattern>>
}