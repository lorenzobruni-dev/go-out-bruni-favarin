package go.out.application.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import go.out.application.R
import go.out.application.ui.registration.RegistrationActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = LoginActivityViewModel(this)

        val btn = findViewById<TextView>(R.id.tv_new_account)
        val btnLogIn = findViewById<Button>(R.id.btn_logIn)
        val etEmail = findViewById<EditText>(R.id.et_emailLogin)
        val etPwd = findViewById<EditText>(R.id.et_pwd)

        btnLogIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPwd.text.toString()
            loginViewModel.loginUser(email, password)
        }

        btn.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}
