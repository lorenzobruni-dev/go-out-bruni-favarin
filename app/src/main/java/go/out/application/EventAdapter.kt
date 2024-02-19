package go.out.application

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EventAdapter(
    val adapterContext: Context,
    val resource: Int,
    var eventsList: List<Event> = emptyList(),
    val layoutInflater: LayoutInflater,
    var currentUser: FirebaseUser
) : ArrayAdapter<String>(adapterContext, resource, eventsList.map {it.id}) {

    private val selectedEventsIds: MutableSet<String> = mutableSetOf()
    var auth = Firebase.auth




    private class ViewHolder {
        var eventName: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = layoutInflater.inflate(resource, parent, false)
            holder = ViewHolder()
            holder.eventName = view.findViewById(R.id.textEventId)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val eventId = getItem(position)
        val selectedEvent = eventsList.find { it.id == eventId }
        holder.eventName?.text = selectedEvent?.nome ?: "Nome non disponibile"


        view.setOnClickListener {

            showEventDetailsDialog(eventId!!)
        }

        return view
    }

    private fun showEventDetailsDialog(eventId: String) {
        val selectedEvent = eventsList.find { it.id == eventId }
        val alertDialogBuilder = AlertDialog.Builder(adapterContext)

        if (selectedEvent != null) {
            val eventDate = selectedEvent.data
            val eventTime = selectedEvent.ora

            alertDialogBuilder.setTitle("Dettagli dell'evento")
            alertDialogBuilder.setMessage("ID evento: $eventId\nData: $eventDate\nOra: $eventTime")
        } else {
            alertDialogBuilder.setTitle("Dettagli dell'evento")
            alertDialogBuilder.setMessage("ID dell'evento: $eventId\nData: Informazione non disponibile\nOra: Informazione non disponibile")
        }

        alertDialogBuilder.setPositiveButton("annulla") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("rifiuta") { dialog, _ ->
            FirebaseDBHelper.removeParticipantFromEvent(eventId, currentUser.uid) { success ->
                if (success) {
                    eventsList = eventsList.filterNot { it.id == eventId }
                    notifyDataSetChanged()
                }
                dialog.dismiss()
            }
        }
      //  alertDialogBuilder.setNeutralButton("partecipa") { dialog, _ ->
        //    FirebaseDBHelper.addEventToCurrentUser(selectedEvent) { success ->
          //      if (success) {
            //        FirebaseDBHelper.removeParticipantFromEvent(
              //          eventId,
                //        currentUser.uid
                  //  ) { removeSuccess ->
                    //    if (removeSuccess) {
                      //      eventsList = eventsList.filterNot { it.id == eventId }
                        //    notifyDataSetChanged()
                       // }
                   // }
               // }

                //dialog.dismiss()
           // }
        //}

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

}}




