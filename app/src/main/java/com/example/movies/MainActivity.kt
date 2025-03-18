package com.example.movies

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var data = ArrayList<Movie>()
    private val database = Firebase.database
    private val myRef = database.getReference("Movies")

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        // Initialize the FloatingActionButton and set its click listener
        val fabAddMovie = findViewById<FloatingActionButton>(R.id.add_button)
        fabAddMovie.setOnClickListener {
            // Start the Edit activity to add a new movie
            val intent = Intent(this, Edit::class.java)
            startActivity(intent)
        }

        readDB()
    }

    private fun readDB() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Database Updated")
                data.clear()

                snapshot.children.forEach { node ->
                    val movie = Movie(
                        name = node.child("name").value?.toString() ?: "",
                        year = node.child("year").value?.toString() ?: "",
                        genre = node.child("genre").value?.toString() ?: "",
                        id = node.key?.toString() ?: "",
                        latitude = node.child("latitude").value?.toString() ?: "",
                        longitude = node.child("longitude").value?.toString() ?: ""
                    )
                    data.add(movie)
                }

                makeList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database Error: ${error.message}")
                Toast.makeText(this@MainActivity, "Failed to load movies.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makeList() {
        val list = findViewById<ListView>(R.id.list)
        val adapter = MovieAdapter(this, data)
        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->
            val movie = data[position]
            val intent = Intent(this, Edit::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
                putExtra("MOVIE_NAME", movie.name)
                putExtra("MOVIE_YEAR", movie.year)
                putExtra("MOVIE_GENRE", movie.genre)
                putExtra("MOVIE_LATITUDE", movie.latitude)
                putExtra("MOVIE_LONGITUDE", movie.longitude)
            }
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out -> {
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
                auth.signOut()
                startActivity(Intent(this, Login::class.java))
                return true
            }
            R.id.profile -> {
                Toast.makeText(this, "Moving to Profile", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}