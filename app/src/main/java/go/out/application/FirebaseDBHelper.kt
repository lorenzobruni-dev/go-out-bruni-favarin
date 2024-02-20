package go.out.application

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.Properties

class FirebaseDBHelper {

    companion object {
       // private val auth = FirebaseAuth.getInstance()
        //private val properties = Properties().apply {
          //  javaClass.getResourceAsStream("../../../config/config.properties").use { inputStream ->
            //    load(inputStream)
            //}
        //}

        //private val URL = properties.getProperty("DATABASE_URL")
        //private val firebaseDatabase = FirebaseDatabase.getInstance(URL)
        //var dbUsers: DatabaseReference
        //var dbEvents: DatabaseReference

        //init {
          //  dbUsers = firebaseDatabase.getReference("Users")
           // dbEvents = firebaseDatabase.getReference("Events")
        //}

        var auth = FirebaseAuth.getInstance()
        val dbUsers = FirebaseDatabase
            .getInstance("https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        val dbEvents = FirebaseDatabase
            .getInstance("https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Events")

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

        fun searchUserByEmail(emailAmico: String, callback: (String?, String?) -> Unit) {

            val utenteCorrente = FirebaseAuth.getInstance().currentUser

            utenteCorrente?.let { user ->
                dbUsers.orderByChild("email").equalTo(emailAmico)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val amicoTrovato = snapshot.children.firstOrNull()?.getValue(User::class.java)
                            if (amicoTrovato != null && amicoTrovato.id != user.uid) {
                                val idAmico = amicoTrovato.id
                                // Aggiungi amico ai contatti dell'utente corrente
                                aggiornaContattiUtente(user.uid, idAmico!!) { error ->
                                    if (error == null) {
                                        callback(idAmico, "Amico aggiunto con successo!")
                                    } else {
                                        callback(null, "Errore durante l'aggiunta dell'amico")
                                    }
                                }
                            } else {
                                callback(null, "Utente non trovato o stesso utente")
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            callback(null, "Errore durante la ricerca")
                        }
                    })
            } ?: run {
                callback(null, "Errore in fase di autenticazione dell'utente")
            }
        }

        fun aggiornaContattiUtente(userId: String, friendId: String, callback: (String?) -> Unit) {
            val currentUserRef = dbUsers.child(userId)

            currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUser = snapshot.getValue(User::class.java)
                    currentUser?.let {
                        val updatedContacts = it.contatti ?: mutableListOf()
                        if (!updatedContacts.contains(friendId)) {
                            updatedContacts.add(friendId)
                            currentUserRef.child("contatti").setValue(updatedContacts) { error, _ ->
                                if (error == null) {
                                    callback(null)
                                } else {
                                    callback(error.message)
                                }
                            }
                        } else {
                            callback("L'amico è già presente nella tua lista")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(error.message)
                }
            })
        }


        fun getNomiContatti(userId: String, onComplete: (List<User>) -> Unit) {
            // Implementazione necessaria
        }

        fun getUtenteDaID(userId: String, onComplete: (User?) -> Unit) {
            // Implementazione necessaria
        }

        fun getInvitedEvents(userId: String, callback: (List<Event>, List<String>) -> Unit) {
            val eventsList = mutableListOf<Event>()
            val eventsIdsList = mutableListOf<String>()

            dbEvents.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Event::class.java)
                        if (event != null && event.partecipanti?.contains(userId) == true) {
                            eventsList.add(event)
                            eventsIdsList.add(event.id!!)
                        }
                    }
                    callback(eventsList, eventsIdsList)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        fun getEventFromDatabase(eventId: String, callback: (Event?) -> Unit) {
            val eventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId)

            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val event = snapshot.getValue(Event::class.java)
                    callback(event)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to retrieve event from database: ${error.message}")
                    callback(null)
                }
            })
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

        fun addEventToCurrentUser(selectedEvent: Event?, any: Any) {

        }


    }
}