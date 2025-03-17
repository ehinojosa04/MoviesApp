package com.example.movies

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Edit : AppCompatActivity() {
    private val database = Firebase.database
    private val myRef = database.getReference("Movies")
    private var movieId: String? = null

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
        val btnSave = findViewById<Button>(R.id.buttonEdit)
        val btnCancel = findViewById<Button>(R.id.buttonCancel) // Initialize Cancel Button

        // Retrieve movie details from Intent
        movieId = intent.getStringExtra("MOVIE_ID")
        editName.setText(intent.getStringExtra("MOVIE_NAME"))
        editYear.setText(intent.getStringExtra("MOVIE_YEAR"))
        editGenre.setText(intent.getStringExtra("MOVIE_GENRE"))

        btnSave.setOnClickListener {
            val updatedName = editName.text.toString()
            val updatedYear = editYear.text.toString()
            val updatedGenre = editGenre.text.toString()

            movieId?.let {
                myRef.child(it).setValue(Movie(updatedName, updatedYear, updatedGenre, it))
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Set OnClickListener for Cancel Button
        btnCancel.setOnClickListener {
            // Navigate back to MainActivity without saving changes
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}