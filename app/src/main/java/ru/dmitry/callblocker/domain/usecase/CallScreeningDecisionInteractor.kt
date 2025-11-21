package ru.dmitry.callblocker.domain.usecase

class CallScreeningDecisionInteractor(
    private val appConfigurationInteractor: AppConfigurationInteractor,
    private val isNumberInContactsUseCase: IsNumberInContactsUseCase,
    private val isNumberBlockedByPatternUseCase: IsNumberBlockedByPatternUseCase
) {

    data class ScreeningResult(
        val shouldBlock: Boolean,
        val reason: Reason
    )

    enum class Reason {
        NULL_PHONE_NUMBER,
        KNOWN_CONTACT,
        BLOCKED_BY_PATTERN,
        ALLOWED_BY_DEFAULT
    }

    fun screenCall(phoneNumber: String?): ScreeningResult {
        if (phoneNumber == null) {
            return ScreeningResult(
                shouldBlock = false,
                reason = Reason.NULL_PHONE_NUMBER
            )
        }

        val config = appConfigurationInteractor.getConfiguration()

        val isKnownNumber = isNumberInContactsUseCase(phoneNumber)
        if (isKnownNumber) {
            return ScreeningResult(
                shouldBlock = false,
                reason = Reason.KNOWN_CONTACT
            )
        }

        val isBlockedByPattern = isNumberBlockedByPatternUseCase(phoneNumber)
        val shouldBlock = config.isBlockByPatternEnable && isBlockedByPattern

        val reason = when {
            shouldBlock -> Reason.BLOCKED_BY_PATTERN
            else -> Reason.ALLOWED_BY_DEFAULT
        }

        return ScreeningResult(
            shouldBlock = shouldBlock,
            reason = reason
        )
    }
}