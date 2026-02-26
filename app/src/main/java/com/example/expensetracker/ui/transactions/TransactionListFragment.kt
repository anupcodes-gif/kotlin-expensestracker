package com.example.expensetracker.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentTransactionsBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupChips()
        setupSortChips()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { expense ->
            val action = TransactionListFragmentDirections
                .actionTransactionsFragmentToAddExpenseFragment(expense.id)
            findNavController().navigate(action)
        }
        binding.rvTransactions.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: "")
                return true
            }
        })
    }

    private fun setupChips() {
        binding.chipAll.setOnClickListener { viewModel.clearFilters() }
        binding.chipIncome.setOnClickListener { viewModel.filterByType(com.example.expensetracker.domain.model.TransactionType.INCOME) }
        binding.chipExpense.setOnClickListener { viewModel.filterByType(com.example.expensetracker.domain.model.TransactionType.EXPENSE) }
    }

    private fun setupSortChips() {
        binding.chipSortNewest.setOnClickListener { viewModel.setSortOrder(SortOrder.DATE_NEWEST) }
        binding.chipSortOldest.setOnClickListener { viewModel.setSortOrder(SortOrder.DATE_OLDEST) }
        binding.chipSortCategory.setOnClickListener { viewModel.setSortOrder(SortOrder.CATEGORY) }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.expenses.collect { expenses ->
                        adapter.submitList(expenses)
                        binding.tvEmpty.isVisible = expenses.isEmpty()
                        binding.rvTransactions.isVisible = expenses.isNotEmpty()
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.progressBar.isVisible = loading
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
