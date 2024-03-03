package go.out.application

data class Confirmation(val nome: String?, val email: String?) {constructor() : this(null, null)}
data class Event (
    var id: String?,
    var nome: String?,
    var creatore: String?,
    var data: String?,
    var ora: String?,
    var luogo: String?,
    var partecipanti: List<String>?,
    var confermati:  MutableList<Confirmation>?

) {
    constructor() : this("", "", "", "", "", "", null, null,)


    fun set_event(item: Event) {
        id = item.id
        creatore = item.creatore
        data = item.data
        luogo = item.luogo
        partecipanti = item.partecipanti

    }
}