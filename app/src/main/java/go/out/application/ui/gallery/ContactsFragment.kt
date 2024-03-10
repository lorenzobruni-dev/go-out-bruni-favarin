package go.out.application.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import go.out.application.FirebaseDBHelper
import go.out.application.R
import go.out.application.User
import go.out.application.databinding.FragmentContactsBinding
import go.out.application.ui.event.creation.CreationEventViewModel

class ContactsFragment : Fragment() {

    private val viewModel: ContactsViewModel by viewModels()
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


        listView.setOnItemClickListener { _, _, position, _ ->
            val utenteSelezionato = data[position]
            Toast.makeText(requireContext(), "Hai cliccato su: ${utenteSelezionato.nome}", Toast.LENGTH_SHORT).show()
            // Aggiungi qui la logica per gestire l'azione quando viene cliccato un elemento
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
