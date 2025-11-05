package ru.dmitry.callblocker.data

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import ru.dmitry.callblocker.core.CONST

object ContactsRepository {
    // TODO добавить инжект и получение контактов.
    fun isNumberInContacts(context: Context, phoneNumber: String): Boolean {
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
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(CONST.APP_TAG, "Error checking contacts", e)
        }

        return false
    }

    fun getContactName(context: Context, phoneNumber: String): String? {
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
                    return cursor.getString(
                        cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(CONST.APP_TAG, "Error getting contact name", e)
        }

        return null
    }
}