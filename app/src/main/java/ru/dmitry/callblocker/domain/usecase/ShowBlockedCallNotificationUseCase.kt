package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.api.NotificationRepositoryApi
import ru.dmitry.callblocker.domain.model.NotificationData

class ShowBlockedCallNotificationUseCase(
    private val notificationRepository: NotificationRepositoryApi
) {
    operator fun invoke(params: NotificationData) {
        notificationRepository.showBlockedCallNotification(params)
    }
}