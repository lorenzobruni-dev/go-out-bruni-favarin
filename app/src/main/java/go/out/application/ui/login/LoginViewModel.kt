package go.out.application.ui.login

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import go.out.application.MainActivity

class LoginActivityViewModel(private val context: LoginActivity) {

    private val auth: FirebaseAuth = Firebase.auth

    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        context.finish()
                    } else {
                        val builder = AlertDialog.Builder(context)
                        with(builder) {
                            setTitle("Login per ${email} fallito")
                            setMessage(task.exception?.message)
                            setPositiveButton("OK", null)
                            show()
                        }
                    }
                }
        } else {
            Toast.makeText(context, "Inserisci email e password", Toast.LENGTH_SHORT).show()
        }
    }
}