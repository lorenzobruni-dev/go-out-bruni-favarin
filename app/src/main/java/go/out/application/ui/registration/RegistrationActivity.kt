package go.out.application.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import go.out.application.FirebaseDBHelper
import go.out.application.ui.login.LoginActivity
import go.out.application.R
import go.out.application.User

class RegistrationActivity : AppCompatActivity() {

    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)

        registrationViewModel = RegistrationViewModel(this) // Istanzia il ViewModel

        val regName = findViewById<EditText>(R.id.et_user_name)
        val regEmail = findViewById<EditText>(R.id.et_email)
        val regPass = findViewById<EditText>(R.id.et_password)
        val confPass = findViewById<EditText>(R.id.et_confirm_pwd)

        val btnReg = findViewById<Button>(R.id.btn_register)
        btnReg.setOnClickListener {

            val nome = regName.text.toString()
            val email = regEmail.text.toString()
            val password = regPass.text.toString()
            val confirmPass = confPass.text.toString()

            registrationViewModel.registerUser(nome, email, password, confirmPass) // Delega la registrazione al ViewModel
        }
    }
}