package go.out.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseDBHelper {

    companion object {
        var auth = FirebaseAuth.getInstance()
        val dbUsers = FirebaseDatabase
            .getInstance("https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        val dbEvents = FirebaseDatabase
            .getInstance("https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Events")
    }
}