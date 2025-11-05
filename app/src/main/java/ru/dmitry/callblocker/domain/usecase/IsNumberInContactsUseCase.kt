package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.ContactsRepository

class IsNumberInContactsUseCase(
    private val contactsRepository: ContactsRepository
) {
    operator fun invoke(phoneNumber: String): Boolean {
        return contactsRepository.isNumberInContacts(phoneNumber)
    }
}