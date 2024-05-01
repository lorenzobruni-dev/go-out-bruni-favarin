package go.out.application.ui.event.creation

import PlaceInfo
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
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import go.out.application.R
import java.util.Calendar
import java.util.Locale

class CreationEventFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: CreationEventViewModel by viewModels()
    private val minDateInMillis = System.currentTimeMillis()
    private var selectedPlaceInfo: PlaceInfo? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.creation_event, container, false)
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

        ediTextData?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    ediTextData.setText(selectedDate)
                },
                year, month, dayOfMonth
            )

            datePickerDialog.datePicker.minDate = minDateInMillis
            datePickerDialog.show()
        }
        editTextOra?.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { _, selectedHourOfDay, selectedMinute ->
                    val selectedTime = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        selectedHourOfDay,
                        selectedMinute
                    )
                    editTextOra.setText(selectedTime)
                },
                hours,
                minutes,
                true
            )
            timePickerDialog.show()
        }

        Places.initialize(requireContext(), getString(R.string.id_api_key))

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autoComplete_fragment) as AutocompleteSupportFragment


        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                selectedPlaceInfo = PlaceInfo(
                    id = place.id ?: "",
                    address = place.address ?: "",
                    latLng = place.latLng ?: LatLng(0.0, 0.0)
                )
                Log.d(TAG , selectedPlaceInfo?.address.toString())
                autocompleteFragment.setText(selectedPlaceInfo?.address)
            }

            override fun onError(p0: Status) {
                Toast.makeText(requireContext() , "Some error" , Toast.LENGTH_SHORT).show()

            }

        })


        btn_saveChanges.setOnClickListener {
            var isPossibleToSendInvite =
                editTextNameEvent?.text.toString().isNotEmpty() &&
                        ediTextData?.text.toString().isNotEmpty() &&
                        editTextOra?.text.toString().isNotEmpty() &&
                        selectedPlaceInfo?.id.toString().isNotEmpty()

            if (isPossibleToSendInvite)
                selectedPlaceInfo?.let { place ->
                    viewModel.saveEvent(
                        requireContext(),
                        editTextNameEvent?.text.toString(),
                        ediTextData?.text.toString(),
                        editTextOra?.text.toString(),
                        place
                    )
                }
            else Toast.makeText(requireContext(), "Errore nell'inserimento", Toast.LENGTH_SHORT)
                .show()
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onMapReady(p0: GoogleMap?) {
        TODO("Not yet implemented")
    }
}