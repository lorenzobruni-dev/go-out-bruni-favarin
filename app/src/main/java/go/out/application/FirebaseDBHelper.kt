package go.out.application

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import go.out.application.ui.event.creation.Confirmation
import go.out.application.ui.event.creation.Event

class FirebaseDBHelper {

    companion object {

        var auth = FirebaseAuth.getInstance()
        val dbUsers = FirebaseDatabase
            .getInstance("https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users")

        val dbEvents = FirebaseDatabase
            .getInstance("https://progetto-pdm-goout-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Events")

        fun readUser(userEventListener: ChildEventListener) {
            dbUsers.addChildEventListener(userEventListener)
        }

        fun removeUser(id: String) {
            dbUsers.child(id).removeValue()
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

        fun getUserEvents(userId: String, callback: (List<Event>, List<String>) -> Unit) {
            val confirmedList = mutableListOf<Event>()
            val eventNamesList = mutableListOf<String>()

            // Ottieni il riferimento al nodo dell'utente nel database
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            // Aggiungi un listener per leggere i dati dell'utente dal database Firebase
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    // Verifica se l'utente esiste e ha degli eventi nel campo "eventi"
                    if (user != null && user.eventi?.isNotEmpty()==true) {
                        // Itera attraverso gli ID degli eventi nell'elenco dell'utente
                        user.eventi!!.forEach { eventId ->
                            // Ottieni il riferimento all'evento nel database
                            val eventRef = FirebaseDatabase.getInstance().getReference("Events").child(eventId)
                            // Leggi i dati dell'evento dal database Firebase
                            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(eventSnapshot: DataSnapshot) {
                                    val event = eventSnapshot.getValue(Event::class.java)
                                    // Verifica se l'evento esiste e aggiungilo alla lista degli eventi
                                    if (event != null) {
                                        confirmedList.add(event)
                                        event.nome?.let { eventNamesList.add(it) }
                                    }
                                    // Se abbiamo aggiunto tutti gli eventi all'elenco, chiamiamo il callback
                                    if (confirmedList.size == user.eventi!!.size) {
                                        callback(confirmedList, eventNamesList)
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    // Gestisci eventuali errori di lettura dal database
                                    Log.e("FirebaseDBHelper", "Errore durante la lettura dell'evento: ${error.message}")
                                }
                            })
                        }
                    } else {
                        // Chiamiamo comunque il callback se l'utente non ha eventi nel campo "eventi"
                        callback(confirmedList, eventNamesList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Gestisci eventuali errori di lettura dal database
                    Log.e("FirebaseDBHelper", "Errore durante la lettura dell'utente: ${error.message}")
                }
            })
        }

        fun removeParticipantFromEvent(eventId: String, userId: String, onComplete: (Boolean) -> Unit) {
            // Implementazione necessaria
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
                                val confirmedList = it.confermati ?: mutableListOf()


                                val currentUserConfirmation = Confirmation(currentUser.nome, currentUser.email)
                                confirmedList.add(currentUserConfirmation)


                                selectedEvent.confermati = confirmedList
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
            stringBuilder.append("Luogo: ${event.luogo}\n")

            stringBuilder.append("\nConfermati:\n")
            event.confermati?.let { confermatiList ->
                for (confermato in confermatiList) {
                    stringBuilder.append("Nome: ${confermato.nome} (${confermato.email}\n)")
                }
            }

            return stringBuilder.toString()
        }


    }
}