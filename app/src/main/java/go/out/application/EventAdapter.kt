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
    var currentUser: FirebaseUser,
    var boolean: Boolean
) : ArrayAdapter<String>(adapterContext, resource, eventsList.map {it.id}) {
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
            showEventDetailsDialog(eventId!!, boolean)
        }
        return view
    }
    private fun showEventDetailsDialog(eventId: String, boolean: Boolean) {
        val selectedEvent = eventsList.find { it.id == eventId }
        val alertDialogBuilder = AlertDialog.Builder(adapterContext)
        val message = FirebaseDBHelper.buildEventDetailsMessage(selectedEvent!!)
        alertDialogBuilder.setTitle("Dettagli dell'evento")
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("chiudi") { dialog, _ ->
            dialog.dismiss()
        }
        if (boolean) {
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
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

}}




