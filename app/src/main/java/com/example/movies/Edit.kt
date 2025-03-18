package com.example.movies

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Edit : AppCompatActivity() {
    private val database = Firebase.database
    private val myRef = database.getReference("Movies")
    private var movieId: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editName = findViewById<EditText>(R.id.editMovieName)
        val editYear = findViewById<EditText>(R.id.editMovieYear)
        val editGenre = findViewById<EditText>(R.id.editMovieGenre)
        val editLatitude = findViewById<EditText>(R.id.editLatitude)
        val editLongitude = findViewById<EditText>(R.id.editLongitude)
        val btnSave = findViewById<Button>(R.id.buttonEdit)
        val btnCancel = findViewById<Button>(R.id.buttonCancel)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        movieId = intent.getStringExtra("MOVIE_ID")
        editName.setText(intent.getStringExtra("MOVIE_NAME"))
        editYear.setText(intent.getStringExtra("MOVIE_YEAR"))
        editGenre.setText(intent.getStringExtra("MOVIE_GENRE"))

        getCurrentLocation { latitude, longitude ->
            editLatitude.setText(latitude)
            editLongitude.setText(longitude)
        }

        btnSave.setOnClickListener {
            val updatedName = editName.text.toString()
            val updatedYear = editYear.text.toString()
            val updatedGenre = editGenre.text.toString()
            val updatedLatitude = editLatitude.text.toString()
            val updatedLongitude = editLongitude.text.toString()

            if (updatedName.isEmpty() || updatedYear.isEmpty() || updatedGenre.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (movieId == null) {
                val newMovieRef = myRef.push()
                newMovieRef.setValue(
                    Movie(
                        updatedName,
                        updatedYear,
                        updatedGenre,
                        newMovieRef.key ?: "",
                        updatedLatitude,
                        updatedLongitude
                    )
                )
            } else {
                myRef.child(movieId!!).setValue(
                    Movie(
                        updatedName,
                        updatedYear,
                        updatedGenre,
                        movieId!!,
                        updatedLatitude,
                        updatedLongitude
                    )
                )
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun getCurrentLocation(callback: (String, String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                callback(location.latitude.toString(), location.longitude.toString())
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                callback("0.0", "0.0")
            }
        }.addOnFailureListener { e ->
            // Handle failure to fetch location
            Toast.makeText(this, "Failed to fetch location: ${e.message}", Toast.LENGTH_SHORT).show()
            callback("0.0", "0.0")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation { latitude, longitude ->
                    findViewById<EditText>(R.id.editLatitude).setText(latitude)
                    findViewById<EditText>(R.id.editLongitude).setText(longitude)
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}