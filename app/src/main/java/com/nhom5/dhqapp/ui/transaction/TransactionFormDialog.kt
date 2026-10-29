package com.nhom5.dhqapp.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nhom5.dhqapp.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormDialog(
    categories: List<Category>,
    editing: TransactionListItem? = null,   // null = thêm mới
    onDismiss: () -> Unit,
    onSave: (type: String, categoryId: Int, amount: Double, note: String) -> Unit,
    onDelete: (() -> Unit)? = null           // chỉ có khi sửa
) {
    var type by remember { mutableStateOf(editing?.type ?: "CHI") }
    var selectedCategoryId by remember { mutableStateOf(editing?.let { e -> categories.firstOrNull { it.name == e.categoryName }?.id } ?: -1) }
    var amountText by remember { mutableStateOf(editing?.let { it.amount.toLong().toString() } ?: "") }
    var note by remember { mutableStateOf(editing?.note ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val catsByType = categories.filter { it.type == type }
    val selectedCatName = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Chọn danh mục"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editing == null) "Thêm giao dịch" else "Sửa giao dịch") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Chọn Thu / Chi
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = type == "THU", onClick = { type = "THU"; selectedCategoryId = -1 }, label = { Text("Thu") })
                    FilterChip(selected = type == "CHI", onClick = { type = "CHI"; selectedCategoryId = -1 }, label = { Text("Chi") })
                }
                // Chọn danh mục (dropdown)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedCatName, onValueChange = {},
                        readOnly = true, label = { Text("Danh mục") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        catsByType.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat.name) }, onClick = { selectedCategoryId = cat.id; expanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = amountText, onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Số tiền (VND)") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("Ghi chú (không bắt buộc)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                when {
                    selectedCategoryId == -1 -> error = "Vui lòng chọn danh mục"
                    amount <= 0 -> error = "Số tiền phải lớn hơn 0"
                    else -> onSave(type, selectedCategoryId, amount, note.trim())
                }
            }) { Text("Lưu") }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
                }
                TextButton(onClick = onDismiss) { Text("Hủy") }
            }
        }
    )
}
