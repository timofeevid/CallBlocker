package ru.dmitry.callblocker.data

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.core.CONST
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.IsNumberInContactsUseCase
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase
import ru.dmitry.callblocker.ui.widget.CallScreenerWidgetProvider
import javax.inject.Inject

@AndroidEntryPoint
class CallScreenerService : CallScreeningService() {

    @Inject
    lateinit var appConfigurationInteractor: AppConfigurationInteractor

    @Inject
    lateinit var callHistoryRepository: CallHistoryRepository

    @Inject
    lateinit var isNumberInContactsUseCase: IsNumberInContactsUseCase

    @Inject
    lateinit var showBlockedCallNotificationUseCase: ShowBlockedCallNotificationUseCase

    override fun onScreenCall(callDetails: Call.Details) {
        val config = appConfigurationInteractor.getConfiguration()
        setServiceIsActive(config)
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
            val shouldBlock = config.isBlockUnknownNumberEnable

            if (shouldBlock) {
                Log.d(CONST.APP_TAG, "Unknown number - blocking call")
                blockCall(callDetails, phoneNumber, config.isPushEnable)
            } else {
                Log.d(CONST.APP_TAG, "Unknown number - allowing call (blocking disabled)")
                allowCall(callDetails)
            }

            callHistoryRepository.saveScreenedCall(
                phoneNumber = phoneNumber,
                wasBlocked = shouldBlock
            )

            CallScreenerWidgetProvider.updateAllWidgets(this)
        }
    }

    private fun setServiceIsActive(config: ConfigurationModel) {
        if (config.isScreenRoleGrand.not()) {
            val newConfig = config.copy(isScreenRoleGrand = true)
            appConfigurationInteractor.updateConfig(newConfig)
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
        phoneNumber: String,
        isPushEnable: Boolean,
    ) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, response)
        if (isPushEnable) {
            showBlockedCallNotificationUseCase.invoke(NotificationData(phoneNumber = phoneNumber))
        }
    }
}