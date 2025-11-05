package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.ContactsRepository

class GetContactNameUseCase(
    private val contactsRepository: ContactsRepository
) {
    operator fun invoke(phoneNumber: String): String? {
        return contactsRepository.getContactName(phoneNumber)
    }
}