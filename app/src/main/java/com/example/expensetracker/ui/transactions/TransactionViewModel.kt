package com.example.expensetracker.ui.transactions

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
import javax.inject.Inject

enum class SortOrder {
    DATE_NEWEST, DATE_OLDEST, CATEGORY
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allExpenses = MutableStateFlow<List<Expense>>(emptyList())
    private val _filteredExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _filteredExpenses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_NEWEST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private var searchQuery = ""
    private var filterType: TransactionType? = null
    private var filterCategory: Category? = null

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            getExpensesUseCase(userId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _allExpenses.value = resource.data
                        applyFilters()
                        _isLoading.value = false
                    }
                    is Resource.Error -> _isLoading.value = false
                    is Resource.Loading -> _isLoading.value = true
                }
            }
        }
    }

    fun search(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun filterByType(type: TransactionType?) {
        filterType = type
        applyFilters()
    }

    fun filterByCategory(category: Category?) {
        filterCategory = category
        applyFilters()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        applyFilters()
    }

    fun clearFilters() {
        searchQuery = ""
        filterType = null
        filterCategory = null
        applyFilters()
    }

    private fun applyFilters() {
        var result = _allExpenses.value
        if (searchQuery.isNotBlank()) {
            result = result.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.note.contains(searchQuery, ignoreCase = true) ||
                        it.category.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
        filterType?.let { type -> result = result.filter { it.type == type } }
        filterCategory?.let { cat -> result = result.filter { it.category == cat } }

        result = when (_sortOrder.value) {
            SortOrder.DATE_NEWEST -> result.sortedByDescending { it.date }
            SortOrder.DATE_OLDEST -> result.sortedBy { it.date }
            SortOrder.CATEGORY -> result.sortedBy { it.category.displayName }
        }

        _filteredExpenses.value = result
    }
}
