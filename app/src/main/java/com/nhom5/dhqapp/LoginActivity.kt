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
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager

    private lateinit var firebaseAuth: FirebaseAuth

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val fbUser = firebaseAuth.currentUser
                    val email = fbUser?.email ?: ""
                    val name = fbUser?.displayName ?: "Người dùng Google"
                    lifecycleScope.launch {
                        // Tìm hoặc tạo user trong Room
                        var user = db.userDao().findByEmail(email)
                        if (user == null) {
                            db.userDao().insert(com.nhom5.dhqapp.data.User(name = name, email = email, phone = "", password = "google-login"))
                            user = db.userDao().findByEmail(email)
                        }
                        user?.let {
                            session.setLoggedInUserId(it.id)
                            Toast.makeText(this@LoginActivity, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
                            startActivity(android.content.Intent(this@LoginActivity, MainActivity::class.java))
                            finishAffinity()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Lỗi: ${e.statusCode}", e)
            Toast.makeText(this, "Đăng nhập Google thất bại: ${e.statusCode}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)
        firebaseAuth = FirebaseAuth.getInstance()
        session = SessionManager(this)

        binding.ivBack.setOnClickListener {
            // Xóa toàn bộ màn Đăng nhập/Đăng ký, quay thẳng về màn chào
            val intent = Intent(this, WelcomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        binding.btnGoogle.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // tự sinh từ google-services.json
                .requestEmail()
                .build()
            googleSignInLauncher.launch(GoogleSignIn.getClient(this, gso).signInIntent)
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
