package pt.isel.pdm.chimp.credentials

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pt.isel.pdm.chimp.ui.components.inputs.EMAIL_TEXT_FIELD_TEST_TAG
import pt.isel.pdm.chimp.ui.components.inputs.NAME_TEXT_FIELD_TEST_TAG
import pt.isel.pdm.chimp.ui.components.inputs.PASSWORD_TEXT_FIELD_TEST_TAG
import pt.isel.pdm.chimp.ui.components.inputs.SHOW_BUTTON_TEST_TAG
import pt.isel.pdm.chimp.ui.components.inputs.TOKEN_TEXT_FIELD_TEST_TAG
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsScreen
import pt.isel.pdm.chimp.ui.screens.credentials.CredentialsScreenState
import pt.isel.pdm.chimp.ui.screens.credentials.views.LOGIN_BUTTON_TEST_TAG
import pt.isel.pdm.chimp.ui.screens.credentials.views.LOGIN_VIEW_EMAIL_OR_USERNAME_TEST_TAG
import pt.isel.pdm.chimp.ui.screens.credentials.views.NAVIGATE_TO_LOGIN_TEST_TAG
import pt.isel.pdm.chimp.ui.screens.credentials.views.NAVIGATE_TO_REGISTER_TEST_TAG
import pt.isel.pdm.chimp.ui.screens.credentials.views.REGISTER_BUTTON_TEST_TAG

@RunWith(AndroidJUnit4::class)
class CredentialsScreenTests {
    @get:Rule
    val testRule = createComposeRule()

    @Test
    fun test_login_view_displays_fields() {
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Login(
                        emailOrUsername = "",
                        password = "",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = {},
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> },
            )
        }
        val nameField = testRule.onNodeWithTag(LOGIN_VIEW_EMAIL_OR_USERNAME_TEST_TAG)
        val passwordField = testRule.onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)

        nameField.assertExists()
        nameField.assertTextContains("")

        passwordField.assertExists()
        passwordField.assertTextContains("")

        val button = testRule.onNodeWithTag(LOGIN_BUTTON_TEST_TAG)
        button.assertExists()
        button.assertIsNotEnabled()
    }

    @Test
    fun test_login_view_displays_fields_with_initial_values() {
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Login(
                        emailOrUsername = "test",
                        password = "test",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = {},
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> },
            )
        }
        val button = testRule.onNodeWithTag(LOGIN_BUTTON_TEST_TAG)
        button.assertExists()
        button.assertIsEnabled()
    }

    @Test
    fun test_login_view_on_login_click() {
        var loginClicked = false
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Login(
                        emailOrUsername = "test",
                        password = "test",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = {},
                doLogin = { _, _ -> loginClicked = true },
                doRegister = { _, _, _, _ -> },
            )
        }
        val emailField = testRule.onNodeWithTag(LOGIN_VIEW_EMAIL_OR_USERNAME_TEST_TAG)
        val passwordField = testRule.onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)

        emailField.assertTextContains("test")
        passwordField.assertTextContains("••••")

        val button = testRule.onNodeWithTag(LOGIN_BUTTON_TEST_TAG)
        button.performClick()
        assert(loginClicked)
    }

    @Test
    fun test_login_view_on_navigate_register_click() {
        var registerClicked = false
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Login(
                        emailOrUsername = "test",
                        password = "test",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = { registerClicked = true },
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> },
            )
        }
        val navigateToRegister = testRule.onNodeWithTag(NAVIGATE_TO_REGISTER_TEST_TAG)
        navigateToRegister.performClick()
        assert(registerClicked)
    }

    @Test
    fun test_login_view_show_password() {
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Login(
                        emailOrUsername = "test",
                        password = "test",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = {},
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> },
            )
        }
        val passwordField = testRule.onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)
        val showButton = testRule.onNodeWithTag(SHOW_BUTTON_TEST_TAG)

        passwordField.assertTextContains("••••")
        showButton.performClick()
        passwordField.assertTextContains("test")
    }

    @Test
    fun register_view_displays_fields() {
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Register(
                        email = "",
                        username = "",
                        password = "",
                        token = "",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = {},
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> },
            )
        }
        val emailField = testRule.onNodeWithTag(EMAIL_TEXT_FIELD_TEST_TAG)
        val nameField = testRule.onNodeWithTag(NAME_TEXT_FIELD_TEST_TAG)
        val passwordField = testRule.onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)
        val tokenField = testRule.onNodeWithTag(TOKEN_TEXT_FIELD_TEST_TAG)

        emailField.assertExists()
        nameField.assertExists()
        passwordField.assertExists()
        tokenField.assertExists()

        val button = testRule.onNodeWithTag(REGISTER_BUTTON_TEST_TAG)
        button.assertExists()
        button.assertIsNotEnabled()
    }

    @Test
    fun register_view_invalid_fields() {
        var registerClicked = false
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Register(
                        email = "",
                        username = "",
                        password = "",
                        token = "",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = {},
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> registerClicked = true },
            )
        }
        val nameField = testRule.onNodeWithTag(NAME_TEXT_FIELD_TEST_TAG)
        val emailField = testRule.onNodeWithTag(EMAIL_TEXT_FIELD_TEST_TAG)
        val passwordField = testRule.onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)
        val tokenField = testRule.onNodeWithTag(TOKEN_TEXT_FIELD_TEST_TAG)

        nameField.performTextInput("1")
        emailField.performTextInput("2")
        passwordField.performTextInput("3")
        tokenField.performTextInput("4")

        val button = testRule.onNodeWithTag(REGISTER_BUTTON_TEST_TAG)
        button.assertIsNotEnabled()
        button.performClick()
        assert(!registerClicked)
    }

    @Test
    fun register_view_valid_fields() {
        var registerClicked = false
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Register(
                        email = "",
                        username = "",
                        password = "",
                        token = "",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = {},
                onRegisterClick = { },
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> registerClicked = true },
            )
        }
        val nameField = testRule.onNodeWithTag(NAME_TEXT_FIELD_TEST_TAG)
        val emailField = testRule.onNodeWithTag(EMAIL_TEXT_FIELD_TEST_TAG)
        val passwordField = testRule.onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)
        val tokenField = testRule.onNodeWithTag(TOKEN_TEXT_FIELD_TEST_TAG)

        nameField.performTextInput("name")
        emailField.performTextInput("test@pdm.pt")
        passwordField.performTextInput("Password123")
        tokenField.performTextInput("00000000-0000-0000-0000-000000000000")

        val button = testRule.onNodeWithTag(REGISTER_BUTTON_TEST_TAG)
        button.assertIsEnabled()
        button.performClick()
        assert(registerClicked)
    }

    @Test
    fun test_register_view_on_navigate_login_click() {
        var loginClicked = false
        testRule.setContent {
            CredentialsScreen(
                state =
                    CredentialsScreenState.Register(
                        email = "test",
                        username = "test",
                        password = "test",
                        token = "test",
                    ),
                onSuccessfulAuthentication = {},
                onLoginClick = { loginClicked = true },
                onRegisterClick = {},
                doLogin = { _, _ -> },
                doRegister = { _, _, _, _ -> },
            )
        }
        val navigateToLogin = testRule.onNodeWithTag(NAVIGATE_TO_LOGIN_TEST_TAG)
        navigateToLogin.performClick()
        assert(loginClicked)
    }
}
