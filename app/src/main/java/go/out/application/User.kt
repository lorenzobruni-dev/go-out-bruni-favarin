package go.out.application

import android.os.Parcel
import android.os.Parcelable

data class User(
    var id: String? = null,
    var nome: String? = null,
    var password: String? = null,
    var email: String? = null,
    var contatti: MutableList<String>? = null,
    var eventi: MutableList<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nome)
        parcel.writeString(password)
        parcel.writeString(email)
        parcel.writeStringList(contatti)
        parcel.writeStringList(eventi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}