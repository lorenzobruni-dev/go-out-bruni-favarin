package go.out.application

data class User (
    var id: String?,
    var nome: String?,
    var password: String?,
    var email: String?,
    var contatti: MutableList<String>?,
    var eventi: MutableList<String>?
) {
    constructor(): this("", "", "", "", null, null)

    private val dbReference = FirebaseDBHelper.dbUsers.child("user")

}