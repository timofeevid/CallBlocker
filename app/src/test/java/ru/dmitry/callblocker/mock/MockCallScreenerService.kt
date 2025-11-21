package ru.dmitry.callblocker.mock

import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.domain.repository.CallHistoryRepositoryApi
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.CallScreeningDecisionInteractor
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase

/**
 * Mock implementation of CallScreenerService for testing purposes
 */
class MockCallScreenerService(
    private val appConfigurationInteractor: AppConfigurationInteractor,
    private val callScreeningDecisionInteractor: CallScreeningDecisionInteractor,
    private val showBlockedCallNotificationUseCase: ShowBlockedCallNotificationUseCase,
    private val callHistoryRepository: CallHistoryRepositoryApi,
) {

    var lastCallAction: CallAction? = null
    var lastPhoneNumber: String? = null

    fun onScreenCall(phoneNumber: String?) {
        lastPhoneNumber = phoneNumber
        val screeningResult = callScreeningDecisionInteractor.screenCall(phoneNumber)

        val config = appConfigurationInteractor.getConfiguration()
        if (screeningResult.shouldBlock) {
            lastCallAction = CallAction.BLOCK
            if (config.isPushEnable && phoneNumber != null) {
                showBlockedCallNotificationUseCase(NotificationData(phoneNumber = phoneNumber))
            }
        } else {
            lastCallAction = CallAction.ALLOW
        }

        if (phoneNumber != null) {
            callHistoryRepository.saveScreenedCall(
                phoneNumber = phoneNumber,
                wasBlocked = screeningResult.shouldBlock
            )
        }
    }

    enum class CallAction {
        ALLOW,
        BLOCK
    }
}