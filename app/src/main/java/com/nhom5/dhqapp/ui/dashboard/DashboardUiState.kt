package com.nhom5.dhqapp.ui.dashboard

import com.nhom5.dhqapp.data.Transaction

data class TransactionWithCat(
    val transaction: Transaction,
    val categoryName: String
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val totalIncome: Double,
        val totalExpense: Double,
        val balance: Double,
        val recent: List<TransactionWithCat>
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}