package com.example.expensetracker.domain.model

import com.example.expensetracker.R

enum class Category(val displayName: String, val emoji: String, val iconResId: Int) {
    FOOD("Food & Dining", "🍔", R.drawable.ic_cat_food),
    TRANSPORT("Transport", "🚗", R.drawable.ic_cat_transport),
    SHOPPING("Shopping", "🛍️", R.drawable.ic_cat_shopping),
    HEALTH("Health & Medical", "💊", R.drawable.ic_cat_health),
    ENTERTAINMENT("Entertainment", "🎮", R.drawable.ic_cat_entertainment),
    EDUCATION("Education", "📚", R.drawable.ic_cat_education),
    UTILITIES("Bills & Utilities", "💡", R.drawable.ic_cat_utilities),
    SALARY("Salary", "💼", R.drawable.ic_cat_salary),
    FREELANCE("Freelance", "💻", R.drawable.ic_cat_freelance),
    INVESTMENT("Investment", "📈", R.drawable.ic_cat_investment),
    RENT("Rent", "🏠", R.drawable.ic_cat_rent),
    OTHER("Other", "📦", R.drawable.ic_cat_other)
}
