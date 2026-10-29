package com.nhom5.dhqapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.data.SessionManager
import com.nhom5.dhqapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)
        session = SessionManager(this)

        binding.ivBack.setOnClickListener { finish() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgot.setOnClickListener {
            Toast.makeText(this, "Liên hệ hỗ trợ để khôi phục mật khẩu", Toast.LENGTH_SHORT).show()
        }
        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Tính năng đang phát triển — xem hướng dẫn để cấu hình google-services.json", Toast.LENGTH_LONG).show()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.edtPassword.text?.toString().orEmpty()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val user = db.userDao().findByEmail(email)
                when {
                    user == null -> Toast.makeText(this@LoginActivity, "Tài khoản chưa được đăng ký", Toast.LENGTH_SHORT).show()
                    user.password != pass -> Toast.makeText(this@LoginActivity, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                    else -> {
                        session.setLoggedInUserId(user.id)
                        Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finishAffinity() // đóng toàn bộ màn auth
                    }
                }
            }
        }
    }
}
