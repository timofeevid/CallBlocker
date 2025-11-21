package ru.dmitry.callblocker.data

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.core.Const
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.domain.repository.CallHistoryRepositoryApi
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.CallScreeningDecisionInteractor
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase
import ru.dmitry.callblocker.ui.widget.WidgetUpdate
import javax.inject.Inject

@AndroidEntryPoint
class CallScreenerService : CallScreeningService() {

    @Inject
    lateinit var appConfigurationInteractor: AppConfigurationInteractor

    @Inject
    lateinit var callHistoryRepository: CallHistoryRepositoryApi

    @Inject
    lateinit var callScreeningDecisionInteractor: CallScreeningDecisionInteractor

    @Inject
    lateinit var showBlockedCallNotificationUseCase: ShowBlockedCallNotificationUseCase

    override fun onScreenCall(callDetails: Call.Details) {
        val config = appConfigurationInteractor.getConfiguration()
        setServiceIsActive(config)
        val phoneNumber = callDetails.handle?.schemeSpecificPart

        Log.d(Const.APP_TAG, "Screening call from: $phoneNumber")

        val screeningResult = callScreeningDecisionInteractor.screenCall(phoneNumber)

        if (screeningResult.shouldBlock) {
            Log.d(Const.APP_TAG, "Blocking call - reason: ${screeningResult.reason}")
            blockCall(callDetails, phoneNumber!!, config.isPushEnable)
        } else {
            Log.d(Const.APP_TAG, "Allowing call - reason: ${screeningResult.reason}")
            allowCall(callDetails)
        }

        if (phoneNumber != null) {
            callHistoryRepository.saveScreenedCall(
                phoneNumber = phoneNumber,
                wasBlocked = screeningResult.shouldBlock
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