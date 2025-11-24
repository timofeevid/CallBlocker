package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.data.model.PhonePatternType
import ru.dmitry.callblocker.domain.repository.PatternRepositoryApi

class PatternInteractor(
    private val patternRepository: PatternRepositoryApi
) {

    fun addPhonePattern(pattern: PhonePattern) {
        val currentPatterns = patternRepository.getPhonePatterns().toMutableList()
        currentPatterns.add(pattern)
        patternRepository.savePhonePatterns(currentPatterns)
    }

    fun updatePhonePattern(oldPattern: PhonePattern, newPattern: PhonePattern) {
        val currentPatterns = patternRepository.getPhonePatterns().toMutableList()
        val index = currentPatterns.indexOf(oldPattern)
        if (index != -1) {
            currentPatterns[index] = newPattern
            patternRepository.savePhonePatterns(currentPatterns)
        }
    }

    fun deletePhonePattern(pattern: PhonePattern) {
        val currentPatterns = patternRepository.getPhonePatterns().toMutableList()
        currentPatterns.remove(pattern)
        patternRepository.savePhonePatterns(currentPatterns)
    }

    fun getPhonePatternsFlow(): Flow<List<PhonePattern>> {
        return patternRepository
            .observePhonePatterns()
            .map { patterns: List<PhonePattern> ->
                patterns.sortedWith(
                    compareBy<PhonePattern> { it.isNegativePattern }.thenBy { pattern ->
                        when (pattern.type) {
                            PhonePatternType.RUSSIAN_MOBILE_PLUS -> 0
                            PhonePatternType.RUSSIAN_MOBILE -> 1
                            PhonePatternType.RUSSIAN_TOLL_FREE -> 2
                            PhonePatternType.GENERIC -> 3
                        }
                    }
                )
            }
    }
}