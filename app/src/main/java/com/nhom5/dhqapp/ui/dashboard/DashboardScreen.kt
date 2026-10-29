package com.nhom5.dhqapp.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import java.util.*

private fun fmt(v: Double): String = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(v.toLong()) + " đ"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userId: Int,
    db: AppDatabase
) {
    val viewModel: DashboardViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(userId, db) as T
            }
        }
    )
    // Đọc StateFlow theo Lab 6 — xoay máy không mất state
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Tổng quan tài chính") })
    }) { padding ->
        when (val state = uiState) {
            is DashboardUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp)); Text("Đang tải...")
            }}

            is DashboardUiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.message); Spacer(Modifier.height(12.dp))
                Button(onClick = { viewModel.load() }) { Text("Thử lại") }
            }}

            is DashboardUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Thẻ số dư
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Số dư hiện tại", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
                            Text(fmt(state.balance), color = MaterialTheme.colorScheme.onPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column { Text("Tổng thu", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp); Text("+ ${fmt(state.totalIncome)}", color = IncomeGreen, fontWeight = FontWeight.Bold) }
                                Column { Text("Tổng chi", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp); Text("- ${fmt(state.totalExpense)}", color = ExpenseRed, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Giao dịch gần đây", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.load() }) { Icon(Icons.Default.Refresh, contentDescription = "Tải lại") }
                    }
                }
                if (state.recent.isEmpty()) {
                    item { Text("Chưa có giao dịch nào. Hãy thêm giao dịch đầu tiên!", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(state.recent, key = { it.transaction.id }) { item ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(item.categoryName, fontWeight = FontWeight.Medium)
                                    Text(item.transaction.note.ifEmpty { "Không có ghi chú" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    (if (item.transaction.type == "THU") "+ " else "- ") + fmt(item.transaction.amount),
                                    color = if (item.transaction.type == "THU") IncomeGreen else ExpenseRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
