package com.nhom5.dhqapp
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nhom5.dhqapp.databinding.ActivityRegisterBinding
import android.util.Patterns
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.nhom5.dhqapp.data.AppDatabase
import com.nhom5.dhqapp.data.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text?.toString()?.trim().orEmpty()
            val email = binding.edtEmail.text?.toString()?.trim().orEmpty()
            val phone = binding.edtPhone.text?.toString()?.trim().orEmpty()
            val pass = binding.edtPassword.text?.toString().orEmpty()
            val confirm = binding.edtConfirm.text?.toString().orEmpty()
            when {
                name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() ->
                    Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                pass.length < 6 ->
                    Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show()
                pass != confirm ->
                    Toast.makeText(this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                !binding.cbTerms.isChecked ->
                    Toast.makeText(this, "Vui lòng đồng ý Điều khoản & Chính sách", Toast.LENGTH_SHORT).show()
                else -> {
                    lifecycleScope.launch {
                        val db = AppDatabase.getDatabase(this@RegisterActivity)
                        val result = db.userDao().insert(
                            User(name = name, email = email, phone = phone, password = pass)
                        )
                        if (result == -1L) {
                            Toast.makeText(this@RegisterActivity, "Email đã được sử dụng", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Đăng ký thành công! Hãy đăng nhập", Toast.LENGTH_SHORT).show()
                            finish() // quay lại màn đăng nhập
                        }
                    }
                }
            }
        }
    }
}