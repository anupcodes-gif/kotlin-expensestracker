package com.example.expensetracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class ChartData(
    val pieEntries: Map<String, Float>,  // category -> amount
    val barEntries: Map<String, Float>   // month label -> amount
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _chartData = MutableStateFlow(ChartData(emptyMap(), emptyMap()))
    val chartData: StateFlow<ChartData> = _chartData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            getExpensesUseCase(userId).collect { resource ->
                if (resource is Resource.Success) {
                    val expenses = resource.data.filter { it.type == TransactionType.EXPENSE }

                    // Pie chart: by category
                    val pieData = expenses
                        .groupBy { it.category }
                        .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }
                        .mapKeys { it.key.displayName }

                    // Bar chart: last 6 months
                    val barData = buildBarData(expenses)
                    _chartData.value = ChartData(pieData, barData)
                    _isLoading.value = false
                } else if (resource is Resource.Error) {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun buildBarData(expenses: List<Expense>): Map<String, Float> {
        val monthMap = mutableMapOf<String, Float>()
        val calendar = Calendar.getInstance()
        val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

        for (i in 5 downTo 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -i)
            val key = months[cal.get(Calendar.MONTH)]
            monthMap[key] = 0f
        }

        expenses.forEach { expense ->
            val cal = Calendar.getInstance().apply { time = expense.date }
            val diff = (calendar.get(Calendar.YEAR) - cal.get(Calendar.YEAR)) * 12 +
                    (calendar.get(Calendar.MONTH) - cal.get(Calendar.MONTH))
            if (diff in 0..5) {
                val key = months[cal.get(Calendar.MONTH)]
                monthMap[key] = (monthMap[key] ?: 0f) + expense.amount.toFloat()
            }
        }
        return monthMap
    }
}
