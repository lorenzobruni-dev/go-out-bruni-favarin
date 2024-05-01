import com.google.android.gms.maps.model.LatLng

data class PlaceInfo(
    val id: String = "",
    val address: String = "",
    val latLng: LatLng = LatLng(0.0, 0.0)
) {
    constructor() : this("", "", LatLng(0.0, 0.0))
}