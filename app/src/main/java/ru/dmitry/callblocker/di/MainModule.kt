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
import ru.dmitry.callblocker.data.api.AppConfigurationRepositoryApi
import ru.dmitry.callblocker.data.api.CallHistoryRepositoryApi
import ru.dmitry.callblocker.data.api.ContactsRepositoryApi
import ru.dmitry.callblocker.data.api.NotificationRepositoryApi
import ru.dmitry.callblocker.data.api.PatternRepositoryApi
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.CallScreeningDecisionInteractor
import ru.dmitry.callblocker.domain.usecase.GetContactNameUseCase
import ru.dmitry.callblocker.domain.usecase.IsNumberBlockedByPatternUseCase
import ru.dmitry.callblocker.domain.usecase.IsNumberInContactsUseCase
import ru.dmitry.callblocker.domain.usecase.PatternInteractor
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    fun provideAppConfigurationRepository(@ApplicationContext context: Context): AppConfigurationRepositoryApi {
        return AppConfigurationRepository(context)
    }

    @Provides
    fun provideAppConfigurationInteractor(appConfigurationRepository: AppConfigurationRepositoryApi): AppConfigurationInteractor {
        return AppConfigurationInteractor(appConfigurationRepository)
    }

    @Provides
    fun provideCallHistoryRepository(
        @ApplicationContext context: Context,
        appConfigurationRepository: AppConfigurationRepositoryApi
    ): CallHistoryRepositoryApi {
        return CallHistoryRepository(
            context = context,
            appConfigurationRepository = appConfigurationRepository
        )
    }

    @Provides
    fun provideContactsRepository(@ApplicationContext context: Context): ContactsRepositoryApi {
        return ContactsRepository(context)
    }

    @Provides
    fun provideIsNumberInContactsUseCase(contactsRepository: ContactsRepositoryApi): IsNumberInContactsUseCase {
        return IsNumberInContactsUseCase(contactsRepository)
    }

    @Provides
    fun provideGetContactNameUseCase(contactsRepository: ContactsRepositoryApi): GetContactNameUseCase {
        return GetContactNameUseCase(contactsRepository)
    }

    @Provides
    fun provideNotificationRepository(@ApplicationContext context: Context): NotificationRepositoryApi {
        return NotificationRepository(context)
    }

    @Provides
    fun provideShowBlockedCallNotificationUseCase(notificationRepository: NotificationRepositoryApi): ShowBlockedCallNotificationUseCase {
        return ShowBlockedCallNotificationUseCase(notificationRepository)
    }

    @Provides
    fun providePatternRepository(@ApplicationContext context: Context): PatternRepositoryApi {
        return PatternRepository(context)
    }

    @Provides
    fun providePatternInteractor(patternRepository: PatternRepositoryApi): PatternInteractor {
        return PatternInteractor(patternRepository)
    }

    @Provides
    fun provideIsNumberBlockedByPatternUseCase(patternRepository: PatternRepositoryApi): IsNumberBlockedByPatternUseCase {
        return IsNumberBlockedByPatternUseCase(patternRepository)
    }

    @Provides
    fun provideCallScreeningDecisionInteractor(
        appConfigurationInteractor: AppConfigurationInteractor,
        isNumberInContactsUseCase: IsNumberInContactsUseCase,
        isNumberBlockedByPatternUseCase: IsNumberBlockedByPatternUseCase
    ): CallScreeningDecisionInteractor {
        return CallScreeningDecisionInteractor(
            appConfigurationInteractor,
            isNumberInContactsUseCase,
            isNumberBlockedByPatternUseCase
        )
    }
}