package com.prototype.beach

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.prototype.beach.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    SuggestionAdapter.OnToggleClickListener, ActivitiesAdaptor.OnItemClickListener {
    private var mGoogleMap: GoogleMap? = null
    lateinit var binding: ActivityMainBinding

    private var isBottomSheetOpen = false
    private var weatherBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private var alertBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter
    private val markersMap: MutableMap<String, MutableList<Marker>> = mutableMapOf()
    private val activityPolygons: MutableMap<String, Polygon> = mutableMapOf()
    private lateinit var IdealActivitiesAdaptor: ActivitiesAdaptor
    private lateinit var OtherActivitiesAdaptor: ActivitiesAdaptor
    private lateinit var ProhibitedActivitiesAdaptor: ProhibitedActivites
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val locationPermissionCode = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


//        This part is for map initializtion
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapfragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        This part is for  bottomsheet for the menu
        binding.acc.setOnClickListener {
            if (!isBottomSheetOpen) {
                bottomSheet_menu();
                isBottomSheetOpen = true; // Set flag to true when BottomSheet is opened
            }
        }

//        This part is for bottomsheet for the activities
        val bottommenu = binding.bottommenu
        bottommenu.addOnButtonCheckedListener { _, checkid, ischecked ->
            if (ischecked) {
                when (checkid) {
                    R.id.act -> {
                        bottomSheet_activities()

                    };
                    R.id.weather -> {
                        bottomSheet_weather()
                    }

                    R.id.alerts -> {

                        bottomSheet_alert()
                    }
                }
            } else {
                when (checkid) {
                    R.id.act -> {
                        val bottomSheet = binding.activites

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


//This part is for recycler View of suggestions
        recyclerView = binding.suggestionRecyclerViewer
        suggestionAdapter = SuggestionAdapter(this, this)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = suggestionAdapter

        //This part is of Ideal Activity

        val bottomSheet = binding.activites

        val recyclerView: RecyclerView? = bottomSheet.findViewById(R.id.ideal_activities);
        val Idealactivities =
            mutableListOf("Jet Skiing", "Banana Boat Ride", "Speed Boat Ride", "Kayaking")


        IdealActivitiesAdaptor = ActivitiesAdaptor(Idealactivities, this);
        recyclerView?.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = IdealActivitiesAdaptor

//    Other activites

        val recyclerView1: RecyclerView? = bottomSheet.findViewById(R.id.other_activites);
        val otheractivites =
            mutableListOf(
                "Beach volleyball",
                "Go Karting",
                "Seafood",
                "Kannagi Statue",
                "Flying kites",
                "horse Riding",
                "Savor Delicious Snack",
                "Sun bath"
            )

        OtherActivitiesAdaptor = ActivitiesAdaptor(otheractivites, this);
        recyclerView1?.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        recyclerView1?.adapter = OtherActivitiesAdaptor

//Prohibited

        val recyclerView2: RecyclerView? = bottomSheet.findViewById(R.id.prohibited_activites)
        val prohibitedActivities = mutableListOf("Bathing", "Surfing", "Swimming")

        ProhibitedActivitiesAdaptor =
            ProhibitedActivites(prohibitedActivities) // Properly initialize the adapter
        recyclerView2?.layoutManager = LinearLayoutManager(this)
        recyclerView2?.adapter = ProhibitedActivitiesAdaptor

    }

    //This function is for map initilization
    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
    }



    //    This function is for bottom sheet for menu
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


    //    Bottomsheet for activites
    private fun bottomSheet_activities() {
        val bottomSheet = binding.activites

        // Get BottomSheetBehavior from the FrameLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // Set or change peekHeight
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 200


    }

//Bottomsheet for weather

    private fun bottomSheet_weather() {
        val bottomSheet = binding.bottomWeather
        weatherBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        weatherBottomSheetBehavior?.peekHeight = 200


    }
//    Bottomsheet for alert

    private fun bottomSheet_alert() {
        val bottomSheet = binding.bottomAlert
        alertBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        alertBottomSheetBehavior?.peekHeight = 200

    }


    //    This is function is for ontoggle event for suggestion
    override fun onToggleClick(position: Int, isChecked: Boolean) {
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
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Banana Boat Ride" -> {
                    val act = arrayOf(
                        LatLng(13.038482374417155, 80.28049203254425) to "Muttukadu Boat House"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Jet Skiing" -> {
                    val act = arrayOf(
                        LatLng(13.047345709320679, 80.2829382070699) to "Muttukadu Boat House"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Kayaking" -> {
                    val act = arrayOf(
                        LatLng(13.06155911434235, 80.28687797684262) to "Kayaking"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Beach volleyball" -> {
                    val act = arrayOf(
                        LatLng(13.052779902117896, 80.28292976532752) to "Beach Volleyball"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Go Karting" -> {
                    val act = arrayOf(
                        LatLng(13.059385243235477, 80.28481804039997) to "Go Karting"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Seafood" -> {
                    val act = arrayOf(
                        LatLng(13.062978853517938, 80.28335890128237) to "Seafood"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Kannagi Statue" -> {
                    val act = arrayOf(
                        LatLng(13.057760767153631, 80.2822140104438) to "Kannagi Statue"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Flying kites" -> {
                    val act = arrayOf(
                        LatLng(13.055419638401869, 80.28467091380509) to "Flying kites"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "horse Riding" -> {
                    val act = arrayOf(
                        LatLng(13.049357683955987, 80.28241249404105) to "horse Riding"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Savor Delicious Snack" -> {
                    val act = arrayOf(
                        LatLng(13.051296480340053, 80.28364631013953) to "Savor Delicious Snack"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

                "Sun bath" -> {
                    val act = arrayOf(
                        LatLng(13.047011249666053, 80.28128596629897) to "Sun bath"
                    )
                    val markers = act.mapNotNull { (location, title) ->
                        mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                    }.toMutableList()

                    // Store the list of markers in the map under the category key
                    markersMap[activity] = markers
                }

            }

        } else {
            // Logic to unmark location on the map
            removeMarkersForCategory(activity)
            Toast.makeText(this, "$activity unselected", Toast.LENGTH_SHORT).show()
        }
    }


    // Step 4: Method to add/remove markers on the map
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
            Toast.makeText(this, "$category enabled", Toast.LENGTH_SHORT).show()
        } else {
            // Remove markers related to the category
            if (category == "Swimming") {
                removeOutlineForActivity(category);
            } else {
                removeMarkersForCategory(category)
                Toast.makeText(this, "$category disabled", Toast.LENGTH_SHORT).show()
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Bathrooms" -> {
                val bathroom = arrayOf(
                    LatLng(13.055383476003025, 80.28200536890765) to "Public Bathroom",
                )
                val markers = bathroom.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
                }.toMutableList()

                // Store the list of markers in the map under the category key
                markersMap[category] = markers
            }

            "Entrances & Exits" -> {
                val loc = arrayOf(
                    LatLng(13.051326946953102, 80.28110951147448) to "Marina Beach Entry Point",
                )
                val markers = loc.mapNotNull { (location, title) ->
                    mGoogleMap?.addMarker(MarkerOptions().position(location).title(title))
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.swim))
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


}



