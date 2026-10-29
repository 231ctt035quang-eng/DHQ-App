package com.nhom5.dhqapp.data

import androidx.room.*

@Dao
interface TransactionDao {
    @Insert suspend fun insert(transaction: Transaction)
    @Update suspend fun update(transaction: Transaction)
    @Delete suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    suspend fun getByUserId(userId: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Transaction?
}