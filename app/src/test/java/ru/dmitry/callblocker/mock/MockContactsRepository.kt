package ru.dmitry.callblocker.mock

import ru.dmitry.callblocker.domain.repository.ContactsRepositoryApi

/**
 * Mock implementation of ContactsRepository for testing purposes
 */
class MockContactsRepository : ContactsRepositoryApi {
    
    private val contacts: MutableSet<String> = mutableSetOf()
    
    override fun isNumberInContacts(phoneNumber: String): Boolean {
        return contacts.contains(phoneNumber)
    }

    override fun getContactName(phoneNumber: String): String? {
        return "Test contact name"
    }

    fun addContact(phoneNumber: String) {
        contacts.add(phoneNumber)
    }
    
    fun removeContact(phoneNumber: String) {
        contacts.remove(phoneNumber)
    }
}