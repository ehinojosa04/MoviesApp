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

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Retrieve movie details from Intent
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

            movieId?.let {
                myRef.child(it).setValue(Movie(updatedName, updatedYear, updatedGenre, it, updatedLatitude, updatedLongitude))
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            Toast.makeText(this, "Womp womp", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                callback(it.latitude.toString(), it.longitude.toString())
            }
        }
    }
}
