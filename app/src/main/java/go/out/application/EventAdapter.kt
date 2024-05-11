package go.out.application

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import go.out.application.ui.event.creation.Event

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
            showEventDetailsDialog(eventId!!)
        }
        return view
    }

    private fun showEventDetailsDialog(eventId: String) {
        var nomeUtente = ""
        FirebaseDBHelper.getUtenteDaID(currentUser.uid) { user ->
            if (user != null) {
                nomeUtente = user.nome!!
            }
        }
        val selectedEvent = eventsList.find { it.id == eventId }
        val alertDialogBuilder = AlertDialog.Builder(adapterContext)
        val message = FirebaseDBHelper.buildEventDetailsMessage(selectedEvent!!)
        alertDialogBuilder.setTitle("Dettagli dell'evento")
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("chiudi") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.setPositiveButton("Apri mappa") { dialog, _ ->
            dialog.dismiss()

            val latitude = selectedEvent.place?.latitude
            val longitude = selectedEvent.place?.longitude
            val nomeEvento = selectedEvent.nome


            if (latitude != null && longitude != null) {
                val intent = Intent(adapterContext, MapActivity::class.java).apply {
                    putExtra("latitude", latitude)
                    putExtra("longitude", longitude)
                    putExtra("nomeEvento" , nomeEvento)
                }
                adapterContext.startActivity(intent)
            } else Toast.makeText(context, "Coordinate non disponibili", Toast.LENGTH_SHORT).show()
        }
        if (boolean) {
            alertDialogBuilder.setNegativeButton("rifiuta") { dialog, _ ->
                FirebaseDBHelper.removeParticipantFromEvent(eventId, nomeUtente) { success ->
                    if (success) {
                        eventsList = eventsList.filterNot { it.id == eventId }
                        notifyDataSetChanged()
                    }
                    dialog.dismiss()
                }
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

}}




