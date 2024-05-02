package go.out.application.ui.event.creation

import PlaceInfo
import android.os.Build
import androidx.annotation.RequiresApi

data class Confirmation(val nome: String?, val email: String?) {
    constructor() : this(null, null)
}

data class Event(
    var id: String?,
    var creatore: String?,
    var nome: String?,
    var data: String?,
    var ora: String?,
    var partecipanti: List<String>?,
    var place: PlaceInfo?,
    var confermati: MutableList<Confirmation>?
) {

    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        null,
        null,
        null
    )

    fun set_event(item: Event) {
        id = item.id
        nome = item.nome
        data = item.data
        ora = item.ora
        creatore = item.creatore
        place = item.place
    }

}