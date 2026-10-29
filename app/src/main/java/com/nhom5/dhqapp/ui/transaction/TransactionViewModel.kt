package com.nhom5.dhqapp.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.data.Category
import com.nhom5.dhqapp.data.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val userId: Int,
    private val db: AppDatabase
) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            delay(300)
            try {
                _categories.value = db.categoryDao().getAll()
                val txns = db.transactionDao().getByUserId(userId)
                val items = txns.map { t ->
                    val cat = db.categoryDao().getById(t.categoryId)
                    TransactionListItem(t.id, cat?.name ?: "Khác", t.type, t.amount, t.note, t.date)
                }
                _uiState.value = TransactionUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = TransactionUiState.Error("Không thể tải danh sách giao dịch")
            }
        }
    }

    fun addTransaction(type: String, categoryId: Int, amount: Double, note: String) {
        viewModelScope.launch {
            db.transactionDao().insert(
                Transaction(
                    userId = userId, categoryId = categoryId, amount = amount,
                    type = type, note = note, date = System.currentTimeMillis()
                )
            )
            load()
        }
    }

    fun updateTransaction(id: Int, type: String, categoryId: Int, amount: Double, note: String) {
        viewModelScope.launch {
            val old = db.transactionDao().getById(id) ?: return@launch
            db.transactionDao().update(old.copy(type = type, categoryId = categoryId, amount = amount, note = note))
            load()
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            db.transactionDao().getById(id)?.let { db.transactionDao().delete(it) }
            load()
        }
    }
}