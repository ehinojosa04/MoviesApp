package com.example.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var data = ArrayList<Movie>()
    private val database = Firebase.database
    private val myRef = database.getReference("Movies")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        auth = FirebaseAuth.getInstance()

        val fabAddMovie = view.findViewById<FloatingActionButton>(R.id.add_button)
        fabAddMovie.setOnClickListener {
            val editFragment = EditFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
        }

        readDB(view)
        return view
    }

    private fun readDB(view: View) {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
                makeList(view)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load movies.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makeList(view: View) {
        val list = view.findViewById<ListView>(R.id.list)
        val adapter = MovieAdapter(requireActivity(), data)
        list.adapter = adapter

        list.setOnItemClickListener { _, _, position, _ ->
            val movie = data[position]
            val editFragment = EditFragment().apply {
                arguments = Bundle().apply {
                    putString("MOVIE_ID", movie.id)
                    putString("MOVIE_NAME", movie.name)
                    putString("MOVIE_YEAR", movie.year)
                    putString("MOVIE_GENRE", movie.genre)
                    putString("MOVIE_LATITUDE", movie.latitude)
                    putString("MOVIE_LONGITUDE", movie.longitude)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}