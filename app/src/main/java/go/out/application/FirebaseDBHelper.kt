package go.out.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.events.Event
import java.util.Properties

class FirebaseDBHelper {
    companion object {
        private val auth = FirebaseAuth.getInstance()
        private val properties = Properties().apply {
            javaClass.getResourceAsStream("../../../config/config.properties").use { inputStream ->
                load(inputStream)
            }
        }

        private val URL = properties.getProperty("DATABASE_URL")
        private val firebaseDatabase = FirebaseDatabase.getInstance(URL)
        var dbUsers: DatabaseReference
        var dbEvents: DatabaseReference

        init {
            dbUsers = firebaseDatabase.getReference("Users")
            dbEvents = firebaseDatabase.getReference("Events")
        }

        fun getDbUsersReference(){
            dbUsers
        }
        fun getDbEventsReference(){
            dbEvents
        }

        fun readUser(userEventListener: ChildEventListener) {
            dbUsers.addChildEventListener(userEventListener)
        }

        fun removeUser(id: String) {
            dbUsers.child(id).removeValue()
        }

        fun getEventById(eventId: String, onComplete: (Any?) -> Unit) {
            // Implementazione necessaria
        }

        fun searchUserByEmail(email: String, callback: (String?) -> Unit) {
            // Implementazione necessaria
        }

        fun getNomiContatti(userId: String, onComplete: (List<User>) -> Unit) {
            // Implementazione necessaria
        }

        fun getUtenteDaID(userId: String, onComplete: (User?) -> Unit) {
            // Implementazione necessaria
        }

        fun getInvitedEvents(userId: String, callback: (List<Any>, List<String>) -> Unit) {
            // Implementazione necessaria
        }

        fun addEvent(event: Any, onComplete: (Boolean) -> Unit) {
            // Implementazione necessaria
        }

        fun removeParticipantFromEvent(eventId: String, userId: String, onComplete: (Boolean) -> Unit) {
            // Implementazione necessaria
        }

        fun getUserNamesFromIds(userIds: List<String>, onComplete: (List<String>) -> Unit) {
            // Implementazione necessaria
        }
    }
}