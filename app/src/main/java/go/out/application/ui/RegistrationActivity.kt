package go.out.application.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import go.out.application.FirebaseDBHelper
import go.out.application.LoginActivity
import go.out.application.R
import go.out.application.User

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

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


            if (nome.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPass) {
                Toast.makeText(this, "Le password non coincidono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createUser(nome, email, password)
        }

        val ret = findViewById<ImageView>(R.id.btn_back)
        ret.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUser(nome: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val newUser =
                        User(id = auth.currentUser?.uid, nome = nome, password = password, email = email, contatti = null, eventi = null)
                    FirebaseDBHelper.dbUsers.child(newUser.id!!).setValue(newUser)
                    Toast.makeText(this, "Utente creato con successo!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Errore durante la registrazione", Toast.LENGTH_SHORT).show()
                }
            }
    }
}