package ru.dmitry.callblocker.data

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.core.CONST
import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.domain.usecase.IsNumberInContactsUseCase
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase
import ru.dmitry.callblocker.ui.widget.CallScreenerWidgetProvider
import javax.inject.Inject

@AndroidEntryPoint
class CallScreenerService : CallScreeningService() {

    @Inject
    lateinit var appConfigurationRepository: AppConfigurationRepository

    @Inject
    lateinit var callHistoryRepository: CallHistoryRepository

    @Inject
    lateinit var isNumberInContactsUseCase: IsNumberInContactsUseCase

    @Inject
    lateinit var showBlockedCallNotificationUseCase: ShowBlockedCallNotificationUseCase

    override fun onScreenCall(callDetails: Call.Details) {
        appConfigurationRepository.markServiceActive()

        val phoneNumber = callDetails.handle?.schemeSpecificPart

        Log.d(CONST.APP_TAG, "Screening call from: $phoneNumber")

        if (phoneNumber == null) {
            allowCall(callDetails)
            return
        }

        val isKnownNumber = isNumberInContactsUseCase(phoneNumber)

        if (isKnownNumber) {
            Log.d(CONST.APP_TAG, "Known contact - allowing call")
            allowCall(callDetails)
        } else {
            val shouldBlock = appConfigurationRepository.shouldBlockUnknownNumbers()

            if (shouldBlock) {
                Log.d(CONST.APP_TAG, "Unknown number - blocking call")
                blockCall(callDetails, phoneNumber)
            } else {
                Log.d(CONST.APP_TAG, "Unknown number - allowing call (blocking disabled)")
                allowCall(callDetails)
            }

            callHistoryRepository.saveScreenedCall(phoneNumber, shouldBlock)

            CallScreenerWidgetProvider.updateAllWidgets(this)
        }
    }

    private fun allowCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, response)
    }

    private fun blockCall(
        callDetails: Call.Details,
        phoneNumber: String
    ) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, response)

        showBlockedCallNotificationUseCase.invoke(NotificationData(phoneNumber = phoneNumber))
    }
}