package ru.dmitry.callblocker.data.api

import ru.dmitry.callblocker.domain.model.NotificationData

interface NotificationRepositoryApi {
    fun showBlockedCallNotification(params: NotificationData)
}