package go.out.application.ui.event.creation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel

class CreationEventViewModel : ViewModel() {
    fun saveEvent(nome: String, data: String, ora: String) {
        // Qui puoi implementare la logica per salvare l'evento nel database o effettuare altre operazioni necessarie
        Log.d(TAG, "Salva evento: Nome=$nome, Data=$data, Ora=$ora")
    }
}