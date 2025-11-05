package ru.dmitry.callblocker.data

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import ru.dmitry.callblocker.ui.widget.CallScreenerWidgetProvider
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.core.CONST

class CallScreenerService : CallScreeningService() {
    // TODO передавать инжектом PreferencesHelper CallHistoryRepository CallScreenerWidgetProvider NotificationHelper

    override fun onScreenCall(callDetails: Call.Details) {
        // Mark service as active - this proves the service is working
        AppConfigurationRepository.markServiceActive(this)

        // Get the incoming phone number
        val phoneNumber = callDetails.handle?.schemeSpecificPart

        Log.d(CONST.APP_TAG, "Screening call from: $phoneNumber")

        if (phoneNumber == null) {
            // If we can't get the number, allow the call
            allowCall(callDetails)
            return
        }

        // Check if number is in contacts
        val isKnownNumber = ContactsRepository.isNumberInContacts(this, phoneNumber)

        if (isKnownNumber) {
            // Allow calls from known contacts
            Log.d(CONST.APP_TAG, "Known contact - allowing call")
            allowCall(callDetails)
        } else {
            // Check if user wants to block unknown numbers
            val shouldBlock = AppConfigurationRepository.shouldBlockUnknownNumbers(this)

            if (shouldBlock) {
                // Block unknown calls
                Log.d(CONST.APP_TAG, "Unknown number - blocking call")
                blockCall(callDetails, phoneNumber)
            } else {
                // Just log but allow the call
                Log.d(CONST.APP_TAG, "Unknown number - allowing call (blocking disabled)")
                allowCall(callDetails)
            }

            // Save to blocked/screened calls log
            CallHistoryRepository.saveScreenedCall(this, phoneNumber, shouldBlock)

            // Update widget
            CallScreenerWidgetProvider.Companion.updateAllWidgets(this)
        }
    }

    private fun allowCall(callDetails: Call.Details) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val response = CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
            respondToCall(callDetails, response)
        }
    }

    private fun blockCall(callDetails: Call.Details, phoneNumber: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val response = CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
            respondToCall(callDetails, response)

            // Show notification about blocked call
            NotificationRepository.showBlockedCallNotification(this, phoneNumber)
        }
    }
}