package go.out.application.ui.registration

import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import go.out.application.FirebaseDBHelper
import go.out.application.User
import go.out.application.ui.login.LoginActivity

class RegistrationViewModel(private val context: RegistrationActivity) {

    val auth = Firebase.auth

    fun registerUser(nome: String, email: String, password: String, confirmPassword: String) {
        if (nome.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(context, "Le password non coincidono", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    val newUser = User(
                        id = auth.currentUser?.uid,
                        nome = nome,
                        password = password,
                        email = email,
                        contatti = null,
                        eventi = null
                    )
                    FirebaseDBHelper.dbUsers.child(newUser.id!!).setValue(newUser)
                    Toast.makeText(context, "Utente creato con successo!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    context.finish()
                } else {
                    Toast.makeText(context, "Errore durante la registrazione", Toast.LENGTH_SHORT).show()
                }
            }
    }
}