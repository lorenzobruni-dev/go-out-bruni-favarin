package go.out.application

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.ArrayList

class FullEventListActivity : AppCompatActivity() {

    private lateinit var eventList: MutableList<Event>
    private lateinit var listView: ListView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_event_list)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        Log.d(TAG, "Creazione della lista")

        // Recupera la lista degli ID degli eventi dall'intent
        val eventIdList = intent.getStringArrayListExtra("eventIdList")
        listView = findViewById(R.id.listViewFullEventList)
        Log.d(TAG, "Ora chiamo la funzione")
        getData(eventIdList)
        Log.d("InvitationsFragment", "Numero di eventi ricevuti a fine ricerca: ${eventList.size}")
        // Sposta la configurazione dell'adattatore dopo il ciclo
        eventAdapter = EventAdapter(this, R.layout.event_invitations, eventList, layoutInflater, currentUser)
        listView.adapter = eventAdapter
        eventAdapter.notifyDataSetChanged()
    }

    private fun getData(eventIdList: ArrayList<String>?) {
        eventList = mutableListOf()
        if (eventIdList != null) {
            Log.d(TAG, "Se la lista non è vuota")
            for (eventId in eventIdList) {
                FirebaseDBHelper.getEventFromDatabase(eventId) { event ->
                    event?.let {
                        eventList.add(it)
                        Log.d(TAG, "Nome dell'evento aggiunto: ${it.nome}")
                    }
                }
            }

        } else {
            Log.d(TAG, "La lista è vuota")
        }
    }
}
