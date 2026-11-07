package com.nhom5.dhqapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var db: AppDatabase

    // Tạo mật khẩu ngẫu nhiên 6 ký tự (chữ + số, bỏ 0/O/1/I dễ nhầm)
    private fun randomPassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        binding.btnReset.setOnClickListener {
            val input = binding.edtInput.text?.toString()?.trim().orEmpty()
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email hoặc Số điện thoại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                // Tìm theo email HOẶC số điện thoại
                val user = db.userDao().findByEmail(input) ?: db.userDao().findByPhone(input)
                if (user == null) {
                    Toast.makeText(this@ForgotPasswordActivity, "Không tìm thấy tài khoản với thông tin này", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val newPass = randomPassword()
                db.userDao().update(user.copy(password = newPass))
                // Hiện mật khẩu mới cho người dùng (bản thật sẽ gửi qua Email/SMS)
                AlertDialog.Builder(this@ForgotPasswordActivity)
                    .setTitle("Cấp lại mật khẩu thành công")
                    .setMessage("Mật khẩu mới của bạn là:\n\n$newPass\n\nHãy dùng mật khẩu này để đăng nhập, sau đó vào Cài đặt → Đổi mật khẩu để đặt lại mật khẩu mới.")
                    .setPositiveButton("Đến trang Đăng nhập") { _, _ ->
                        startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}
