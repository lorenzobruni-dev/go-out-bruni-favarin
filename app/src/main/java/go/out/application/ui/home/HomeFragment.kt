package go.out.application.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import go.out.application.Event
import go.out.application.EventAdapter
import go.out.application.FirebaseDBHelper
import go.out.application.FirebaseDBHelper.Companion.auth
import go.out.application.FirebaseDBHelper.Companion.getInvitedEvents
import go.out.application.FullEventListActivity
import go.out.application.R

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var adapter: EventAdapter
    private var invitedEvents: List<Event> = emptyList()
    private val eventsIdsList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        getInvitationsData()

        val textViewMostraAltro: TextView = view.findViewById(R.id.textViewMostraAltroBox1)
        textViewMostraAltro.setOnClickListener {
            val intent = Intent(requireContext(), FullEventListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getInvitationsData() {
        FirebaseDBHelper.getInvitedEvents(currentUser.uid) { eventsList, eventsIdsList ->
            this.eventsIdsList.clear()
            this.eventsIdsList.addAll(eventsIdsList)
            invitedEvents = eventsList
            val primiElementi = invitedEvents.take(1)
            adapter = EventAdapter(
                requireContext(),
                R.layout.event_invitations,
                primiElementi,
                layoutInflater,
                currentUser
            )
            val listView = view?.findViewById<ListView>(R.id.lv_eventi)
            listView?.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}



