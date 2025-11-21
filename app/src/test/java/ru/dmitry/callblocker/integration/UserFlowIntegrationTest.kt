package ru.dmitry.callblocker.integration

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.data.model.PhonePatternType
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.domain.usecase.CallScreeningDecisionInteractor
import ru.dmitry.callblocker.domain.usecase.CallScreeningDecisionInteractor.ScreeningResult
import ru.dmitry.callblocker.domain.usecase.IsNumberBlockedByPatternUseCase
import ru.dmitry.callblocker.domain.usecase.IsNumberInContactsUseCase
import ru.dmitry.callblocker.domain.usecase.PatternInteractor
import ru.dmitry.callblocker.domain.usecase.ShowBlockedCallNotificationUseCase
import ru.dmitry.callblocker.mock.MockAppConfigurationRepository
import ru.dmitry.callblocker.mock.MockCallHistoryRepository
import ru.dmitry.callblocker.mock.MockCallScreenerService
import ru.dmitry.callblocker.mock.MockContactsRepository
import ru.dmitry.callblocker.mock.MockNotificationRepository
import ru.dmitry.callblocker.mock.MockPatternRepository

class UserFlowIntegrationTest {

    private lateinit var mockPatternRepository: MockPatternRepository
    private lateinit var mockAppConfigurationRepository: MockAppConfigurationRepository
    private lateinit var mockContactsRepository: MockContactsRepository
    private lateinit var mockCallHistoryRepository: MockCallHistoryRepository
    private lateinit var mockNotificationRepository: MockNotificationRepository

    private lateinit var patternInteractor: PatternInteractor
    private lateinit var appConfigurationInteractor: AppConfigurationInteractor
    private lateinit var isNumberInContactsUseCase: IsNumberInContactsUseCase
    private lateinit var isNumberBlockedByPatternUseCase: IsNumberBlockedByPatternUseCase
    private lateinit var showBlockedCallNotificationUseCase: ShowBlockedCallNotificationUseCase
    private lateinit var callScreeningDecisionInteractor: CallScreeningDecisionInteractor

    private lateinit var callScreenerService: MockCallScreenerService

    @Before
    fun setUp() {
        // Initialize mock repositories
        mockPatternRepository = MockPatternRepository()
        mockAppConfigurationRepository = MockAppConfigurationRepository()
        mockContactsRepository = MockContactsRepository()
        mockCallHistoryRepository = MockCallHistoryRepository()
        mockNotificationRepository = MockNotificationRepository()

        // Initialize use cases/interactors
        patternInteractor = PatternInteractor(mockPatternRepository)
        appConfigurationInteractor = AppConfigurationInteractor(mockAppConfigurationRepository)
        isNumberInContactsUseCase = IsNumberInContactsUseCase(mockContactsRepository)
        isNumberBlockedByPatternUseCase = IsNumberBlockedByPatternUseCase(mockPatternRepository)
        showBlockedCallNotificationUseCase =
            ShowBlockedCallNotificationUseCase(mockNotificationRepository)

        // Initialize the new interactor
        callScreeningDecisionInteractor = CallScreeningDecisionInteractor(
            appConfigurationInteractor,
            isNumberInContactsUseCase,
            isNumberBlockedByPatternUseCase
        )

        // Initialize service
        callScreenerService = MockCallScreenerService(
            appConfigurationInteractor,
            callScreeningDecisionInteractor,
            showBlockedCallNotificationUseCase,
            mockCallHistoryRepository
        )
    }

    private fun setConfiguration(
        isBlockByPatternEnable: Boolean = true
    ) {
        val config = ConfigurationModel(
            isScreenRoleGrand = true,
            isBlockByPatternEnable = isBlockByPatternEnable,
            isPushEnable = true,
            numberOfBlockCallToStore = 100,
            language = AppLanguage.ENG.code,
            theme = AppThemeColor.DARK.themeName
        )
        appConfigurationInteractor.updateConfig(config)
    }

    private fun checkPhone(
        phoneNumber: String,
        phonePatterns: List<PhonePattern>,
        expectedResult: ScreeningResult
    ) {
        mockPatternRepository.savePhonePatterns(phonePatterns)
        val result = callScreeningDecisionInteractor.screenCall(phoneNumber)

        assertEquals(expectedResult.shouldBlock, result.shouldBlock)
        assertEquals(expectedResult.reason.name, result.reason.name)
    }

    @Test
    fun testMoscowPhoneNegative() {
        val phone = "+7(495) 111-22-00".filter { it.isDigit() }
        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "+7495*", isNegativePattern = true, type = PhonePatternType.RUSSIAN_MOBILE)
            ),
            ScreeningResult(true, CallScreeningDecisionInteractor.Reason.BLOCKED_BY_PATTERN)
        )
    }

    @Test
    fun testMoscowPhonePositive() {
        val phone = "+7(495) 111-22-00".filter { it.isDigit() }
        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "+7495*", isNegativePattern = false, type = PhonePatternType.RUSSIAN_MOBILE)
            ),
            ScreeningResult(false, CallScreeningDecisionInteractor.Reason.ALLOWED_BY_DEFAULT)
        )
    }

    @Test
    fun testAllowStartFrom8800Positive() {
        val phone = "8(800) 111-22-00".filter { it.isDigit() }
        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "8800*", isNegativePattern = false, type = PhonePatternType.RUSSIAN_TOLL_FREE)
            ),
            ScreeningResult(false, CallScreeningDecisionInteractor.Reason.ALLOWED_BY_DEFAULT)
        )
    }

    @Test
    fun testAllowStartFrom8800Negative() {
        val phone = "8(800) 111-22-00".filter { it.isDigit() }
        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "8800*", isNegativePattern = true, type = PhonePatternType.RUSSIAN_TOLL_FREE)
            ),
            ScreeningResult(true, CallScreeningDecisionInteractor.Reason.BLOCKED_BY_PATTERN)
        )
    }

    @Test
    fun testAllowAnyNegative() {
        val phone = "8(800) 111-22-00".filter { it.isDigit() }
        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "*", isNegativePattern = false, type = PhonePatternType.RUSSIAN_MOBILE)
            ),
            ScreeningResult(false, CallScreeningDecisionInteractor.Reason.ALLOWED_BY_DEFAULT)
        )
    }

    @Test
    fun testBlockAnyNegative() {
        val phone = "8(800) 111-22-00".filter { it.isDigit() }
        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "*", isNegativePattern = true, type = PhonePatternType.RUSSIAN_MOBILE)
            ),
            ScreeningResult(true, CallScreeningDecisionInteractor.Reason.BLOCKED_BY_PATTERN)
        )

        checkPhone(
            phone,
            listOf(
                PhonePattern(pattern = "8800*", isNegativePattern = false, type = PhonePatternType.RUSSIAN_TOLL_FREE),
                PhonePattern(pattern = "*", isNegativePattern = true, type = PhonePatternType.RUSSIAN_MOBILE),
            ),
            ScreeningResult(false, CallScreeningDecisionInteractor.Reason.ALLOWED_BY_DEFAULT)
        )
    }

    @Test
    fun checkProcess() {
        // Step 1: Configure app to enable pattern blocking
        setConfiguration()

        // Step 2: Add a blocking pattern
        val blockingPattern = PhonePattern(pattern = "+7***", isNegativePattern = true, type = PhonePatternType.RUSSIAN_MOBILE)
        patternInteractor.addPhonePattern(blockingPattern)

        // Step 3: Verify pattern was saved
        val savedPatterns = mockPatternRepository.getPhonePatterns()
        assertEquals(1, savedPatterns.size)
        assertEquals(blockingPattern, savedPatterns[0])

        // Step 4: Test call screening with a number matching the pattern
        val testNumber = "+74952223344".filter { it.isDigit() }
        callScreenerService.onScreenCall(testNumber)

        // Step 5: Verify call was blocked
        assertEquals(MockCallScreenerService.CallAction.BLOCK, callScreenerService.lastCallAction)
        assertEquals(testNumber, callScreenerService.lastPhoneNumber)

        // Step 6: Verify call was recorded in history
        assertEquals(1, mockCallHistoryRepository.getBlockedCallsCount())
        assertEquals(0, mockCallHistoryRepository.getAllowedCallsCount())

        // Step 7: Verify notification was shown
        assertEquals(1, mockNotificationRepository.getNotificationCount())
        assertEquals(testNumber, mockNotificationRepository.getLastNotification()?.phoneNumber)
    }

}
