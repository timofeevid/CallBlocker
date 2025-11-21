package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.domain.repository.NotificationRepositoryApi

class ShowBlockedCallNotificationUseCase(
    private val notificationRepository: NotificationRepositoryApi
) {
    operator fun invoke(params: NotificationData) {
        notificationRepository.showBlockedCallNotification(params)
    }
}