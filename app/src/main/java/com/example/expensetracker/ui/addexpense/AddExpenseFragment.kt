package com.example.expensetracker.ui.addexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentAddExpenseBinding
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.utils.showSnackbar
import com.example.expensetracker.utils.toDisplayString
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddExpenseViewModel by viewModels()
    private val args: AddExpenseFragmentArgs by navArgs()

    private var selectedDate: Date = Date()
    private var selectedCategory: Category = Category.OTHER
    private var selectedType: TransactionType = TransactionType.EXPENSE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryChips()
        setupDatePicker()
        setupTypeToggle()
        setupButtons()
        observeViewModel()

        if (args.expenseId.isNotBlank()) {
            viewModel.loadExpense(args.expenseId)
            binding.btnDelete.isVisible = true
            binding.tvTitle.text = "Edit Expense"
        } else {
            binding.btnDelete.isVisible = false
            binding.tvTitle.text = "Add Expense"
            binding.tvSelectedDate.text = selectedDate.toDisplayString()
        }
    }

    private fun setupCategoryChips() {
        // Default selection is already set via android:checked="true" on chipOther in XML
        binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val chip = group.findViewById<Chip>(checkedId)
            val tag = chip?.tag?.toString() ?: return@setOnCheckedStateChangeListener
            selectedCategory = try {
                Category.valueOf(tag)
            } catch (e: IllegalArgumentException) {
                Category.OTHER
            }
        }
    }

    private fun setupDatePicker() {
        binding.btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { time = selectedDate }
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDate = calendar.time
                    binding.tvSelectedDate.text = selectedDate.toDisplayString()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTypeToggle() {
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedType = when (checkedId) {
                    R.id.btnIncome -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                updateTypeColors()
            }
        }
        binding.toggleType.check(R.id.btnExpense)
    }

    private fun updateTypeColors() {
        val isExpense = selectedType == TransactionType.EXPENSE
        val color = if (isExpense) R.color.expense_red else R.color.income_green
        // update UI tint if desired
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val amountStr = binding.etAmount.text.toString().trim()
            val note = binding.etNote.text.toString().trim()

            if (title.isBlank()) {
                binding.tilTitle.error = "Title is required"
                return@setOnClickListener
            }
            if (amountStr.isBlank()) {
                binding.tilAmount.error = "Amount is required"
                return@setOnClickListener
            }
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }

            binding.tilTitle.error = null
            binding.tilAmount.error = null

            viewModel.saveExpense(title, amount, selectedCategory, selectedType, selectedDate, note)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteExpense()
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.btnSave.isEnabled = !state.isLoading

                    if (state.isSaved || state.isDeleted) {
                        findNavController().navigateUp()
                        return@collect
                    }

                    // Populate fields when editing
                    state.expense?.let { expense ->
                        binding.etTitle.setText(expense.title)
                        binding.etAmount.setText(expense.amount.toString())
                        binding.etNote.setText(expense.note)
                        selectedDate = expense.date
                        selectedCategory = expense.category
                        selectedType = expense.type
                        binding.tvSelectedDate.text = expense.date.toDisplayString()
                        // Select the correct chip for the loaded expense category
                        for (i in 0 until binding.chipGroupCategory.childCount) {
                            val chip = binding.chipGroupCategory.getChildAt(i) as? Chip
                            if (chip?.tag?.toString() == expense.category.name) {
                                chip.isChecked = true
                                break
                            }
                        }
                        if (expense.type == TransactionType.INCOME) binding.toggleType.check(R.id.btnIncome)
                        else binding.toggleType.check(R.id.btnExpense)
                    }

                    state.error?.let { error ->
                        binding.root.showSnackbar(error)
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
