package com.nhom5.dhqapp.ui.transaction

data class TransactionListItem(
    val id: Int,
    val categoryName: String,
    val type: String,      // "THU" / "CHI"
    val amount: Double,
    val note: String,
    val date: Long
)

sealed interface TransactionUiState {
    data object Loading : TransactionUiState
    data class Success(val items: List<TransactionListItem>) : TransactionUiState
    data class Error(val message: String) : TransactionUiState
}