package com.example.expensetracker.ui.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.AddExpenseUseCase
import com.example.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.domain.usecase.UpdateExpenseUseCase
import com.example.expensetracker.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class AddExpenseUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null,
    val expense: Expense? = null
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun loadExpense(expenseId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            getExpensesUseCase(userId).collect { resource ->
                if (resource is Resource.Success) {
                    val expense = resource.data.find { it.id == expenseId }
                    _uiState.value = _uiState.value.copy(expense = expense)
                }
            }
        }
    }

    fun saveExpense(
        title: String,
        amount: Double,
        category: Category,
        type: TransactionType,
        date: Date,
        note: String
    ) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existingExpense = _uiState.value.expense
            val expense = Expense(
                id = existingExpense?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                amount = amount,
                category = category,
                type = type,
                date = date,
                note = note
            )
            val result = if (existingExpense != null) {
                updateExpenseUseCase(expense)
            } else {
                addExpenseUseCase(expense)
            }
            when (result) {
                is Resource.Success -> _uiState.value = AddExpenseUiState(isSaved = true)
                is Resource.Error -> _uiState.value = AddExpenseUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun deleteExpense() {
        val userId = auth.currentUser?.uid ?: return
        val expenseId = _uiState.value.expense?.id ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = deleteExpenseUseCase(expenseId, userId)) {
                is Resource.Success -> _uiState.value = AddExpenseUiState(isDeleted = true)
                is Resource.Error -> _uiState.value = AddExpenseUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
