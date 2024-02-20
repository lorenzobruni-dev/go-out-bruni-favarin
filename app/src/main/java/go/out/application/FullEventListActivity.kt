package go.out.application

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.ArrayList

class FullEventListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var adapter: EventAdapter
    private var invitedEvents: List<Event> = emptyList()
    private var eventsIdsList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_event_list)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        getInvitationsData()
    }

    private fun getInvitationsData() {
        FirebaseDBHelper.getInvitedEvents(currentUser.uid) { eventsList, eventsIdsList ->
            this.eventsIdsList.clear()
            this.eventsIdsList.addAll(eventsIdsList)
            invitedEvents = eventsList
            adapter = EventAdapter(
                this,
                R.layout.event_invitations,
                invitedEvents,
                layoutInflater,
                currentUser
            )
            val listView = findViewById<ListView>(R.id.listViewFullEventList)
            listView?.adapter = adapter
            adapter.notifyDataSetChanged()

            }
        }


    }

