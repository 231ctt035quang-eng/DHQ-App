package com.nhom5.dhqapp.ui.report

data class CategoryStat(
    val name: String,
    val amount: Double,
    val percent: Float   // 0..1
)

sealed interface ReportUiState {
    data object Loading : ReportUiState
    data class Success(
        val totalIncome: Double,
        val totalExpense: Double,
        val breakdown: List<CategoryStat>,
        val topCategory: String?
    ) : ReportUiState
    data class Error(val message: String) : ReportUiState
}