package go.out.application

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FullEventListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val isAnInvitationOrConfirmEvent = intent.getBooleanExtra("bool_key", false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_event_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        title = "Lista eventi utente "

        if (isAnInvitationOrConfirmEvent) getInvitationsData()
        else getConfirmedEvents()
    }

    private fun getInvitationsData() {
        FirebaseDBHelper.getInvitedEvents(currentUser.uid) { eventsList, eventsIdsList ->
            adapter = EventAdapter(
                this,
                R.layout.event_invitations,
                eventsList,
                layoutInflater,
                currentUser,
                true
            )
            val listView = findViewById<ListView>(R.id.listViewFullEventList)
            listView?.adapter = adapter
            adapter.notifyDataSetChanged()
            }
        }
    private fun getConfirmedEvents() {
        FirebaseDBHelper.getUserEvents(currentUser.uid) { confirmedEventsList, eventNamesList ->
            adapter = EventAdapter(
                this,
                R.layout.event_invitations,
                confirmedEventsList,
                layoutInflater,
                currentUser,
                false
            )
            val listView = findViewById<ListView>(R.id.listViewFullEventList)
            listView?.adapter = adapter
            adapter.notifyDataSetChanged()
            }
        }
    }


