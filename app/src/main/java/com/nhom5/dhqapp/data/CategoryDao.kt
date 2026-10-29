package com.nhom5.dhqapp.data

import androidx.room.*

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY type, name")
    suspend fun getAll(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Category?

    @Query("SELECT * FROM categories WHERE type = :type")
    suspend fun getByType(type: String): List<Category>

    @Insert suspend fun insert(category: Category)
    @Update suspend fun update(category: Category)
    @Delete suspend fun delete(category: Category)
}