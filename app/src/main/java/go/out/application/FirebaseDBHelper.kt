package go.out.application

import android.content.ContentValues.TAG
import android.content.Context
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import go.out.application.ui.event.creation.Event

class FirebaseDBHelper {

    companion object {

        private const val FIREBASE_INSTANCE =
            "https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/"

        var auth = FirebaseAuth.getInstance()
        val dbUsers = FirebaseDatabase
            .getInstance(FIREBASE_INSTANCE)
            .getReference("Users")

        val dbEvents = FirebaseDatabase
            .getInstance(FIREBASE_INSTANCE)
            .getReference("Events")


        fun searchUserByEmail(emailAmico: String, callback: (String?, String?) -> Unit) {
            val utenteCorrente = FirebaseAuth.getInstance().currentUser
            utenteCorrente?.let { user ->
                dbUsers.orderByChild("email").equalTo(emailAmico)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val amicoTrovato =
                                snapshot.children.firstOrNull()?.getValue(User::class.java)
                            if (amicoTrovato != null && amicoTrovato.id != user.uid) {
                                val idAmico = amicoTrovato.id
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
            val contattiReference = dbUsers.child(userId).child("contatti")

            contattiReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val contattiList = mutableListOf<User>()

                    for (contattoSnapshot in snapshot.children) {
                        val contattoId = contattoSnapshot.getValue(String::class.java)
                        if (contattoId != null) {
                            getUtenteDaID(contattoId) { utente ->
                                contattiList.add(utente!!)

                                if (contattiList.size == snapshot.childrenCount.toInt()) {
                                    onComplete(contattiList)
                                }
                            }
                        }
                    }
                    if (snapshot.childrenCount == 0L) {
                        onComplete(contattiList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                }

            })
        }

        fun getUtenteDaID(userId: String, onComplete: (User?) -> Unit) {
            val userReference = dbUsers.child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val utente = snapshot.getValue(User::class.java)
                    onComplete(utente)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(null)
                }
            })
        }

        fun getInvitedEvents(userId: String, callback: (List<Event>, List<String>) -> Unit) {
            var nomeUtente = ""
            getUtenteDaID(userId) { user ->
                if (user != null) {
                    nomeUtente = user.nome!!
                }
            }
            val eventsList = mutableListOf<Event>()
            val eventsIdsList = mutableListOf<String>()
            dbEvents.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Event::class.java)
                        if (event != null && event.partecipanti?.contains(nomeUtente) == true) {
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

        fun removeEventsFromUserWhenPartecipantFieldIsEmpty(
            userId: String,
            onComplete: (Boolean) -> Unit
        ) {
            val userReference = dbUsers.child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val eventsFieldsFromUser = snapshot.child("eventi").children

                    eventsFieldsFromUser.forEach { event ->
                        val eventReference = dbEvents.child(event.value.toString())
                        eventReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val singleEvent = snapshot.getValue(Event::class.java)
                                if (singleEvent != null && singleEvent.partecipanti == null) {
                                    userReference.child("eventi")
                                        .child(event.key!!).ref.removeValue()
                                    eventReference.ref.removeValue()
                                }
                                onComplete(true)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, error.toString())
                            }

                        })
                    }
                    onComplete(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)

                    Log.e(TAG, error.toString())
                }

            })
        }

        fun getUserEvents(userId: String, callback: (List<Event>, List<String>) -> Unit) {
            val confirmedList = mutableListOf<Event>()
            val eventNamesList = mutableListOf<String>()

            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    if (user != null && user.eventi?.isNotEmpty() == true) {
                        user.eventi!!.forEach { eventId ->
                            val eventRef = dbEvents.child(eventId)
                            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(eventSnapshot: DataSnapshot) {
                                    val event = eventSnapshot.getValue(Event::class.java)
                                    if (event != null) {
                                        confirmedList.add(event)
                                        event.nome?.let { eventNamesList.add(it) }
                                    }
                                    if (confirmedList.size == user.eventi!!.size) {
                                        callback(confirmedList, eventNamesList)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(
                                        "FirebaseDBHelper",
                                        "Errore durante la lettura dell'evento: ${error.message}"
                                    )
                                }
                            })
                        }
                    } else {
                        callback(confirmedList, eventNamesList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        "FirebaseDBHelper",
                        "Errore durante la lettura dell'utente: ${error.message}"
                    )
                }
            })
        }

        fun removeParticipantFromEvent(
            eventId: String,
            userId: String,
            onComplete: (Boolean) -> Unit
        ) {
            val eventReference = dbEvents.child(eventId)
            eventReference.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val event = currentData.getValue(Event::class.java)
                    if (event != null) {
                        val participants = event.partecipanti?.toMutableList()
                        if (participants != null) {
                            participants.remove(userId)
                            event.partecipanti = participants
                            currentData.value = event
                        }
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (committed && databaseError == null) {
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
            })
        }


        fun addEvent(event: Event, onComplete: (Boolean) -> Unit) {
            val eventReference = dbEvents.push()
            val eventID = eventReference.key
            event.id = eventID

            eventReference.setValue(event)
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        }

        fun getNomiContattiForCreationEvent(userId: String, callback: (List<String>) -> Unit) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    val friendsList: List<String> = user?.contatti ?: emptyList()
                    val friendNames =
                        mutableListOf<String>()

                    val friendCount = friendsList.size
                    var friendProcessed = 0

                    friendsList.forEach { friend ->
                        val usersReference =
                            FirebaseDatabase.getInstance().getReference("Users").child(friend)

                        usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(User::class.java)
                                val friendListName: String? = user?.nome

                                friendListName?.let { name ->
                                    friendNames.add(name)
                                }

                                friendProcessed++
                                if (friendProcessed == friendCount) {

                                    callback(friendNames)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, error.toString())
                            }
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.toString())
                }
            })
        }

        fun addEventToCurrentUser(
            selectedEvent: Event?,
            onComplete: (Boolean) -> Unit
        ) {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val currentUserRef = dbUsers.child(firebaseUser.uid)
                currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.getValue(User::class.java)
                        currentUser?.let {

                            val updateEvents = it.eventi?.toMutableList() ?: mutableListOf()
                            updateEvents.add(selectedEvent?.id ?: "")
                            currentUserRef.child("eventi").setValue(updateEvents)


                            selectedEvent?.let {
                                dbEvents.child(selectedEvent.id!!).setValue(selectedEvent)
                                onComplete(true)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onComplete(false)
                    }
                })
            }
        }

        fun buildEventDetailsMessage(event: Event): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Nome: ${event.nome}\n")
            stringBuilder.append("Data: ${event.data}\n")
            stringBuilder.append("Ora: ${event.ora}\n")

            stringBuilder.append("\nConfermati:\n")
            event.partecipanti?.forEachIndexed { index, item -> stringBuilder.append("${index + 1}) $item\n") }

            return stringBuilder.toString()
        }

        fun showAddFriendDialog(context: Context, onFriendAdded: (Boolean) -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Aggiungi amico")

            val inputEmail = EditText(context)
            inputEmail.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(inputEmail)

            builder.setPositiveButton("Ricerca") { dialog, _ ->
                val email = inputEmail.text.toString()
                searchUserByEmail(email) { friendID, message ->
                    if (friendID != null) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onFriendAdded(true)
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onFriendAdded(false)
                    }
                    dialog.dismiss()
                }
            }
            builder.setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
                onFriendAdded(false)
            }
            builder.show()
        }


    }
}
