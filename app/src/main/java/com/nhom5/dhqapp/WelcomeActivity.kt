package com.nhom5.dhqapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.data.DatabaseSeeder
import com.nhom5.dhqapp.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Chèn danh mục mặc định nếu chưa có
        lifecycleScope.launch {
            DatabaseSeeder.seedCategoriesIfEmpty(AppDatabase.getDatabase(this@WelcomeActivity))
        }

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
