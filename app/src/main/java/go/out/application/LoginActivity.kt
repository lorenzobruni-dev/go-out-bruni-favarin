package go.out.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import go.out.application.ui.RegistrationActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val btn = findViewById<TextView> (R.id.tv_new_account)
        val btnLogIn = findViewById<Button>(R.id.btn_logIn)
        val etEmail = findViewById<EditText>(R.id.et_emailLogin)
        val etPwd = findViewById<EditText>(R.id.et_pwd)

        btnLogIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPwd.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Inserisci email e password", Toast.LENGTH_SHORT).show()
            }
        }

        btn.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity (intent)
        }
    }

    fun loginUser(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val builder = AlertDialog.Builder(this)
                    with(builder)
                    {
                        setTitle("Login per ${email} fallito")
                        setMessage(task.exception?.message)
                        setPositiveButton("OK", null)
                        show()
                    }

                }

            }
    }
}