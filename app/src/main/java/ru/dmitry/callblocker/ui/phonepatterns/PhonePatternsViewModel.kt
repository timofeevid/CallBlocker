package ru.dmitry.callblocker.ui.phonepatterns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.domain.usecase.PatternInteractor
import javax.inject.Inject

@HiltViewModel
class PhonePatternsViewModel @Inject constructor(
    private val patternInteractor: PatternInteractor
) : ViewModel() {
    
    val uiState: StateFlow<PhonePatternsUiState> = patternInteractor.getPhonePatternsFlow()
        .map { patterns -> PhonePatternsUiState(patterns) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PhonePatternsUiState())

    fun addPattern(pattern: String, isNegativePattern: Boolean) {
        patternInteractor.addPhonePattern(PhonePattern(pattern, isNegativePattern))
    }

    fun updatePattern(oldPattern: PhonePattern, pattern: String, isNegativePattern: Boolean) {
        patternInteractor.updatePhonePattern(oldPattern, PhonePattern(pattern, isNegativePattern))
    }

    fun deletePattern(pattern: PhonePattern) {
        patternInteractor.deletePhonePattern(pattern)
    }
}

data class PhonePatternsUiState(
    val patterns: List<PhonePattern> = emptyList()
)