package com.nhom5.dhqapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom5.dhqapp.data.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userId: Int,
    private val db: AppDatabase
) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            delay(400) // giả lập tải dữ liệu
            try {
                val txns = db.transactionDao().getByUserId(userId)
                val income = txns.filter { it.type == "THU" }.sumOf { it.amount }
                val expense = txns.filter { it.type == "CHI" }.sumOf { it.amount }
                val recent = txns.take(5).map { t ->
                    val cat = db.categoryDao().getById(t.categoryId)
                    TransactionWithCat(t, cat?.name ?: "Khác")
                }
                _uiState.value = DashboardUiState.Success(income, expense, income - expense, recent)
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Không thể tải thông tin tài chính, vui lòng thử lại")
            }
        }
    }
}