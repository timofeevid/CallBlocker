package ru.dmitry.callblocker.mock

import ru.dmitry.callblocker.data.api.NotificationRepositoryApi
import ru.dmitry.callblocker.domain.model.NotificationData

/**
 * Mock implementation of NotificationRepository for testing purposes
 */
class MockNotificationRepository : NotificationRepositoryApi {
    
    private var lastNotification: NotificationData? = null
    private var notificationCount: Int = 0
    
    override fun showBlockedCallNotification(params: NotificationData) {
        lastNotification = params
        notificationCount++
    }
    
    fun getLastNotification(): NotificationData? = lastNotification
    
    fun getNotificationCount(): Int = notificationCount
}