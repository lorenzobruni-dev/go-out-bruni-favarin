package go.out.application.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import go.out.application.Event
import go.out.application.EventAdapter
import go.out.application.FirebaseDBHelper
import go.out.application.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbEvents: DatabaseReference
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbEvents = FirebaseDatabase.getInstance("https://your-database-url.firebaseio.com/").getReference("Eventi")
        adapter = EventAdapter(requireContext())

        // Recupera gli eventi a cui l'utente è stato invitato e aggiorna l'interfaccia
        fetchInvitedEvents()

        return root
    }

    private fun fetchInvitedEvents() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID == null) {
            return
        }

        FirebaseDBHelper.getInvitedEvents(currentUserID) { eventsList, _ ->
            displayInvitedEvents(eventsList)
        }
    }

    private fun displayInvitedEvents(eventList: List<Event>) {
        adapter.setEvents(eventList)
        binding.eventsList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



