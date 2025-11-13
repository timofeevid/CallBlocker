package ru.dmitry.callblocker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.data.CallHistoryRepository
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.data.NotificationRepository
import ru.dmitry.callblocker.data.PatternRepository
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.GetContactNameUseCase
import ru.dmitry.callblocker.domain.usecase.IsNumberBlockedByPatternUseCase
import ru.dmitry.callblocker.domain.usecase.IsNumberInContactsUseCase
import ru.dmitry.callblocker.domain.usecase.PatternInteractor
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    fun provideAppConfigurationRepository(@ApplicationContext context: Context): AppConfigurationRepository {
        return AppConfigurationRepository(context)
    }

    @Provides
    fun provideAppConfigurationInteractor(appConfigurationRepository: AppConfigurationRepository): AppConfigurationInteractor {
        return AppConfigurationInteractor(appConfigurationRepository)
    }

    @Provides
    fun provideCallHistoryRepository(
        @ApplicationContext context: Context,
        appConfigurationRepository: AppConfigurationRepository
    ): CallHistoryRepository {
        return CallHistoryRepository(
            context = context,
            appConfigurationRepository = appConfigurationRepository
        )
    }

    @Provides
    fun provideContactsRepository(@ApplicationContext context: Context): ContactsRepository {
        return ContactsRepository(context)
    }

    @Provides
    fun provideIsNumberInContactsUseCase(contactsRepository: ContactsRepository): IsNumberInContactsUseCase {
        return IsNumberInContactsUseCase(contactsRepository)
    }

    @Provides
    fun provideGetContactNameUseCase(contactsRepository: ContactsRepository): GetContactNameUseCase {
        return GetContactNameUseCase(contactsRepository)
    }

    @Provides
    fun provideNotificationRepository(@ApplicationContext context: Context): NotificationRepository {
        return NotificationRepository(context)
    }

    @Provides
    fun provideShowBlockedCallNotificationUseCase(notificationRepository: NotificationRepository): ShowBlockedCallNotificationUseCase {
        return ShowBlockedCallNotificationUseCase(notificationRepository)
    }

    @Provides
    fun providePatternRepository(@ApplicationContext context: Context): PatternRepository {
        return PatternRepository(context)
    }

    @Provides
    fun providePatternInteractor(patternRepository: PatternRepository): PatternInteractor {
        return PatternInteractor(patternRepository)
    }

    @Provides
    fun provideIsNumberBlockedByPatternUseCase(patternRepository: PatternRepository): IsNumberBlockedByPatternUseCase {
        return IsNumberBlockedByPatternUseCase(patternRepository)
    }
}