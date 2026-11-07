package com.nhom5.dhqapp.ui.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhom5.dhqapp.ChangePasswordActivity
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
    val context = LocalContext.current
    val user = remember { runBlocking { db.userDao().findById(userId) } }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Cài đặt & Hồ sơ") }) }) { padding ->
        // ✅ Một Column duy nhất, cuộn được — mọi thứ (kể cả nút Đăng xuất) cuộn chung
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thẻ hồ sơ
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        user?.name?.firstOrNull()?.uppercase()?.toString() ?: "U",
                        fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(user?.name ?: "Người dùng", fontWeight = FontWeight.Bold)
                        Text(
                            user?.email ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Dark mode switch
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Giao diện tối (Dark mode)")
                Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
            }

            HorizontalDivider()

            // Đổi mật khẩu
            Surface(
                onClick = { context.startActivity(Intent(context, ChangePasswordActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Đổi mật khẩu") },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                )
            }

            // Các mục khác
            listOf("Khóa ứng dụng (PIN)", "Xuất dữ liệu CSV", "Sao lưu / Khôi phục").forEach { label ->
                ListItem(
                    headlineContent = { Text(label) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ✅ Nút Đăng xuất nằm trong luồng cuộn, cách dòng cuối rõ rệt — KHÔNG bị đè
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("Đăng xuất", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp)) // đệm đáy, tránh nút dính sát mép màn hình
        }
    }
}
