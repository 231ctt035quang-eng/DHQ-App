package com.nhom5.dhqapp.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhom5.dhqapp.data.AppDatabase
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userId: Int,
    db: AppDatabase,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    // Lấy thông tin user (đơn giản: runBlocking vì chỉ 1 lần)
    val user = remember { runBlocking { db.userDao().findById(userId) } }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Cài đặt & Hồ sơ") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thẻ hồ sơ
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        user?.name?.firstOrNull()?.uppercase()?.toString() ?: "U",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(user?.name ?: "Người dùng", fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            // Dark mode switch (Lab 4)
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Giao diện tối (Dark mode)")
                Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
            }
            HorizontalDivider()
            // Các mục khác (placeholder, có thể mở rộng)
            listOf("Đổi mật khẩu", "Khóa ứng dụng (PIN)", "Xuất dữ liệu CSV", "Sao lưu / Khôi phục").forEach { label ->
                ListItem(
                    headlineContent = { Text(label) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Nút Đăng xuất ở cuối danh sách cuộn
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("Đăng xuất", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
