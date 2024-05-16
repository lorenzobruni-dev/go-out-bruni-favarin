package go.out.application.ui.contact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import go.out.application.R
import go.out.application.User

class ContactAdapter(
    adapterContext: Context,
    resource: Int,
    objects: List<User>,
    val layoutInflater: LayoutInflater,
) : ArrayAdapter<User>(adapterContext, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ContactsFragment.ViewHolder

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.contact_item, parent, false)
            holder = ContactsFragment.ViewHolder()
            holder.textNome = view.findViewById(R.id.textNome)
            holder.textEmail = view.findViewById(R.id.textEmail)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ContactsFragment.ViewHolder
        }

        val utente = getItem(position)
        holder.textNome?.text = capitalizeFirstLetter(utente?.nome ?: "")
        holder.textEmail?.text = utente?.email


        return view
    }

    private fun capitalizeFirstLetter(name: String): String {
        return name.replaceFirstChar { it.uppercase() }
    }

}