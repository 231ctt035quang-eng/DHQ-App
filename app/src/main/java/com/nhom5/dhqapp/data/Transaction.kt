package com.nhom5.dhqapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val type: String,      // "THU" hoặc "CHI"
    val note: String,
    val date: Long         // timestamp (System.currentTimeMillis())
)