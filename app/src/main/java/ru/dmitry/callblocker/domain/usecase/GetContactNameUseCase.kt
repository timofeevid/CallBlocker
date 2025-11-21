package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.domain.repository.ContactsRepositoryApi

class GetContactNameUseCase(
    private val contactsRepository: ContactsRepositoryApi
) {
    operator fun invoke(phoneNumber: String): String? {
        return contactsRepository.getContactName(phoneNumber)
    }
}