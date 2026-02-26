package com.example.expensetracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.domain.usecase.GetSummaryUseCase
import com.example.expensetracker.domain.usecase.MonthlySummary
import com.example.expensetracker.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _summary = MutableStateFlow(MonthlySummary(0.0, 0.0, 0.0, emptyMap()))
    val summary: StateFlow<MonthlySummary> = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _recentExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val recentExpenses: StateFlow<List<Expense>> = _recentExpenses.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            getExpensesUseCase(userId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val allExpenses = resource.data
                        _expenses.value = allExpenses

                        // Filter current month
                        val calendar = Calendar.getInstance()
                        val currentMonth = calendar.get(Calendar.MONTH)
                        val currentYear = calendar.get(Calendar.YEAR)
                        val thisMonthExpenses = allExpenses.filter { expense ->
                            val cal = Calendar.getInstance().apply { time = expense.date }
                            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
                        }

                        val income = thisMonthExpenses.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        val expense = thisMonthExpenses.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        val byCategory = thisMonthExpenses
                            .filter { it.type == TransactionType.EXPENSE }
                            .groupBy { it.category.displayName }
                            .mapValues { e -> e.value.sumOf { it.amount } }

                        _summary.value = MonthlySummary(income, expense, income - expense, byCategory)
                        _recentExpenses.value = allExpenses.take(5)
                        _isLoading.value = false
                    }
                    is Resource.Error -> _isLoading.value = false
                    is Resource.Loading -> _isLoading.value = true
                }
            }
        }
    }

    fun getUserName(): String = auth.currentUser?.displayName ?: "User"
    fun getUserEmail(): String = auth.currentUser?.email ?: ""
}
