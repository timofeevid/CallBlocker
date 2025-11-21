package ru.dmitry.callblocker.domain.repository

interface ContactsRepositoryApi {
    fun isNumberInContacts(phoneNumber: String): Boolean
    fun getContactName(phoneNumber: String): String?
}