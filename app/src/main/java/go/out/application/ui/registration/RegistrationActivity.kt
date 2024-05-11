package go.out.application.ui.registration

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import go.out.application.R

class RegistrationActivity : AppCompatActivity() {

    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

            registrationViewModel.registerUser(nome, email, password, confirmPass)
        }
    }
}