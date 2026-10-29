package com.nhom5.dhqapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, Category::class, Transaction::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dhq_app.db"
                )
                    .fallbackToDestructiveMigration()   // đổi cấu trúc DB thì xóa dữ liệu cũ (tránh lỗi migration)
                    .addCallback(seedCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Tự động thêm danh mục mặc định lần đầu mở app
        private val seedCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val thu = listOf("Lương", "Thưởng", "Đầu tư", "Thu khác")
                val chi = listOf("Ăn uống", "Di chuyển", "Mua sắm", "Nhà cửa", "Giải trí", "Sức khỏe", "Học tập", "Chi khác")
                thu.forEach { db.execSQL("INSERT INTO categories (name, type) VALUES ('$it', 'THU')") }
                chi.forEach { db.execSQL("INSERT INTO categories (name, type) VALUES ('$it', 'CHI')") }
            }
        }
    }
}