package com.example.expensetracker.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.databinding.ItemTransactionBinding
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.utils.toCurrency
import com.example.expensetracker.utils.toDisplayString

class TransactionAdapter(
    private val onItemClick: (Expense) -> Unit
) : ListAdapter<Expense, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvTitle.text = expense.title
            val isIncome = expense.type == TransactionType.INCOME

            if (isIncome) {
                binding.tvCategory.visibility = android.view.View.GONE
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_income)
            } else {
                binding.tvCategory.visibility = android.view.View.VISIBLE
                binding.tvCategory.text = expense.category.displayName
                binding.ivCategoryIcon.setImageResource(expense.category.iconResId)
            }

            binding.tvDate.text = expense.date.toDisplayString()
            val prefix = if (isIncome) "+" else "-"
            binding.tvAmount.text = "$prefix${expense.amount.toCurrency()}"
            binding.tvAmount.setTextColor(
                itemView.context.getColor(
                    if (isIncome) R.color.income_green else R.color.expense_red
                )
            )

            itemView.setOnClickListener { onItemClick(expense) }
        }
    }
}
