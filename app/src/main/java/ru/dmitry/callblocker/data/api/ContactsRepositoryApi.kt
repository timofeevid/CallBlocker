package ru.dmitry.callblocker.data.api

interface ContactsRepositoryApi {
    fun isNumberInContacts(phoneNumber: String): Boolean
    fun getContactName(phoneNumber: String): String?
}