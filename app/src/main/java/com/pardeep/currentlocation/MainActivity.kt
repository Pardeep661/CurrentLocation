package com.pardeep.currentlocation

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pardeep.currentlocation.databinding.ActivityMainBinding
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //--------------declaration-----------------
    var binding : ActivityMainBinding?=null
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = "MainActivity"

    //--------------declaration-----------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission()){
            println("Permission granted")
            getLastLocation()
        }else{
            requestPermissions()
        }
    }

    // --------------- last location functionality ------------------

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it !=null){
                var latitude = it.latitude
                var longnitude = it.longitude
                var address = getAddress(latitude,longnitude)
                binding?.LocationTv?.setText(address)
                binding?.latitudeTv?.setText(latitude.toString())
                binding?.longnitudeTv?.setText(longnitude.toString())
            }
        }
    }

    private fun getAddress(latitude: Double, longnitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(latitude,longnitude,1)
            if (address !=null && address.isNotEmpty()){
                val address = address[0]

                val addressString = address.getAddressLine(0)
                Log.d(TAG, "getAddress: $addressString")

                val placeIndex = addressString.indexOf(" ")
                if (placeIndex != -1){
                    return addressString.substring(placeIndex+1)
                }else{
                    return addressString
                }
            }
        }
        catch (e : IOException){
            e.printStackTrace()
        }
        return "No address found"
    }
    // --------------- last location functionality ------------------


    // ------------------ request permission ------------------
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            100
        )
    }
    // ------------------ request permission ------------------


    // ------------------ check permission ------------------

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    // ------------------ check permission ------------------

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            100 -> {
                if (grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLastLocation()
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}