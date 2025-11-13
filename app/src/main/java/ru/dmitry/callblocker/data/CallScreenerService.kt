package ru.dmitry.callblocker.data

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.core.Const
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.IsNumberBlockedByPatternUseCase
import ru.dmitry.callblocker.domain.usecase.IsNumberInContactsUseCase
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase
import ru.dmitry.callblocker.ui.widget.WidgetUpdate
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
    lateinit var isNumberBlockedByPatternUseCase: IsNumberBlockedByPatternUseCase

    @Inject
    lateinit var showBlockedCallNotificationUseCase: ShowBlockedCallNotificationUseCase

    override fun onScreenCall(callDetails: Call.Details) {
        val config = appConfigurationInteractor.getConfiguration()
        setServiceIsActive(config)
        val phoneNumber = callDetails.handle?.schemeSpecificPart

        Log.d(Const.APP_TAG, "Screening call from: $phoneNumber")

        if (phoneNumber == null) {
            allowCall(callDetails)
            return
        }

        val isKnownNumber = isNumberInContactsUseCase(phoneNumber)
        val isBlockedByPattern = isNumberBlockedByPatternUseCase(phoneNumber)

        if (isKnownNumber) {
            Log.d(Const.APP_TAG, "Known contact - allowing call")
            allowCall(callDetails)
        } else {
            val shouldBlock = config.isBlockUnknownNumberEnable || (config.isBlockByPatternEnable && isBlockedByPattern)

            if (shouldBlock) {
                Log.d(Const.APP_TAG, "Blocking call - ${if (isBlockedByPattern && config.isBlockByPatternEnable) "matches blocking pattern" else "unknown number blocking enabled"}")
                blockCall(callDetails, phoneNumber, config.isPushEnable)
            } else {
                Log.d(Const.APP_TAG, "Allowing call - unknown number blocking disabled")
                allowCall(callDetails)
            }

            callHistoryRepository.saveScreenedCall(
                phoneNumber = phoneNumber,
                wasBlocked = shouldBlock
            )

            WidgetUpdate.updateWidgets(this)
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