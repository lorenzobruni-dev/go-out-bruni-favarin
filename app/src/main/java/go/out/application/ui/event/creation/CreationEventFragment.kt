package go.out.application.ui.event.creation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import go.out.application.R
import java.util.Calendar
import java.util.Locale

class CreationEventFragment : Fragment() {

    private val viewModel: CreationEventViewModel by viewModels()
    val minDateInMillis = System.currentTimeMillis()

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



        ediTextData?.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth  ->
                    val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    Log.d(TAG,selectedDate.toString() + editTextNameEvent?.text.toString())
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

        viewModel.saveEvent(editTextNameEvent.toString(), ediTextData?.text.toString(), editTextOra?.text.toString())
        return view
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}