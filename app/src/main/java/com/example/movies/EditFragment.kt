package com.example.movies

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditFragment : Fragment() {

    private val database = Firebase.database
    private val myRef = database.getReference("Movies")
    private var movieId: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        val editName = view.findViewById<EditText>(R.id.editMovieName)
        val editYear = view.findViewById<EditText>(R.id.editMovieYear)
        val editGenre = view.findViewById<EditText>(R.id.editMovieGenre)
        val editLatitude = view.findViewById<EditText>(R.id.editLatitude)
        val editLongitude = view.findViewById<EditText>(R.id.editLongitude)
        val btnSave = view.findViewById<Button>(R.id.buttonEdit)
        val btnCancel = view.findViewById<Button>(R.id.buttonCancel)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        movieId = arguments?.getString("MOVIE_ID")
        editName.setText(arguments?.getString("MOVIE_NAME"))
        editYear.setText(arguments?.getString("MOVIE_YEAR"))
        editGenre.setText(arguments?.getString("MOVIE_GENRE"))

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
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
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

            requireActivity().supportFragmentManager.popBackStack()
        }

        btnCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun getCurrentLocation(callback: (String, String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                callback(location.latitude.toString(), location.longitude.toString())
            } else {
                Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show()
                callback("0.0", "0.0")
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to fetch location: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    view?.findViewById<EditText>(R.id.editLatitude)?.setText(latitude)
                    view?.findViewById<EditText>(R.id.editLongitude)?.setText(longitude)
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}