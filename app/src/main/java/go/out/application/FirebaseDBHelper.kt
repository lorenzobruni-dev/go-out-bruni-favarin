package go.out.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDBHelper {
    companion object {
        val auth = FirebaseAuth.getInstance()
        lateinit var dbUsers: DatabaseReference
            private set
        lateinit var dbEvents: DatabaseReference
            private set

        init {
            val dbUsers = FirebaseDatabase
                .getInstance()
                .getReference("Users")
            val dbEvents = FirebaseDatabase
                .getInstance()
                .getReference("Events")
        }
    }



}