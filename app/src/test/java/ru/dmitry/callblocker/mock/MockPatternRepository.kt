package ru.dmitry.callblocker.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.dmitry.callblocker.data.api.PatternRepositoryApi
import ru.dmitry.callblocker.data.model.PhonePattern

/**
 * Mock implementation of PatternRepository for testing purposes
 */
class MockPatternRepository : PatternRepositoryApi {
    
    private val patternsFlow = MutableStateFlow<List<PhonePattern>>(emptyList())
    private var patterns: MutableList<PhonePattern> = mutableListOf()
    
    override fun getPhonePatterns(): List<PhonePattern> {
        return patterns
    }
    
    override fun savePhonePatterns(patterns: List<PhonePattern>) {
        this.patterns.clear()
        this.patterns.addAll(patterns)
        patternsFlow.value = this.patterns.toList()
    }
    
    override fun observePhonePatterns(): Flow<List<PhonePattern>> {
        return patternsFlow
    }
}