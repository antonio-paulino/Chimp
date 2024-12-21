package pt.isel.pdm.chimp.credentials

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.invitations.ImInvitation
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.domain.wrappers.email.Email
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.domain.wrappers.password.Password
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsScreenState
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsViewModel
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ReplaceMainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CredentialsViewModelTests {
    private lateinit var services: ChimpService
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: CredentialsViewModel

    companion object {
        private const val VALID_PASSWORD = "validPassword123"
        private const val VALID_USERNAME = "validUsername"
        private const val VALID_EMAIL = "validEmail@isel.pt"
        private const val VALID_TOKEN = "valid token"

        private const val INVALID_USERNAME = "invalidUsername"
        private const val INVALID_PASSWORD = "invalidPassword123"

        private val mockProblem =
            Problem.ServiceProblem(
                status = 400,
                title = "Bad Request",
                detail = "Invalid credentials",
                type = "invalid-credentials",
            )
    }

    @get:Rule
    val dispatcherRule = ReplaceMainDispatcherRule()

    @Before
    fun setup() {
        services = mock()
        sessionManager = mock()
        viewModel = CredentialsViewModel(services, sessionManager)
    }

    @Test
    fun do_login_with_invalid_credentials_transitions_to_login_error() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            whenever(services.authService.login(INVALID_USERNAME, null, INVALID_PASSWORD))
                .thenReturn(failure(mockProblem))

            // Act
            viewModel.doLogin(INVALID_USERNAME, INVALID_PASSWORD)

            // Assert
            advanceUntilIdle()
        }

    @Test
    fun do_login_with_valid_credentials_transitions_to_success() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            whenever(services.authService.login(VALID_USERNAME, null, VALID_PASSWORD))
                .thenReturn(success(mock()))

            // Act
            viewModel.doLogin(VALID_USERNAME, VALID_PASSWORD)

            // Assert
            advanceUntilIdle()
            assert(viewModel.state.firstOrNull() is CredentialsScreenState.Success)
        }

    @Test
    fun do_register_with_invalid_credentials_transitions_to_register_error() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            val name = Name(INVALID_USERNAME)
            val email = Email(VALID_EMAIL)
            val password = Password(VALID_PASSWORD)
            val token = ImInvitation(UUID.randomUUID(), LocalDateTime.now())

            whenever(services.authService.register(name, email, password, token))
                .thenReturn(failure(mockProblem))

            // Act
            viewModel.doRegister(VALID_EMAIL, INVALID_USERNAME, VALID_PASSWORD, VALID_TOKEN)

            // Assert
            advanceUntilIdle()
            val vmState = viewModel.state.firstOrNull()
            assert(vmState is CredentialsScreenState.RegisterError)
            vmState as CredentialsScreenState.RegisterError
            assert(vmState.email == VALID_EMAIL)
            assert(vmState.username == INVALID_USERNAME)
            assert(vmState.password == VALID_PASSWORD)
            assert(vmState.token == VALID_TOKEN)
            assert(vmState.problem is Problem.ServiceProblem)
        }

    @Test
    fun do_register_with_valid_credentials_transitions_to_success() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            val name = Name(VALID_USERNAME)
            val email = Email(VALID_EMAIL)
            val password = Password(VALID_PASSWORD)
            val token = ImInvitation(UUID.randomUUID(), LocalDateTime.now())

            whenever(services.authService.register(name, email, password, token))
                .thenReturn(success(mock()))

            // Act
            viewModel.doRegister(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD, VALID_TOKEN)

            // Assert
            advanceUntilIdle()
            assert(viewModel.state.firstOrNull() is CredentialsScreenState.Success)
        }

    @Test
    fun login_on_register_click_transitions_to_register() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            viewModel = CredentialsViewModel(services, sessionManager, CredentialsScreenState.Login())

            // Act
            viewModel.onRegisterClick()

            // Assert
            advanceUntilIdle()
            assert(viewModel.state.firstOrNull() is CredentialsScreenState.Register)
        }

    @Test
    fun login_error_on_register_click_transitions_to_register() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            viewModel = CredentialsViewModel(services, sessionManager, CredentialsScreenState.LoginError(VALID_USERNAME, VALID_PASSWORD, mockProblem))

            // Act
            viewModel.onRegisterClick()

            // Assert
            advanceUntilIdle()
            assert(viewModel.state.firstOrNull() is CredentialsScreenState.Register)
        }

    @Test
    fun register_on_login_click_transitions_to_login() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            viewModel = CredentialsViewModel(services, sessionManager, CredentialsScreenState.Register())

            // Act
            viewModel.onLoginClick()

            // Assert
            advanceUntilIdle()
            assert(viewModel.state.firstOrNull() is CredentialsScreenState.Login)
        }

    @Test
    fun register_error_on_login_click_transitions_to_login() =
        runTest(dispatcherRule.testDispatcher) {
            // Arrange
            viewModel = CredentialsViewModel(services, sessionManager, CredentialsScreenState.RegisterError(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD, VALID_TOKEN, mockProblem))

            // Act
            viewModel.onLoginClick()

            // Assert
            advanceUntilIdle()
            assert(viewModel.state.firstOrNull() is CredentialsScreenState.Login)
        }
}
