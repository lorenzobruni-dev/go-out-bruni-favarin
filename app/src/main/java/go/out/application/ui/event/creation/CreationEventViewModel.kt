package go.out.application.ui.event.creation

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import go.out.application.FirebaseDBHelper

class CreationEventViewModel : ViewModel() {
    var auth = FirebaseDBHelper.auth
    val currentUserId = FirebaseAuth.getInstance().currentUser
    val creatoreId = currentUserId?.uid
    val eventsId = auth.currentUser?.uid

    lateinit var newEvent: Event

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEvent(context: Context, nome: String, data: String, ora: String) {
        newEvent = Event()
        newEvent.set_event(
            Event(
                eventsId,
                creatoreId,
                nome,
                data,
                ora,
                null,
                null
            )
        )

        if (auth.currentUser != null){
            FirebaseDBHelper.addEvent(newEvent) { success ->
                if (success) {
                    FirebaseDBHelper.addEventToCurrentUser(newEvent){ success ->
                        if(!success) Toast.makeText(context , "Errore , evento gia' inserito " , Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(context , "Inserito evento di nome ${newEvent.nome} con successo " , Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context , "Errore inserimento evento con id ${newEvent.id}" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}