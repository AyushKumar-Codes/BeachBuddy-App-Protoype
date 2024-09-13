package com.prototype.beach

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.prototype.beach.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition

import com.google.android.gms.maps.model.Polyline
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), OnMapReadyCallback, SuggestionAdapter.OnToggleClickListener, ActivitiesAdaptor.OnItemClickListener {
    // for viewbinding
    lateinit var binding: ActivityMainBinding





    // for account
    private var accountID: Int = -1





    // for intents
    private lateinit var intentNotifications : Intent





    // for maps
    private var mGoogleMap: GoogleMap? = null
    private val markersMap: MutableMap<String, MutableList<Marker>> = mutableMapOf()
    private val activityPolygons: MutableMap<String, Polygon> = mutableMapOf()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 101
    private lateinit var currentLoc: LatLng
    private val polylines = mutableListOf<Polyline>()
    lateinit var destination: LatLng
    private lateinit var mapMarkers_Objects: MutableList<MarkerOptions>
    private var placename:String? = null



    // for bottom menu
    private lateinit var bottommenu : MaterialButtonToggleGroup



    // for bottom sheets
    private var isBottomSheetOpen = false
    private var weatherBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private var alertBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    // for activities
    private lateinit var bottomSheet : FrameLayout

    // for ideal activities subcategory
    private lateinit var idealRecyclerView: RecyclerView
    private lateinit var IdealActivitiesAdaptor: ActivitiesAdaptor
    private lateinit var Idealactivities: MutableList<String>

    // for other activities subcategory
    private lateinit var otherRecyclerView: RecyclerView
    private lateinit var otherActivitiesAdaptor: ActivitiesAdaptor
    private lateinit var otherActivitesList : MutableList<String>

    // for prohibited subcategories
    private lateinit var prohibitedActivitiesRecyclerView: RecyclerView
    private lateinit var ProhibitedActivitiesAdaptor: ProhibitedActivites
    private lateinit var prohibitedActivities : MutableList<String>




    // for suggestions
    private lateinit var sugesstionRecyclerView : RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter



    // Recycler for beaches
    private lateinit var BeachRecyclerView: RecyclerView
    private lateinit var BeachesList: MutableList<Beach>
    private lateinit var beachTrie:Trie



    // Data
    private lateinit var AllBeachesList: MutableList<Beach>



    // for icons
    private lateinit var hotelIcon: Bitmap
    private lateinit var hospitalIcon: Bitmap
    private lateinit var swimmingIcon: Bitmap
    private lateinit var ATMSIcon: Bitmap
    private lateinit var parkingIcon: Bitmap
    private lateinit var bathroomsIcon: Bitmap
    private lateinit var toiletIcon: Bitmap
    private lateinit var dinkingwaterIcon: Bitmap
    private lateinit var restaurantsIcon: Bitmap
    private lateinit var entranceIcon: Bitmap
    private lateinit var jetskiIcon: Bitmap
    private lateinit var speedboatIcon: Bitmap
    private lateinit var bannaboatIcon: Bitmap
    private lateinit var kayakingIcon: Bitmap
    private lateinit var volleyballIcon: Bitmap
    private lateinit var kartingIcon: Bitmap
    private lateinit var statueIcon: Bitmap
    private lateinit var horseIcon: Bitmap
    private lateinit var kiteIcon: Bitmap
    private lateinit var sunbathIcon: Bitmap
    private lateinit var beachIcon: Bitmap



    // for global variables
    object DataRepository {
        // notifications
        lateinit var mainNotificationManager : NotificationManager
        var notificationsList : MutableList<NotificationClass> = mutableListOf()

        val defaultChannelID = "notification_weather"
        val defaultChannelName = "Weather Alerts"
        var globalNotificationID = 1      // an id that will uniquely identify a notification
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initializing the data-binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initMap()

        initSearchButton()
        initBeachesRecycler()
        initBeachesTrie()

        initIncludeWeather()
        initMenuButton()

        initBottomMenu()

        initSuggestionsRecyclerView()

        initActivitiesSubcategory()
        initOtherActivitiesSubategory()
        initProhibitedSubcategory()


        initMultiThreading()



        // notifications
        initNotifications()
        fetchNotifications()
        testNotification()  // #testing
    }





    // Functions for Recycler
    private fun initBeachesRecycler(){
        BeachesList = mutableListOf()
        AllBeachesList = mutableListOf()

        fun loadBeachesFromJson(){
            val drawableMap = mapOf(
                "beach_marina" to R.drawable.beach_marina,
                "beach_corbyn" to R.drawable.beach_corbyn,
                "beach_kovalam" to R.drawable.beach_kovalam,
                "beach_calangute" to R.drawable.beach_calangute,
                "beach_marari" to R.drawable.beach_marari,
            )

            fun getDrawableResourceIdFromString(drawableName: String): Int {
                return drawableMap[drawableName] ?: R.drawable.question  // Default drawable resource
            }

            // Reading JSON file from resources
            val jsonStream = assets.open("Beaches.json")
            val reader = InputStreamReader(jsonStream)

            // Parsing JSON data
            val gson = Gson()
            val beachType = object : TypeToken<List<BeachJson>>() {}.type
            val beachJsonList: List<BeachJson> = gson.fromJson(reader, beachType)

            // Create and populate Beach objects
            for (beachJson in beachJsonList) {
                val beach= Beach()
                beach.id = beachJson.id
                beach.name = beachJson.name
                beach.imageName = beachJson.imageName
                beach.imageID = getDrawableResourceIdFromString(beachJson.imageName)
                beach.latitude = beachJson.latitude
                beach.longitude = beachJson.longitude
                beach.polygonCoordinates = beachJson.polygonCoordinates

                Log.d("Entered beach", "id:${beach.id}\tname:${beach.name}")
                AllBeachesList.add(beach)
            }

            Log.d("In loadBeachesFromJson","AllBeachesList.size = ${AllBeachesList.size}")
        }

        loadBeachesFromJson()

        BeachRecyclerView = binding.includesearch.MainRecyclerView
        BeachRecyclerView.layoutManager = LinearLayoutManager(this)
        BeachRecyclerView.setHasFixedSize(false)


        BeachRecyclerView.adapter = BeachAdapter(BeachesList){ selectedBeach ->

            Log.d("testing", "Selected Beach: ${selectedBeach.name}")
            BeachesList = mutableListOf(selectedBeach)
            showSuggestions(false)
            searchBeaches(BeachesList)

        }
    }

    private fun showSuggestions(state: Boolean = true) {
        if (state) {
            binding.includesearch.RecyclerConstraintLayout.visibility = View.VISIBLE
            binding.includesearch.RecyclerConstraintLayout.isClickable = true

            binding.search.alpha = 1.0F

            // Adjust alpha and clickability so map isn't obscured
            binding.mapfragment.alpha = 0.1F
            binding.mapfragment.isClickable = false

            return
        }

        binding.includesearch.RecyclerConstraintLayout.visibility = View.INVISIBLE
        binding.includesearch.RecyclerConstraintLayout.isClickable = false

        binding.search.alpha = 0.8F

        binding.mapfragment.alpha = 1.0F
        binding.mapfragment.isClickable = true
    }

    private fun getBeachIdsFromNames(viableNames: List<String>): List<Int> {
        val viableBeaches = mutableListOf<Beach>()

        // Iterate through each name in viableNames and find matching beaches
        Log.d("getBeachIdsFromNames", "viableNames.size = ${viableNames.size}")
        for (name in viableNames) {
            val matchedBeach = AllBeachesList.find { it.name.equals(name, ignoreCase = true) }
            if (matchedBeach != null) {
                viableBeaches.add(matchedBeach)  // Add matching beach object to the viable list
            }
        }

        // Return the list of IDs of the matched beaches
        return viableBeaches.map { it.id }
    }





    // Functions for Map
    private fun initMap(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapMarkers_Objects = mutableListOf()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        checkLocationPermission()

        // Set initial camera position to India
        val indiaLatLng = LatLng(20.5937, 78.9629) // Approximate coordinates of the center of India
        val initialZoomLevel = 5.0f // Adjust zoom level as per your requirement

        // Move the camera to the specified location
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, initialZoomLevel))

        // Set up marker click listener
        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16f))
            navigation(marker.position, marker.title)
            binding.bottommenu.clearChecked()
            true
        }

        // Set up map click listener
        mGoogleMap?.setOnMapClickListener {
            // Hide the button
            binding.navibutton.visibility = View.GONE
            binding.includednavi.navi.clearChecked()
            binding.bottommenu.clearChecked()
        }
    }

    private fun searchBeaches(Beaches: MutableList<Beach>) {

        binding.search.setIconified(true)
        binding.search.isFocusable = false
        binding.search.clearFocus()
        updateSearchTitle(Beaches)
        binding.includesearch.RecentSearchesTextView.text = "Explore Beaches"

        clearMarkersFromAnArray()
        createMarkersFromAnArray(Beaches)
         createPolygonsFromAnArray(Beaches)
        showSuggestions(false)

        moveCameraFromAnArray(Beaches)
        binding.suggestionRecyclerViewer.visibility = View.VISIBLE
        binding.menu.visibility = View.VISIBLE
        binding.includesearch.RecyclerConstraintLayout.visibility = View.GONE

    }

    private fun createPolygonsFromAnArray(beaches: List<Beach>) {
        for (beach in beaches) {
            val polygonOptions = PolygonOptions()

                .fillColor(Color.argb(108,227, 255, 255)) // Semi-transparent fill color (ARGB format)
                .strokeColor(Color.argb(205,129, 131, 140)) // Opaque stroke color (ARGB format)
                .strokeWidth(5f)

            for (coord in beach.polygonCoordinates) {
                // Assume coord[0] is longitude and coord[1] is latitude
                val latLng = LatLng(coord[1], coord[0]) // Correct order for LatLng
                polygonOptions.add(latLng)
            }

            mGoogleMap!!.addPolygon(polygonOptions)

            Log.d("Polygon Addition", "Added polygon for ${beach.name}")
        }
    }

    private fun moveCameraFromAnArray(Beaches:List<Beach>){
        if(Beaches.size == 1){
            val newCameraPosition = CameraPosition(LatLng(Beaches[0].latitude, Beaches[0].longitude), 16.0F, 0F, 0F)
            CameraUpdateFactory.newCameraPosition(newCameraPosition)
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition))
        }
        else{
            moveCameraToDefaultPosition()
        }
    }

    private fun moveCameraToDefaultPosition(){
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.0490, 79.2824), 4.0f))
    }

    private fun createMarkersFromAnArray(Beaches:List<Beach>){
        var beachLatLng : LatLng
        for(beach in Beaches){
            beachLatLng = LatLng(beach.latitude, beach.longitude)

            val markerOptions = MarkerOptions()
            markerOptions.title(beach.name)
            markerOptions.position(beachLatLng)

            mapMarkers_Objects.add(MarkerOptions().position(beachLatLng).title(beach.name).icon(BitmapDescriptorFactory.fromBitmap(beachIcon)))

            mGoogleMap!!.addMarker(mapMarkers_Objects[mapMarkers_Objects.lastIndex])

            Log.d("Marker Addition", "Added ${markerOptions.title} at ${markerOptions.position.latitude}, ${markerOptions.position.longitude}")    // #Debugger
        }
    }

    private fun updateSearchTitle(Beaches: MutableList<Beach>){
        Log.d("In updateSearchTitle", "Changing Search results text to ${Beaches.size}")
        var resultString = "Found ${Beaches.size} beaches"
        if(Beaches.size == 1){resultString = "Found ${Beaches.size} beach"}
        else if(Beaches.size == 0 && binding.search.query.isEmpty()){resultString = "Explore Beaches"}
        else if(Beaches.size == 0){resultString = "Found no beach"}
        binding.includesearch.RecentSearchesTextView.text = resultString
    }

    private fun clearMarkersFromAnArray() {
        for (markerObj in mapMarkers_Objects){
            Log.d("Marker Deletion", "Deleted ${markerObj.title}")    // #Debugger
            markerObj.visible(false)
            mGoogleMap!!.clear()
        }
        mapMarkers_Objects = mutableListOf(MarkerOptions())
    }

    private fun initBeachesTrie(){
        beachTrie = Trie()
        for(beach in AllBeachesList){
            beachTrie.insert(beach.name)

            Log.d("initBeachTrie", "Added ${beach.name} as beachTrie")  // #Debugging
        }
    }

    private fun initSearchButton(){
        // on opening the search bar
        binding.search.setOnSearchClickListener {
            showSuggestions()
        }

        // on closing the search bar
        binding.search.setOnCloseListener {
            showSuggestions(false)
            clearMarkersFromAnArray()
            false  // Return false if you want the default behavior to still occur
        }

        // on changing the text of the query
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            fun computeSuggestions(newText:String?) : MutableList<Beach>{
                binding.suggestionRecyclerViewer.visibility = View.GONE
                if(newText.isNullOrEmpty()){
                    updateSuggestionsUI(emptyList())  // Clear suggestions if no texts
                    return mutableListOf()
                }

                val viableBeaches : MutableList<Beach>

                val viableNames = beachTrie.searchByPrefix(newText)  // Search for viable names using Trie
                for (viableName in viableNames) Log.d("computeSuggestions","Viable name: ${viableName}")  // #Debuggin

                val viableBeachesID = getBeachIdsFromNames(viableNames)  // Get beach IDs based on the names
                for (viableBeach in viableBeachesID) Log.d("computeSuggestions","Viable beach id: ${viableBeach}")  // #Debuggin

                viableBeaches = viableBeachesID.map { beachId ->
                    AllBeachesList.find { it.id == beachId }?: Beach()  // Find full beach object by ID
                }.toMutableList()

                updateSuggestionsUI(viableBeaches)
                return viableBeaches
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrEmpty()) {
                    Log.d("Query Status", "Query is empty")
                    return true
                }

                // Perform the search in a background coroutine
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    val suggestions = withContext(Dispatchers.IO) {
                        computeSuggestions(query)
                    }
                    searchBeaches(suggestions)
                }

                binding.includesearch.RecentSearchesTextView.text = "Explore Beaches"
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val characterCount = newText?.length ?: 0
                Log.d("onQueryTextChange", "Character count: ${characterCount}")
                Log.d("onQueryTextChange", "Word changed to: ${newText}")

                showSuggestions(true)
                updateSearchTitle(computeSuggestions(newText))
                return false
            }
        })
    }

    private fun updateSuggestionsUI(matchingBeaches:List<Beach>){
        val adapter = BeachRecyclerView.adapter as BeachAdapter


        Log.d("In updateSuggestionsUI","BeachesList (before setFilteredList()): ${matchingBeaches.size}")
        adapter.setFilteredList(matchingBeaches)

        for(beach in matchingBeaches){
            Log.d("In updateSuggestionsUI","Matched Name: ${beach.name}")
        }
        Log.d("In updateSuggestionsUI","matchingBeaches (after setFilteredList()): ${matchingBeaches.size}")
    }

    private fun navigation(markercord: LatLng , title : String?) {
        placename = title
        binding.navibutton.visibility = View.VISIBLE
        binding.includednavi.navi.clearChecked()
        destination = markercord

    }

    private fun drawRoute(currentLoc: LatLng, destination: LatLng) {
        // Clear existing polylines if needed (optional)
        clearPreviousPolylines()

        // Create a PolylineOptions object to customize the polyline appearance
        val polylineOptions = PolylineOptions()
            .add(currentLoc) // Add current location
            .add(destination) // Add destination (marker position)
            .color(Color.BLUE) // Set polyline color
            .width(10f) // Set polyline width

        // Draw the polyline on the map and store it in the list
        mGoogleMap?.addPolyline(polylineOptions)?.let { polyline ->
            polylines.add(polyline)
        }
    }

    private fun clearPreviousPolylines() {
        // Iterate through the list and remove each polyline
        for (polyline in polylines) {
            polyline.remove()
        }
        // Clear the list after removing the polylines
        polylines.clear()
    }

    private fun checkLocationPermission() {
        // Check for the fine location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission if it's not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            // If permission is granted, enable live location updates
            enableUserLocation()
        }
    }

    private fun enableUserLocation() {
        try {
            mGoogleMap?.isMyLocationEnabled = true
            mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true

            // Request location updates to keep user's location updated
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    updateMapLocation(it)
                    currentLoc = LatLng(it.latitude, it.longitude)
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Location permission is required to show your location",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateMapLocation(location: Location) {
        // Set the map's camera position to the current location of the device
        val currentLatLng = LatLng(location.latitude, location.longitude)

        // Optional: Add a marker at the current location

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to add/remove markers on the map
    private fun updateMarkers(isChecked: Boolean, category: String) {
        if (isChecked) {
            // Add markers related to the category
            if (category == "Swimming") {
                val swimmingCoordinates = listOf(
                    LatLng(13.0632694, 80.2882018),
                    LatLng(13.0611797, 80.2873971),
                    LatLng(13.0582855, 80.2863736),
                    LatLng(13.0548375, 80.2852535),
                    LatLng(13.0496195, 80.2837837),
                    LatLng(13.0436943, 80.2823889),
                    LatLng(13.0383722, 80.2812946),
                    LatLng(13.0381632, 80.2814341),
                    LatLng(13.0393335, 80.2818203),
                    LatLng(13.0438684, 80.2829704),
                    LatLng(13.0474440, 80.2840841),
                    LatLng(13.0518012, 80.2850840),
                    LatLng(13.0558531, 80.2863994),
                    LatLng(13.0596647, 80.2875924),
                    LatLng(13.0633759, 80.2890494),
                    LatLng(13.0632506, 80.2882233)
                )
                drawOutlineForActivity("Swimming", swimmingCoordinates)

            } else {
                addMarkersForCategory(category)
            }

        } else {
            // Remove markers related to the category
            if (category == "Swimming") {
                removeOutlineForActivity(category);
            } else {
                removeMarkersForCategory(category)

            }
        }
    }

    private fun addMarkersForCategory(category: String) {
        when (category) {
            "Hotels" -> {
                val hotels = arrayOf(
                    LatLng(
                        13.036820881549403,
                        80.27810543700338
                    ) to "Collection O Santhome Residency Near Marina Beach",
                    LatLng(
                        13.043343032853757,
                        80.27334183397971
                    ) to "Capital O 70518 P. A.s Residency",
                    LatLng(13.045015351641068, 80.27334183397971) to "Iris Hotel",
                    LatLng(13.041064480306924, 80.26744097437829) to "Sarada Nivas Lodging"
                )
                val markers = hotels.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(hotelIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Hospitals & Clinics" -> {
                val hospital = arrayOf(
                    LatLng(
                        13.044916058169585,
                        80.2723520979109
                    ) to "C.S.I. Kalyani Multispeciality Hospital",
                    LatLng(13.049932948685889, 80.27406871161311) to "Fousiya Hospital",
                    LatLng(
                        13.058545039964992,
                        80.27578532531533
                    ) to "Sakthi Hospital & Research Centre",
                    LatLng(13.060969749370352, 80.2780169231282) to "Gosha Hospital"
                )
                val markers = hospital.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(hospitalIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Bathrooms" -> {
                val bathroom = arrayOf(
                    LatLng(13.055383476003025, 80.28200536890765) to "Public Bathroom",
                )
                val markers = bathroom.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(bathroomsIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Toilets" -> {
                val loc = arrayOf(
                    LatLng(13.060567374885101, 80.28312116781409) to "Marina beach Public Toilet",
                    LatLng(13.064622445748425, 80.28342157521197) to "SBM Toilet",
                    LatLng(13.05216438727931, 80.28131872342675) to "SBM Toilet",
                    LatLng(
                        13.049948626451362,
                        80.2811041467123
                    ) to "Physically Challenged Person Toilet",
                    LatLng(13.046144160712268, 80.28024583986118) to "Public Toilet",
                    LatLng(13.043008568022207, 80.28003126314842) to "Beach toilet"
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(toiletIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Drinking Water" -> {
                val loc = arrayOf(
                    LatLng(13.063326095458597, 80.28372197824064) to "Drinking water kiosk",
                    LatLng(13.058058642330378, 80.27694135507768) to "Sri Vel Water Supply",
                    LatLng(13.054505138898108, 80.282219942212) to "Drinking Water"
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(dinkingwaterIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Restaurants" -> {
                val loc = arrayOf(
                    LatLng(13.055340854937372, 80.28204827647426) to "Chicken Paradise",
                    LatLng(
                        13.050742143131561,
                        80.28153329236359
                    ) to "Merina beach Bengali fish fry",
                    LatLng(13.050491301796509, 80.28191953043904) to "Marina Restaurant",
                    LatLng(13.049153477301324, 80.28091101988899) to "Mobile Sea Food Restaurant"
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(restaurantsIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "ATMS" -> {
                val loc = arrayOf(
                    LatLng(13.051578177188098, 80.28123288393125) to "Indian Overseas Bank",
                    LatLng(13.058120842021953, 80.2801385426961) to "Bank of Baroda ATM",
                    LatLng(13.052748762534158, 80.28078227283443) to "IOB ATM",
                    LatLng(13.062113245698342, 80.28039603463786) to "State Bank of India ATM"
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(ATMSIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Parking" -> {
                val loc = arrayOf(
                    LatLng(13.051326946953102, 80.28110951147448) to "Marina Beach Entry Point",
                    LatLng(13.055854905978567, 80.28217493970814) to "Bike parking",
                    LatLng(13.06159421304884, 80.28320029340578) to "Marina Beach Parking",
                    LatLng(13.055072543150352, 80.28189137543683) to "Parking"
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(parkingIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Entrances & Exits" -> {
                val loc = arrayOf(
                    LatLng(13.051326946953102, 80.28110951147448) to "Marina Beach Entry Point",
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(entranceIcon)))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }


        }
    }

    private fun removeMarkersForCategory(category: String) {
        markersMap[category]?.forEach { marker ->
            marker.remove() // Remove each marker from the map
        }
        markersMap.remove(category) // Clear the markers list from the map
    }

    private fun drawOutlineForActivity(category: String, coordinates: List<LatLng>) {
        // Remove existing polygon for the category if it exists
        activityPolygons[category]?.remove()

        // Add a new polygon outline with the given coordinates
        val polygon = mGoogleMap?.addPolygon(
            PolygonOptions()
                .addAll(coordinates)
                .strokeColor(0xFF0000FF.toInt()) // Solid blue outline
                .strokeWidth(4f)
                .fillColor(0x00FFFFFF) // Transparent fill color, making it an outline
        )


        // Store the polygon and marker in the respective maps
        polygon?.let { activityPolygons[category] = it }

//        Adding title to polygon

        val markersList = markersMap.getOrPut(category) { mutableListOf() }

        // Add a transparent marker for the title at the first coordinate
        val titleMarker = mGoogleMap?.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        13.054852646358954,
                        80.28532377671235
                    )
                ) // Position the marker at the first point of the polygon
                .title(category) // Set the title of the marker
                .icon(BitmapDescriptorFactory.fromBitmap(swimmingIcon))
        )

        // Add the title marker to the list
        titleMarker?.let { markersList.add(it) }
    }

    private fun removeOutlineForActivity(category: String) {
        activityPolygons[category]?.remove()
        activityPolygons.remove(category)

        markersMap[category]?.forEach { it.remove() }
        markersMap.remove(category)
    }





    // Functions for suggestions
    private fun initSuggestionsRecyclerView(){
        sugesstionRecyclerView = binding.suggestionRecyclerViewer

        suggestionAdapter = SuggestionAdapter(this, this)

        sugesstionRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        sugesstionRecyclerView.adapter = suggestionAdapter
    }





    // Functions for Bottom Menu
    private fun initBottomMenu(){
        bottommenu = binding.bottommenu
        bottommenu.addOnButtonCheckedListener { _, checkid, ischecked ->
            if (ischecked) {
                binding.navibutton.visibility = View.GONE
                binding.includednavi.navi.clearChecked()
                when (checkid) {
                    R.id.act -> {
                        bottomSheet_activities()
                    };
                    R.id.weather -> {
                        bottomSheet_weather()
                    }

                    R.id.alerts -> {
                        startActivity(intentNotifications)
                        bottomSheet_alert()
                    }
                }
            } else {
                when (checkid) {
                    R.id.act -> {
                        // Get BottomSheetBehavior from the FrameLayout
                        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                        // Set or change peekHeight
                        bottomSheetBehavior.peekHeight = 0
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                    }

                    R.id.weather -> {
                        weatherBottomSheetBehavior?.peekHeight = 0
                        weatherBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    R.id.alerts -> {
                        alertBottomSheetBehavior?.peekHeight = 0
                        alertBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

        }
    }

    // ideal activity category
    private fun initActivitiesSubcategory(){
        bottomSheet = binding.activites

        idealRecyclerView = bottomSheet.findViewById(R.id.ideal_activities);
        Idealactivities = mutableListOf("Jet Skiing", "Banana Boat Ride", "Speed Boat Ride", "Kayaking")

        idealRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)

        IdealActivitiesAdaptor = ActivitiesAdaptor(Idealactivities, this);
        idealRecyclerView.adapter = IdealActivitiesAdaptor
    }

    // other activity category
    private fun initOtherActivitiesSubategory(){
        otherRecyclerView = bottomSheet.findViewById(R.id.other_activites)

        otherActivitesList = mutableListOf(
            "Beach volleyball",
            "Go Karting",
            "Kannagi Statue",
            "Flying kites",
            "horse Riding",
            "Sun bath"
        )

        otherActivitiesAdaptor = ActivitiesAdaptor(otherActivitesList, this);

        otherRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        otherRecyclerView.adapter = otherActivitiesAdaptor
    }

    // prohibited activity category
    private fun initProhibitedSubcategory(){
        prohibitedActivitiesRecyclerView = bottomSheet.findViewById(R.id.prohibited_activites)

        prohibitedActivities = mutableListOf("Bathing", "Surfing", "Swimming")

        ProhibitedActivitiesAdaptor = ProhibitedActivites(prohibitedActivities)

        prohibitedActivitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        prohibitedActivitiesRecyclerView.adapter = ProhibitedActivitiesAdaptor
    }

    private fun saveToggleState(buttonId: Int, isChecked: Boolean) {
        val sharedPreferences = getSharedPreferences("ToggleStates", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(buttonId.toString(), isChecked).apply()
    }

    private fun loadToggleState(buttonId: Int): Boolean {
        val sharedPreferences = getSharedPreferences("ToggleStates", MODE_PRIVATE)
        return sharedPreferences.getBoolean(
            buttonId.toString(),
            false

        ) // Default to unchecked if not saved
    }

    private fun bottomSheet_menu() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.acc_menu)

        // Find the MaterialButtonToggleGroup inside your BottomSheet layout
        val toggleGroup =
            bottomSheetDialog.findViewById<MaterialButtonToggleGroup>(R.id.toggleButtonGroup)

        toggleGroup?.let { group ->
            // Restore saved states for all buttons in the group
            for (i in 0 until group.childCount) {
                val button = group.getChildAt(i) as MaterialButton
                val isChecked = loadToggleState(button.id)
                button.isChecked = isChecked

                // Set a default button to be checked if it's the first time or no state is found
                if (button.id == R.id.toggleButtonNormal && !isChecked) { // Default button
                    button.isChecked = true
                    saveToggleState(button.id, true) // Save this default selection

                    // Set default map type if googleMap is not null
                    mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }

            // Add listener to update the saved state and change map type when any button is toggled
            group.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    saveToggleState(checkedId, true)
                    when (checkedId) {
                        R.id.toggleButtonNormal -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                        R.id.toggleButtonSatellite -> mGoogleMap?.mapType =
                            GoogleMap.MAP_TYPE_SATELLITE

                        R.id.toggleButtonTerrain -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    }
                } else {
                    saveToggleState(checkedId, false)
                }
            }
        }

        bottomSheetDialog.setOnDismissListener {
            isBottomSheetOpen = false // Optional: reset your custom flag if needed
        }

        bottomSheetDialog.show()


        bottomSheetDialog.setOnDismissListener {
            isBottomSheetOpen = false // Optional: reset your custom flag if needed
        }

        bottomSheetDialog.show()
    }

    private fun bottomSheet_activities() {
        val bottomSheet = binding.activites

        // Get BottomSheetBehavior from the FrameLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // Set or change peekHeight
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 200
    }

    private fun bottomSheet_weather() {
        val bottomSheet = binding.bottomWeather
        weatherBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        weatherBottomSheetBehavior?.peekHeight = 200
    }

    private fun bottomSheet_alert() {
        val bottomSheet = binding.bottomAlert
        alertBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        alertBottomSheetBehavior?.peekHeight = 200
    }

    // This is function is for ontoggle event for suggestion
    override fun onToggleClick(position: Int, isChecked: Boolean) {
        binding.navibutton.visibility = View.GONE
        binding.includednavi.navi.clearChecked()
        clearPreviousPolylines()
        when (position) {
            0 -> updateMarkers(isChecked, "Hotels")
            1 -> updateMarkers(isChecked, "Hospitals & Clinics")
            2 -> updateMarkers(isChecked, "Swimming")
            3 -> updateMarkers(isChecked, "ATMS")
            4 -> updateMarkers(isChecked, "Parking")
            5 -> updateMarkers(isChecked, "Bathrooms")
            6 -> updateMarkers(isChecked, "Toilets")
            7 -> updateMarkers(isChecked, "Drinking Water")
            8 -> updateMarkers(isChecked, "Restaurants")
            9 -> updateMarkers(isChecked, "Entrances & Exits")
        }
    }

    // This is function for toggle in bottom sheet
    override fun onItemChecked(activity: String, isChecked: Boolean) {
        if (isChecked) {
            // Logic to mark location on the map
            when (activity) {
                "Speed Boat Ride" -> {
                    val act = arrayOf(
                        LatLng(13.066743597771906, 80.28701516457059) to "Red Boat"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(speedboatIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Banana Boat Ride" -> {
                    val act = arrayOf(
                        LatLng(13.038482374417155, 80.28049203254425) to "Muttukadu Boat House"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(bannaboatIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Jet Skiing" -> {
                    val act = arrayOf(
                        LatLng(13.047345709320679, 80.2829382070699) to "Muttukadu Boat House"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(jetskiIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Kayaking" -> {
                    val act = arrayOf(
                        LatLng(13.06155911434235, 80.28687797684262) to "Kayaking"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(kayakingIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Beach volleyball" -> {
                    val act = arrayOf(
                        LatLng(13.052779902117896, 80.28292976532752) to "Beach Volleyball"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(volleyballIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Go Karting" -> {
                    val act = arrayOf(
                        LatLng(13.059385243235477, 80.28481804039997) to "Go Karting"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(kartingIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Kannagi Statue" -> {
                    val act = arrayOf(
                        LatLng(13.057760767153631, 80.2822140104438) to "Kannagi Statue"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(statueIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Flying kites" -> {
                    val act = arrayOf(
                        LatLng(13.055419638401869, 80.28467091380509) to "Flying kites"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(kiteIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "horse Riding" -> {
                    val act = arrayOf(
                        LatLng(13.046021568292193, 80.28222245876631) to "horse Riding"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(horseIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }


                "Sun bath" -> {
                    val act = arrayOf(
                        LatLng(13.047011249666053, 80.28128596629897) to "Sun bath"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.fromBitmap(sunbathIcon)))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

            }

        } else {
            // Logic to unmark location on the map
            removeMarkersForCategory(activity)
        }
    }

    // for the contents of the bottom menu
    private fun initIncludeWeather(){
    binding.includeweather.we.setOnClickListener() {
        binding.includeweather.we.setImageResource(R.drawable.wd)
    }

    binding.includednavi.navi.addOnButtonCheckedListener { _, checkedId, isChecked ->
        // Check if the specific button is toggled
        if (checkedId == binding.includednavi.naviButton.id) {
            if (isChecked) {
                // Button is checked: Call drawRoute
                drawRoute(currentLoc, destination)
                binding.includednavi.naviButton.text = "Cancel"

                val startLocation = Location("start").apply {
                    latitude = currentLoc.latitude
                    longitude = currentLoc.longitude
                }

                val endLocation = Location("end").apply {
                    latitude = destination.latitude
                    longitude = destination.longitude
                }

                // Calculate the distance in meters
                val distance = startLocation.distanceTo(endLocation)

                binding.includednavi.placename.text = placename
                val formattedDistance = String.format("%.2f", distance)
                binding.includednavi.dis.text = "$formattedDistance meters"

                binding.includednavi.navcard.visibility = View.VISIBLE

            } else {
                binding.includednavi.naviButton.text = "Find Route"
                // Button is unchecked: Call clearPreviousPolylines
                binding.includednavi.navcard.visibility = View.GONE
                clearPreviousPolylines()
            }
        }
    }
}

    // menu button
    private fun initMenuButton(){
        // This part is for  bottomsheet for the menu
        binding.acc.setOnClickListener {
            if (accountID == -1) {
                var intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)

                if (!isBottomSheetOpen) {
                    bottomSheet_menu();
                    isBottomSheetOpen = true; // Set flag to true when BottomSheet is opened
                }
            }
        }
    }







    // for Multi-threading


    // Multithreading Icon Resizing
    private fun initMultiThreading(){
        CoroutineScope(Dispatchers.IO).launch {
            var originalIcon = BitmapFactory.decodeResource(resources, R.drawable.hotel)
            hotelIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)
            originalIcon = BitmapFactory.decodeResource(resources, R.drawable.hospital)
            hospitalIcon = Bitmap.createScaledBitmap(originalIcon, 150, 140, false)
            originalIcon = BitmapFactory.decodeResource(resources, R.drawable.swimming  )
            swimmingIcon = Bitmap.createScaledBitmap(originalIcon, 150, 140, false)
            originalIcon = BitmapFactory.decodeResource(resources, R.drawable.atm)
            ATMSIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)
            originalIcon = BitmapFactory.decodeResource(resources, R.drawable.parking)
            parkingIcon = Bitmap.createScaledBitmap(originalIcon, 130, 110, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.shower)
            bathroomsIcon = Bitmap.createScaledBitmap(originalIcon, 125, 110, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.bathroom)
            toiletIcon = Bitmap.createScaledBitmap(originalIcon, 120, 120, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.tap)
            dinkingwaterIcon = Bitmap.createScaledBitmap(originalIcon, 120, 120, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.restaurant)
            restaurantsIcon = Bitmap.createScaledBitmap(originalIcon, 140, 130, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.entrance)
            entranceIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.jetski)
            jetskiIcon = Bitmap.createScaledBitmap(originalIcon, 100, 100, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.beach)
            beachIcon =  Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.bananaboat)
            bannaboatIcon =  Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.speedboat)
            speedboatIcon =  Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.kayaking)
            kayakingIcon =  Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.statue)
            statueIcon =   Bitmap.createScaledBitmap(originalIcon, 145, 180, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.volleyball)
            volleyballIcon = Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.karting)
            kartingIcon = Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.kite)
            kiteIcon = Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.horse)
            horseIcon = Bitmap.createScaledBitmap(originalIcon, 145, 160, false)
            originalIcon =  BitmapFactory.decodeResource(resources, R.drawable.sunbath)
            sunbathIcon = Bitmap.createScaledBitmap(originalIcon, 145, 160, false)

        }
    }






    // for notifications
    private fun initNotifications(){
        intentNotifications = Intent(this, NotificationActivity::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel (for Android 8.0 and above)
            val mainChannel = NotificationChannel(
                DataRepository.defaultChannelID,
                DataRepository.defaultChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            mainChannel.enableVibration(true)
            mainChannel.description = "BeachBuddy Notifications"
            mainChannel.lockscreenVisibility = 1

            com.bluecoast.map.SearchActivity.DataRepository.mainNotificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager

            // Register the notification channel with the system
            com.bluecoast.map.SearchActivity.DataRepository.mainNotificationManager.createNotificationChannel(mainChannel)
        }

        binding.notificationImageView.setImageResource(R.drawable.notification_bell_alarm)

        // defines an event when the notification bell is clicked on
        binding.notificationImageView.setOnClickListener{
            val intent = Intent(this, NotificationActivity::class.java)
            // Pass relevant beach data (e.g., name or ID) to the new activity
            intent.putExtra("notificationList", com.bluecoast.map.SearchActivity.DataRepository.notificationsList)
            startActivity(intent)
        }
    }

    private fun createNotification(context: Context, notificationObject : NotificationClass) {
        // Create a notification
        val notification = NotificationCompat.Builder(context, DataRepository.defaultChannelID)  // defaultChannelID required
            .setSmallIcon(R.drawable.volleyball_ball)
            .setContentTitle(notificationObject.title)
            .setContentText(notificationObject.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensures it's a high priority notification
            .build()  // Build the notification

        // Checking notification permission (for Android 13+)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(DataRepository.globalNotificationID++, notification)
        } else {
            // Optionally, request notification permission for Android 13+
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        com.bluecoast.map.SearchActivity.DataRepository.notificationsList.add(notificationObject)
    }

    private fun fetchNotifications(): MutableList<NotificationClass>{
        var userHasPendingNotifications : Boolean = true

        if (!userHasPendingNotifications)return mutableListOf()

        fun fetchNotificationsListFromServer():MutableList<NotificationClass>{
            var mockListOfNotifications: MutableList<NotificationClass> = mutableListOf(
                NotificationClass().apply {
                    id = DataRepository.globalNotificationID++
                    type = 1
                    title = "Stormy Clouds Alert"
                    message = "Stormy Clouds are expected at your planned location"
                    mainImageID = R.drawable.exclamation
                    colorCode = "red"
                },
                NotificationClass().apply {
                    id = DataRepository.globalNotificationID++
                    type = 2
                    title = "Update on Planned Trip"
                    message = "Suitable Weather expected at your planned location tomorrow"
                    mainImageID = R.drawable.calendar
                    colorCode = "blue"
                },
                NotificationClass().apply {
                    id = DataRepository.globalNotificationID++
                    type = 3
                    title = "Great weather"
                    message = "Great weather for trying volleyball"
                    mainImageID = R.drawable.volleyball_ball
                    colorCode = "green"
                }
            )
            return mockListOfNotifications
        }

        DataRepository.notificationsList = fetchNotificationsListFromServer()
        return DataRepository.notificationsList
    }









    // TESTING
    fun testNotification(){     // #testing
        for (notification in DataRepository.notificationsList){
            createNotification(baseContext, notification)
        }
    }
}