package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.api.ContactsRepositoryApi

class IsNumberInContactsUseCase(
    private val contactsRepository: ContactsRepositoryApi
) {
    operator fun invoke(phoneNumber: String): Boolean {
        return contactsRepository.isNumberInContacts(phoneNumber)
    }
}