package com.example.meowbottom.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.meowbottom.R
import com.example.meowbottom.databinding.ActivityMapsBinding
import com.example.meowbottom.response.KlinikItem
import com.example.meowbottom.response.StoriesItem
import com.example.meowbottom.ui.community.CommunityViewModel
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationClient : FusedLocationProviderClient

    private val mapsViewModel by viewModels<MapsViewModel>()
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
       /* val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getMyLastLocation()
        setupViewModel()
    }

    private fun setupViewModel() {
        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]

        val target = intent.getStringExtra("TARGET")
        if (target == "Community") {
            userViewModel.getUser().observe(this, { user ->
                mapsViewModel.listMap("Bearer ${user.token}")
                mapsViewModel.listMap.observe(this, { story ->
                    getListLocation(story)
                })
            })
        } else if (target == "Clinic") {
            mapsViewModel.listClinic()
            mapsViewModel.listClinic.observe(this) { clinic ->
                getListClinic(clinic)
            }
        }

    }

    private fun getListClinic(clinic: List<KlinikItem?>?) {
        val listClinic = clinic
        if (listClinic == null) {
            Toast.makeText(this, getString(R.string.empty_location_clinic), Toast.LENGTH_SHORT).show()
        } else {
            listClinic.forEach { map ->
                val latLng = LatLng(map?.lat!!.toDouble(), map.lon!!.toDouble())
                //val snippet = "Lat : ${map.lat}, Lon : ${map.lon}"
                //val addressName = getAddressName(map.lat, map.lon)
                mMap.addMarker(MarkerOptions().position(latLng).title(map.nama?.toString()).snippet(map.alamat))
            }
        }
    }

    private fun getListLocation(story: List<StoriesItem?>?) {

        val listMap = story
        if (listMap == null) {
            Toast.makeText(this, getString(R.string.empty_location_community), Toast.LENGTH_SHORT).show()
        } else {
            listMap.forEach { map ->
                val latLng = LatLng(map?.lat!!, map.lon!!)
                val snippet = "Lat : ${map.lat}, Lon : ${map.lon}"
                //val addressName = getAddressName(map.lat, map.lon)
                mMap.addMarker(MarkerOptions().position(latLng).title(map.senderName?.toString()).snippet(snippet))
            }
        }
    }

    /**
     * ERROR TIDAK BISA LAT == 999.0
     */
    @Suppress("DEPRECATION")
    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName : String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if (location != null) {
                    showStartMarker(location)
                    mMap.isMyLocationEnabled = true
                } else {
                    Toast.makeText(this@MapsActivity, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location) {
        Toast.makeText(
            this,
            "Latitude : ${location.latitude} dan Longitude : ${location.longitude}",
            Toast.LENGTH_SHORT
        ).show()
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {

            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "setMapStyle: setMapStyle: Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "setMapStyle: Error Style", )
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private const val TAG = "Maps Activity"
    }
}