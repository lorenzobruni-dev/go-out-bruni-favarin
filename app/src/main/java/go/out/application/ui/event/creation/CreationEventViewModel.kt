package go.out.application.ui.event.creation

import PlaceInfo
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import go.out.application.FirebaseDBHelper
import go.out.application.User

class CreationEventViewModel : ViewModel() {
    var auth = FirebaseDBHelper.auth
    val currentUserId = FirebaseAuth.getInstance().currentUser!!
    val creatoreId = currentUserId.uid
    val eventsId = auth.currentUser?.uid
    lateinit var newEvent: Event
    private val data: MutableList<User> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEvent(
        context: Context,
        nome: String,
        data: String,
        ora: String,
        friends: List<String>,
        placeInfo: PlaceInfo
    ) {
        newEvent = Event()
        newEvent.set_event(
            Event(
                eventsId,
                creatoreId,
                nome,
                data,
                ora,
                friends,
                placeInfo,
                null
            )
        )

        if (auth.currentUser != null) {
            FirebaseDBHelper.addEvent(newEvent) { success ->
                if (success) {
                    FirebaseDBHelper.addEventToCurrentUser(newEvent) { success ->
                        if (!success) Toast.makeText(
                            context,
                            "Errore , evento gia' inserito ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Toast.makeText(
                        context,
                        "Inserito evento di nome ${newEvent.nome} con successo ",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        context,
                        "Errore inserimento evento con id ${newEvent.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    fun getFriends(): List<String> {
        FirebaseDBHelper.getNomiContattiForCreationEvent(currentUserId.uid){
            friendsList -> Log.d(TAG , "${friendsList.map { it }}")
        }
        return mutableListOf()
    }

}