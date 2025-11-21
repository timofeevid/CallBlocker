package ru.dmitry.callblocker.domain.repository

import ru.dmitry.callblocker.domain.model.NotificationData

interface NotificationRepositoryApi {
    fun showBlockedCallNotification(params: NotificationData)
}