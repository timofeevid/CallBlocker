package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.NotificationRepository
import ru.dmitry.callblocker.domain.model.NotificationData

class ShowBlockedCallNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(params: NotificationData) {
        notificationRepository.showBlockedCallNotification(params)
    }
}