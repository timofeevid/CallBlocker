package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.dmitry.callblocker.data.PatternRepository
import ru.dmitry.callblocker.data.model.PhonePattern

class PatternInteractor(
    private val patternRepository: PatternRepository
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
        return patternRepository.observePhonePatterns()
    }
}