package com.nhom5.dhqapp.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.ui.theme.ExpenseRed
import com.nhom5.dhqapp.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private fun fmt(v: Double): String = NumberFormat.getNumberInstance(Locale("vi","VN")).format(v.toLong()) + " đ"
private fun fmtDate(ts: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale("vi","VN")).format(Date(ts))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(userId: Int, db: AppDatabase) {
    val viewModel: TransactionViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TransactionViewModel(userId, db) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    // null = đang đóng dialog; != null = đang sửa (chứa item); editing=false = đang thêm mới
    var editingItem by remember { mutableStateOf<TransactionListItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Giao dịch") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                label = { Text("Tìm kiếm theo danh mục / ghi chú") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            when (val state = uiState) {
                is TransactionUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is TransactionUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message); Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.load() }) { Text("Thử lại") }
                    }
                }
                is TransactionUiState.Success -> {
                    val filtered = state.items.filter {
                        it.categoryName.contains(query, true) || it.note.contains(query, true)
                    }
                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(if (query.isEmpty()) "Chưa có giao dịch. Bấm + để thêm." else "Không tìm thấy kết quả", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filtered, key = { it.id }) { item ->
                                Surface(onClick = { editingItem = item }, modifier = Modifier.fillMaxWidth()) {
                                    Card(Modifier.fillMaxWidth()) {
                                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Column(Modifier.weight(1f)) {
                                                Text(item.categoryName, fontWeight = FontWeight.Medium)
                                                Text("${fmtDate(item.date)} · ${item.note.ifEmpty { "Không có ghi chú" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text((if (item.type == "THU") "+ " else "- ") + fmt(item.amount), color = if (item.type == "THU") IncomeGreen else ExpenseRed, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog thêm mới
    if (showAddDialog) {
        TransactionFormDialog(
            categories = categories,
            onDismiss = { showAddDialog = false },
            onSave = { type, catId, amount, note ->
                viewModel.addTransaction(type, catId, amount, note)
                showAddDialog = false
            }
        )
    }
    // Dialog sửa / xóa
    editingItem?.let { item ->
        TransactionFormDialog(
            categories = categories,
            editing = item,
            onDismiss = { editingItem = null },
            onSave = { type, catId, amount, note ->
                viewModel.updateTransaction(item.id, type, catId, amount, note)
                editingItem = null
            },
            onDelete = {
                viewModel.deleteTransaction(item.id)
                editingItem = null
            }
        )
    }
}
