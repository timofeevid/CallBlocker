package ru.dmitry.callblocker
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

class CallScreenerService : CallScreeningService() {

    companion object {
        private const val TAG = "CallScreenerService"
    }

    override fun onScreenCall(callDetails: Call.Details) {
        // Get the incoming phone number
        val phoneNumber = callDetails.handle?.schemeSpecificPart

        Log.d(TAG, "Screening call from: $phoneNumber")

        if (phoneNumber == null) {
            // If we can't get the number, allow the call
            allowCall(callDetails)
            return
        }

        // Check if number is in contacts
        val isKnownNumber = ContactsHelper.isNumberInContacts(this, phoneNumber)

        if (isKnownNumber) {
            // Allow calls from known contacts
            Log.d(TAG, "Known contact - allowing call")
            allowCall(callDetails)
        } else {
            // Check if user wants to block unknown numbers
            val shouldBlock = PreferencesHelper.shouldBlockUnknownNumbers(this)

            if (shouldBlock) {
                // Block unknown calls
                Log.d(TAG, "Unknown number - blocking call")
                blockCall(callDetails, phoneNumber)
            } else {
                // Just log but allow the call
                Log.d(TAG, "Unknown number - allowing call (blocking disabled)")
                allowCall(callDetails)
            }

            // Save to blocked/screened calls log
            CallLogHelper.saveScreenedCall(this, phoneNumber, shouldBlock)
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
            NotificationHelper.showBlockedCallNotification(this, phoneNumber)
        }
    }
}