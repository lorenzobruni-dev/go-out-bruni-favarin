package go.out.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var nomeEvento: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_container_info_friends)

        nomeEvento = intent.getStringExtra("nomeEvento")

        title = "Info per evento : $nomeEvento "
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val location = LatLng(latitude, longitude)

        googleMap.addMarker(MarkerOptions().position(location).title("La tua posizione"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

    }
}