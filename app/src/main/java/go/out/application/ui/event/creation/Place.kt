data class PlaceInfo(
    val id: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    constructor() : this("", "", 0.0, 0.0)
}