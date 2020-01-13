package com.hadi.restaurantsarround.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.hadi.restaurantsarround.BaseApplication
import com.hadi.restaurantsarround.R
import com.hadi.restaurantsarround.model.Venue
import com.hadi.restaurantsarround.model.VenuesListResponse
import com.hadi.restaurantsarround.network.FourSquareAPI
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    @Inject
    lateinit var retrofit: Retrofit

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager: LocationManager? = null

    private var restaurants: ArrayList<Venue?>? = null
    private var markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as BaseApplication).getApiComponent().inject(this)

        setContentView(R.layout.activity_main)

        configureToolbar()

        (mapFragment as SupportMapFragment).getMapAsync(this)

        enableLocationListener()

        obtieneLocalization()
    }

    //MARK: location updates
    private fun obtieneLocalization() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                //MARK: update location
                if (location != null) {
                    this@MainActivity.map?.isMyLocationEnabled = true
                    this@MainActivity.map?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 15.0f
                        )
                    )
                    getData(location.latitude.toString(), location.longitude.toString())
                }
            }
    }

    //MARK: make network call to server
    private fun getData(lat: String, lon: String) {
        textViewMessage.text = "Loading Data ..."
        val categoryId = "4d4b7105d754a06374d81259"
        val apiVersion = "20130815"
        val apiClient = retrofit.create<FourSquareAPI>(FourSquareAPI::class.java)
        apiClient.getVenuesByLocation(
            apiVersion,
            getString(R.string.foursquare_client_id),
            getString(R.string.foursquare_client_secret),
            categoryId,
            "$lat,$lon"
        )
            ?.enqueue(object : Callback<VenuesListResponse?> {
                override fun onFailure(call: Call<VenuesListResponse?>, t: Throwable) {
                    textViewMessage.text = t.localizedMessage
                }

                override fun onResponse(
                    call: Call<VenuesListResponse?>,
                    response: Response<VenuesListResponse?>
                ) {
                    textViewMessage.text = "Restaurants Loaded"
                    val venueBody = response.body()
                    val venues = venueBody?.response
                    if (200 == venueBody?.meta?.code) {
                        this@MainActivity.restaurants = venues?.venues
                    } else {
                        textViewMessage.text = "Error Loading"
                    }
                    addMarkersOnMap()
                }

            })
    }

    override fun onResume() {
        super.onResume()
        isLocationEnabled()
    }

    //MARK: check if location is enabled, tru enable
    private fun isLocationEnabled() {
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle("Location Information")
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.")
            alertDialog.setPositiveButton(
                "Location Settings"
            ) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel() }
            val alert: AlertDialog = alertDialog.create()
            alert.show()
        } else {
            enableMyLocation()
        }
    }


    //MARK: add locations to the map
    fun addMarkersOnMap() {
        this.map.clear()
        this.markers.clear()
        this.restaurants?.forEach {
            val marker = map.addMarker(createMarker(it))
            this.markers.add(marker)
            marker.tag = it?.id
        }
        //MARK: check for marker and bound it
        if (this.markers.isNotEmpty()) {
            //MARK: set map boundaries
            val b = LatLngBounds.Builder()
            this.markers.forEach {
                b.include(it.position)
            }
            val bounds = b.build()
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 5)
            this.map?.animateCamera(cu)
        }
    }

    //MARK: create marker options
    private fun createMarker(it: Venue?): MarkerOptions {
        val latLng = LatLng(it?.location!!.lat, it.location!!.lng)
        return MarkerOptions()
            .position(latLng)
            .title(it.name)
            .snippet(it.location?.address)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationListener() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        if (isPermissionGranted()) {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
        }
    }

    //define the listener, listen on location changes
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    //MARK: configure toolbar
    private fun configureToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = ""
    }


    // MARK: check permission status
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // MARK: enable user current location o the map
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            obtieneLocalization()
            enableLocationListener()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //MARK: permission callback
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    //MARK: map is ready, proceed with calls
    override fun onMapReady(map: GoogleMap) {
        this.map = map
        this.map.setOnMarkerClickListener(this)
        enableMyLocation()
    }

    //MARK: when the
    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("name", marker.title)
        intent.putExtra("venueId", marker.tag.toString())
        startActivity(intent)
        return true
    }
}



