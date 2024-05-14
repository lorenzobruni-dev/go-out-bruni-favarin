package go.out.application.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import go.out.application.FirebaseDBHelper
import go.out.application.R
import go.out.application.User

class ContactsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbReference: DatabaseReference
    private lateinit var adapter: Adapter
    private val data: MutableList<User> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        dbReference = FirebaseDBHelper.dbUsers.child(currentUser.uid)

        adapter = Adapter(
            requireContext(),
            R.layout.list_item_layout,
            data,
            LayoutInflater.from(requireContext())
        )

        val listView: ListView = view.findViewById(R.id.listViewUsers)
        listView.adapter = adapter


        FirebaseDBHelper.getNomiContatti(currentUser.uid) { utentiList ->
            data.clear()
            data.addAll(utentiList)
            adapter.notifyDataSetChanged()
        }

        return view
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    class ViewHolder {
        var textNome: android.widget.TextView? = null
        var textEmail: android.widget.TextView? = null
    }
}


