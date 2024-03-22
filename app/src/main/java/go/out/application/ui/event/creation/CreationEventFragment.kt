package go.out.application.ui.event.creation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import go.out.application.R
import java.util.Calendar
import java.util.Locale

class CreationEventFragment : Fragment() {

    private val viewModel: CreationEventViewModel by viewModels()
    private val minDateInMillis = System.currentTimeMillis()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.creation_event , container , false)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val ediTextData = view?.findViewById<EditText>(R.id.editTextData)
        val editTextOra = view?.findViewById<EditText>(R.id.editTextOra)
        val editTextNameEvent = view?.findViewById<EditText>(R.id.editTextNome)

        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)

        val btn_saveChanges = view.findViewById<Button>(R.id.btn_create_event)

        ediTextData?.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth  ->
                    val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    ediTextData.setText(selectedDate)
                },
                year, month, dayOfMonth
            )

            datePickerDialog.datePicker.minDate = minDateInMillis
            datePickerDialog.show()
        }
        editTextOra?.setOnClickListener{
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, selectedHourOfDay, selectedMinute ->
                    val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHourOfDay, selectedMinute)
                    editTextOra.setText(selectedTime)
                },
                hours,
                minutes,
                true
            )
            timePickerDialog.show()
        }

        btn_saveChanges.setOnClickListener {
            var isPossibleToSendInvite =
                editTextNameEvent?.text.toString().isNotEmpty() &&
                ediTextData?.text.toString().isNotEmpty() &&
                editTextOra?.text.toString().isNotEmpty()

            Log.d(TAG , isPossibleToSendInvite.toString())

            if(isPossibleToSendInvite)
                viewModel.saveEvent(
                    requireContext(),
                    editTextNameEvent?.text.toString(),
                    ediTextData?.text.toString(),
                    editTextOra?.text.toString()
                )
            else Toast.makeText(requireContext(),"Errore nell'inserimento" , Toast.LENGTH_SHORT).show()
        }

        return view
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}