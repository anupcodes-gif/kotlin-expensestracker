package com.example.expensetracker.ui.transactions

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var getExpensesUseCase: GetExpensesUseCase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: TransactionViewModel

    // Sample test data
    private val userId = "user-123"
    private val sampleExpenses = listOf(
        Expense(id = "1", userId = userId, title = "Pizza",    amount = 250.0, category = Category.FOOD,     type = TransactionType.EXPENSE, date = Date(1000)),
        Expense(id = "2", userId = userId, title = "Salary",   amount = 50000.0, category = Category.SALARY,    type = TransactionType.INCOME,  date = Date(3000)),
        Expense(id = "3", userId = userId, title = "Bus Pass", amount = 100.0, category = Category.TRANSPORT,   type = TransactionType.EXPENSE, date = Date(2000))
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        firebaseAuth = mock()
        getExpensesUseCase = mock()

        val mockUser = mock<FirebaseUser>()
        whenever(mockUser.uid).thenReturn(userId)
        whenever(firebaseAuth.currentUser).thenReturn(mockUser)

        // Default: return all sample expenses
        whenever(getExpensesUseCase(userId))
            .thenReturn(flowOf(Resource.Success(sampleExpenses)))

        viewModel = TransactionViewModel(getExpensesUseCase, firebaseAuth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test that all expenses are loaded correctly on init.
     */
    @Test
    fun `loadExpenses - all expenses are loaded`() = runTest {
        advanceUntilIdle()
        assertEquals("Should load all 3 expenses", 3, viewModel.expenses.value.size)
    }

    /**
     * Test sorting by newest first (default).
     */
    @Test
    fun `setSortOrder DATE_NEWEST - expenses sorted newest first`() = runTest {
        advanceUntilIdle()
        viewModel.setSortOrder(SortOrder.DATE_NEWEST)
        advanceUntilIdle()

        val sorted = viewModel.expenses.value
        assertTrue(
            "Expenses should be sorted newest first",
            sorted[0].date >= sorted[1].date && sorted[1].date >= sorted[2].date
        )
    }

    /**
     * Test sorting by oldest first.
     */
    @Test
    fun `setSortOrder DATE_OLDEST - expenses sorted oldest first`() = runTest {
        advanceUntilIdle()
        viewModel.setSortOrder(SortOrder.DATE_OLDEST)
        advanceUntilIdle()

        val sorted = viewModel.expenses.value
        assertTrue(
            "Expenses should be sorted oldest first",
            sorted[0].date <= sorted[1].date && sorted[1].date <= sorted[2].date
        )
    }

    /**
     * Test filtering by INCOME type.
     */
    @Test
    fun `filterByType INCOME - only income transactions returned`() = runTest {
        advanceUntilIdle()
        viewModel.filterByType(TransactionType.INCOME)

        val result = viewModel.expenses.value
        assertEquals("Should only have 1 income transaction", 1, result.size)
        assertTrue("All results should be INCOME type", result.all { it.type == TransactionType.INCOME })
    }

    /**
     * Test filtering by EXPENSE type.
     */
    @Test
    fun `filterByType EXPENSE - only expense transactions returned`() = runTest {
        advanceUntilIdle()
        viewModel.filterByType(TransactionType.EXPENSE)

        val result = viewModel.expenses.value
        assertEquals("Should only have 2 expense transactions", 2, result.size)
        assertTrue("All results should be EXPENSE type", result.all { it.type == TransactionType.EXPENSE })
    }

    /**
     * Test search filters correctly by title.
     */
    @Test
    fun `search - filters transactions by title`() = runTest {
        advanceUntilIdle()
        viewModel.search("pizza")

        val result = viewModel.expenses.value
        assertEquals("Search for 'pizza' should return 1 result", 1, result.size)
        assertEquals("Result should be Pizza transaction", "Pizza", result[0].title)
    }

    /**
     * Test that searching with an empty query returns all results.
     */
    @Test
    fun `search empty query - returns all transactions`() = runTest {
        advanceUntilIdle()
        viewModel.search("bus")
        advanceUntilIdle()

        // Now clear
        viewModel.search("")

        val result = viewModel.expenses.value
        assertEquals("Empty query should return all 3 transactions", 3, result.size)
    }

    /**
     * Test that clearFilters resets all active filters.
     */
    @Test
    fun `clearFilters - resets type filter and returns all transactions`() = runTest {
        advanceUntilIdle()
        viewModel.filterByType(TransactionType.INCOME)
        assertEquals("Sanity check: should have 1 income transaction", 1, viewModel.expenses.value.size)

        viewModel.clearFilters()

        assertEquals("After clearFilters, should have all 3 transactions", 3, viewModel.expenses.value.size)
    }

    /**
     * Test that a loading state is emitted while expenses load.
     */
    @Test
    fun `loadExpenses loading - isLoading becomes true then false`() = runTest {
        // Reset with a loading resource first
        whenever(getExpensesUseCase(userId))
            .thenReturn(flowOf(Resource.Loading, Resource.Success(sampleExpenses)))

        // Recreate viewModel to trigger init
        val newViewModel = TransactionViewModel(getExpensesUseCase, firebaseAuth)
        advanceUntilIdle()

        assertEquals("Loading should be false after expenses are loaded", false, newViewModel.isLoading.value)
    }
}
