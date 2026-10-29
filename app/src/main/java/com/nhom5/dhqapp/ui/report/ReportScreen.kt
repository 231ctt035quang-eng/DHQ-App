package com.nhom5.dhqapp.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private fun fmt(v: Double): String = NumberFormat.getNumberInstance(Locale("vi","VN")).format(v.toLong()) + " đ"
private val palette = listOf(Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFFF44336), Color(0xFF00BCD4))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(userId: Int, db: AppDatabase) {
    val viewModel: ReportViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReportViewModel(userId, db) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var period by remember { mutableStateOf("MONTH") }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Báo cáo thống kê") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Bộ lọc kỳ
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = period == "WEEK", onClick = { period = "WEEK"; viewModel.load("WEEK") }, label = { Text("Tuần") })
                FilterChip(selected = period == "MONTH", onClick = { period = "MONTH"; viewModel.load("MONTH") }, label = { Text("Tháng") })
                FilterChip(selected = period == "ALL", onClick = { period = "ALL"; viewModel.load("ALL") }, label = { Text("Tất cả") })
            }
            Spacer(Modifier.height(16.dp))
            when (val state = uiState) {
                is ReportUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is ReportUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message); Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.load(period) }) { Text("Thử lại") }
                    }
                }
                is ReportUiState.Success -> {
                    if (state.breakdown.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Chưa có dữ liệu thống kê cho kỳ này", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Tổng thu/chi
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Text("Tổng thu", fontSize = 12.sp); Text(fmt(state.totalIncome), color = IncomeGreen, fontWeight = FontWeight.Bold) } }
                                Card(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Text("Tổng chi", fontSize = 12.sp); Text(fmt(state.totalExpense), color = ExpenseRed, fontWeight = FontWeight.Bold) } }
                            }
                            state.topCategory?.let { Text("Danh mục chi nhiều nhất: $it", fontWeight = FontWeight.Medium) }
                            Text("Cơ cấu chi tiêu theo danh mục", fontWeight = FontWeight.Bold)
                            // Biểu đồ cột
                            state.breakdown.forEachIndexed { i, stat ->
                                Column {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(stat.name, fontSize = 13.sp)
                                        Text(fmt(stat.amount), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        Modifier
                                            .fillMaxWidth(stat.percent.coerceAtLeast(0.05f))
                                            .height(18.dp)
                                            .background(palette[i % palette.size], RoundedCornerShape(9.dp))
                                    )
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}