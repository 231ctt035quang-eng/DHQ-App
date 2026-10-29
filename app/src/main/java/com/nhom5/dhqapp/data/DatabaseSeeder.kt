package com.nhom5.dhqapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {
    // Nếu bảng categories rỗng → chèn 12 danh mục mặc định
    suspend fun seedCategoriesIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        if (db.categoryDao().getAll().isEmpty()) {
            val thu = listOf("Lương", "Thưởng", "Đầu tư", "Thu khác")
            val chi = listOf("Ăn uống", "Di chuyển", "Mua sắm", "Nhà cửa", "Giải trí", "Sức khỏe", "Học tập", "Chi khác")
            thu.forEach { db.categoryDao().insert(Category(name = it, type = "THU")) }
            chi.forEach { db.categoryDao().insert(Category(name = it, type = "CHI")) }
        }
    }
}