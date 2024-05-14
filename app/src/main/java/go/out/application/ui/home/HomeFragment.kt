package go.out.application.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import go.out.application.EventAdapter
import go.out.application.FirebaseDBHelper
import go.out.application.FullEventListActivity
import go.out.application.R

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var adapter: EventAdapter

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
        deletionEventsWhenPartecipantFieldsIsEmpty()

        val welcomeText = view.findViewById<TextView>(R.id.textViewBenvenuto)

        val userReference = FirebaseDBHelper.dbUsers.child(currentUser.uid)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nomeUtente = dataSnapshot.child("nome").getValue(String::class.java)
                    if (nomeUtente != null) {
                        welcomeText.text = "Welcome $nomeUtente! :) "
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })


        val invitationEventView: TextView = view.findViewById(R.id.tv_mostra_altro1)
        invitationEventView.setOnClickListener {
            val intent = Intent(requireContext(), FullEventListActivity::class.java)
            intent.putExtra("bool_key", true)
            startActivity(intent)
        }

        val confirmEventView: TextView = view.findViewById(R.id.tv_mostra_altro2)
        confirmEventView.setOnClickListener {
            val intent = Intent(requireContext(), FullEventListActivity::class.java)
            intent.putExtra("bool_key", false)
            startActivity(intent)
        }
    }

    private fun deletionEventsWhenPartecipantFieldsIsEmpty() {
        FirebaseDBHelper.removeEventsFromUserWhenPartecipantFieldIsEmpty(currentUser.uid) { onComplete ->
            if (onComplete) {
                getConfirmedEvents()
            } else Toast.makeText(context, "Generic error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getConfirmedEvents() {
        FirebaseDBHelper.getUserEvents(currentUser.uid) { confirmedEventsList, eventNamesList ->
            val messageTextView = view?.findViewById<TextView>(R.id.messageTextView2)
            val listView = view?.findViewById<ListView>(R.id.lv_eventi_confermati)
            val btn = view?.findViewById<TextView>(R.id.tv_mostra_altro2)
            if (confirmedEventsList.isEmpty()) {
                messageTextView?.visibility = View.VISIBLE
                listView?.visibility = View.GONE
                btn?.visibility = View.GONE
            } else {
                messageTextView?.visibility = View.GONE
                listView?.visibility = View.VISIBLE
                val primiElementi = confirmedEventsList.take(1)
                adapter = EventAdapter(
                    requireContext(),
                    R.layout.event_invitations,
                    primiElementi,
                    layoutInflater,
                    currentUser,
                    false
                )
                listView?.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getInvitationsData() {
        FirebaseDBHelper.getInvitedEvents(currentUser.uid) { eventsList, eventsIdsList ->
            val messageTextView = view?.findViewById<TextView>(R.id.messageTextView)
            val listView = view?.findViewById<ListView>(R.id.lv_eventi)
            val btn = view?.findViewById<TextView>(R.id.tv_mostra_altro1)
            if (eventsList.isEmpty()) {
                messageTextView?.visibility = View.VISIBLE
                listView?.visibility = View.GONE
                btn?.visibility = View.GONE
            } else {
                messageTextView?.visibility = View.GONE
                listView?.visibility = View.VISIBLE
                val primiElementi = eventsList.take(1)
                adapter = EventAdapter(
                    requireContext(),
                    R.layout.event_invitations,
                    primiElementi,
                    layoutInflater,
                    currentUser,
                    true
                )
                listView?.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

    }


}



