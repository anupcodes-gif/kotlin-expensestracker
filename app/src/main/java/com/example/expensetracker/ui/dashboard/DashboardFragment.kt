package com.example.expensetracker.ui.dashboard

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
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentDashboardBinding
import com.example.expensetracker.ui.transactions.TransactionAdapter
import com.example.expensetracker.utils.toCurrency
import android.view.animation.AnimationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recentAdapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recentAdapter = TransactionAdapter { expense ->
            val action = DashboardFragmentDirections.actionDashboardFragmentToAddExpenseFragment(expense.id)
            findNavController().navigate(action)
        }
        binding.rvRecentTransactions.adapter = recentAdapter
    }

    private fun setupFab() {
        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addExpenseFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.summary.collect { summary ->
                        binding.tvTotalBalance.text = summary.balance.toCurrency()
                        binding.tvTotalIncome.text = summary.totalIncome.toCurrency()
                        binding.tvTotalExpense.text = summary.totalExpense.toCurrency()

                        val isPositiveBalance = summary.balance >= 0
                        val balanceColor = if (isPositiveBalance)
                            requireContext().getColor(R.color.income_green)
                        else
                            requireContext().getColor(R.color.expense_red)
                        binding.tvTotalBalance.setTextColor(balanceColor)
                    }
                }
                launch {
                    viewModel.recentExpenses.collect { expenses ->
                        recentAdapter.submitList(expenses)
                        binding.tvNoTransactions.isVisible = expenses.isEmpty()
                        binding.rvRecentTransactions.isVisible = expenses.isNotEmpty()
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.progressBar.isVisible = loading
                    }
                }
            }
        }

        binding.tvGreeting.text = "Hello, ${viewModel.getUserName()}"
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.waving_hand)
        binding.tvEmojiHand.startAnimation(animation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
