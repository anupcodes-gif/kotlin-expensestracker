package com.example.expensetracker.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // Replaces the Main dispatcher with a test dispatcher for coroutine testing
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        firebaseAuth = mock()
        // Make currentUser return null by default (logged out)
        whenever(firebaseAuth.currentUser).thenReturn(null)
        viewModel = AuthViewModel(firebaseAuth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test that initial state has no user and no error.
     */
    @Test
    fun `initial state - no user, no error, not loading`() {
        val state = viewModel.authState.value
        assertNull("Initial user should be null", state.user)
        assertNull("Initial error should be null", state.error)
        assertEquals("Should not be loading initially", false, state.isLoading)
    }

    /**
     * Test that a successful login updates the state with the user.
     */
    @Test
    fun `login success - state updates with isSuccess true`() = runTest {
        val mockUser = mock<FirebaseUser>()
        val mockAuthResult = mock<AuthResult>()
        whenever(mockAuthResult.user).thenReturn(mockUser)

        // Mock Firebase sign-in to return a successful task
        val mockTask = com.google.android.gms.tasks.Tasks.forResult(mockAuthResult)
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(mockTask)

        viewModel.login("test@gmail.com", "123456")
        advanceUntilIdle() // Let coroutines complete

        val state = viewModel.authState.value
        assertEquals("isSuccess should be true on successful login", true, state.isSuccess)
        assertEquals("User should be set on success", mockUser, state.user)
        assertNull("Error should be null on success", state.error)

        verify(firebaseAuth).signInWithEmailAndPassword("test@gmail.com", "123456")
    }

    /**
     * Test that a failed login updates the state with an error message.
     */
    @Test
    fun `login failure - state updates with error message`() = runTest {
        val errorMsg = "The email address is badly formatted."
        val mockTask = com.google.android.gms.tasks.Tasks.forException<AuthResult>(
            Exception(errorMsg)
        )
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(mockTask)

        viewModel.login("bad-email", "123456")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertEquals("Error message should be set on failure", errorMsg, state.error)
        assertEquals("isSuccess should be false on failure", false, state.isSuccess)
        assertNull("User should be null on failure", state.user)
    }

    /**
     * Test that logout clears the user from state.
     */
    @Test
    fun `logout - clears user from state`() {
        viewModel.logout()

        val state = viewModel.authState.value
        assertNull("User should be null after logout", state.user)
        verify(firebaseAuth).signOut()
    }

    /**
     * Test that clearError removes the error from state.
     */
    @Test
    fun `clearError - removes error from auth state`() = runTest {
        // Simulate a failed login first to introduce an error
        val mockTask = com.google.android.gms.tasks.Tasks.forException<AuthResult>(
            Exception("Some error")
        )
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(mockTask)
        viewModel.login("test@gmail.com", "wrongpassword")
        advanceUntilIdle()

        // Verify error is present, then clear it
        assertEquals("Error should be present before clearing", "Some error", viewModel.authState.value.error)
        viewModel.clearError()
        assertNull("Error should be null after clearError", viewModel.authState.value.error)
    }
}
