package go.out.application

data class User(
    var id: String?,
    var nome: String?,
    var password: String?,
    var email: String?,
    var contatti: MutableList<String>?,
    var eventi: MutableList<String>?
) {
    private val dbReference = FirebaseDBHelper.dbUsers.child("User")

    constructor() : this("", "", "", "", null, null)

    fun getUser(): User {
        return User(id, nome, password, email, contatti, eventi)
    }

}