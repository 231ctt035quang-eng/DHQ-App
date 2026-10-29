package com.nhom5.dhqapp.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom5.dhqapp.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel(
    private val userId: Int,
    private val db: AppDatabase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        load("MONTH")
    }

    fun load(period: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading
            try {
                val allTxns = db.transactionDao().getByUserId(userId)
                val now = System.currentTimeMillis()
                val minTime = when (period) {
                    "WEEK" -> now - 7L * 24 * 3600 * 1000
                    "MONTH" -> now - 30L * 24 * 3600 * 1000
                    else -> 0L
                }
                val filteredTxns = allTxns.filter { it.date >= minTime }

                val totalIncome = filteredTxns.filter { it.type == "THU" }.sumOf { it.amount }
                val totalExpense = filteredTxns.filter { it.type == "CHI" }.sumOf { it.amount }

                val expenseTxns = filteredTxns.filter { it.type == "CHI" }
                val groupedExpenses = expenseTxns.groupBy { it.categoryId }

                val breakdown = groupedExpenses.map { (catId, txns) ->
                    val sum = txns.sumOf { it.amount }
                    val cat = db.categoryDao().getById(catId)
                    val catName = cat?.name ?: "Khác"
                    val percent = if (totalExpense > 0) (sum / totalExpense).toFloat() else 0f
                    CategoryStat(name = catName, amount = sum, percent = percent)
                }.sortedByDescending { it.amount }

                val topCat = breakdown.firstOrNull()?.name

                _uiState.value = ReportUiState.Success(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    breakdown = breakdown,
                    topCategory = topCat
                )
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error("Không thể tải báo cáo thống kê")
            }
        }
    }
}
