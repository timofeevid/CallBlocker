package ru.dmitry.callblocker.data

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import ru.dmitry.callblocker.core.Const
import ru.dmitry.callblocker.data.api.ContactsRepositoryApi

class ContactsRepository(
    private val context: Context
) : ContactsRepositoryApi {
    
    override fun isNumberInContacts(phoneNumber: String): Boolean {
        return queryContact(phoneNumber) != null
    }

    override fun getContactName(phoneNumber: String): String? {
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
                    Log.d(Const.APP_TAG, "Found contact: $name for number: $phoneNumber")
                    return name
                }
            }
        } catch (e: Exception) {
            Log.e(Const.APP_TAG, "Error checking contacts", e)
        }

        return null
    }
}