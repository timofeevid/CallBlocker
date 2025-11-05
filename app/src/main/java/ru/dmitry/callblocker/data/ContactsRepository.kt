package ru.dmitry.callblocker.data

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import ru.dmitry.callblocker.core.CONST

class ContactsRepository(
    private val context: Context
) {
    
    fun isNumberInContacts(phoneNumber: String): Boolean {
        return queryContact(phoneNumber) != null
    }

    fun getContactName(phoneNumber: String): String? {
        return queryContact(phoneNumber)
    }
    
    private fun queryContact(phoneNumber: String): String? {
        val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI
        val lookupUri = uri.buildUpon().appendPath(phoneNumber).build()

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        try {
            context.contentResolver.query(
                lookupUri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(
                        cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    )
                    Log.d(CONST.APP_TAG, "Found contact: $name for number: $phoneNumber")
                    return name
                }
            }
        } catch (e: Exception) {
            Log.e(CONST.APP_TAG, "Error checking contacts", e)
        }

        return null
    }
}