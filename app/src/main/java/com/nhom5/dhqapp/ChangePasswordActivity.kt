package com.nhom5.dhqapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.data.SessionManager
import com.nhom5.dhqapp.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)
        session = SessionManager(this)

        binding.ivBack.setOnClickListener { finish() }

        binding.btnSave.setOnClickListener {
            val old = binding.edtOld.text?.toString().orEmpty()
            val new = binding.edtNew.text?.toString().orEmpty()
            val confirm = binding.edtConfirm.text?.toString().orEmpty()
            when {
                old.isEmpty() || new.isEmpty() -> Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show()
                new.length < 6 -> Toast.makeText(this, "Mật khẩu mới tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show()
                new != confirm -> Toast.makeText(this, "Xác nhận mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                else -> lifecycleScope.launch {
                    val userId = session.loggedInUserId.first()
                    val user = db.userDao().findById(userId)
                    when {
                        user == null -> Toast.makeText(this@ChangePasswordActivity, "Lỗi phiên đăng nhập", Toast.LENGTH_SHORT).show()
                        user.password != old -> Toast.makeText(this@ChangePasswordActivity, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                        else -> {
                            db.userDao().update(user.copy(password = new))
                            Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }
}
