package com.example.movies

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MovieAdapter(private val context: Context, private val arrayList: ArrayList<Movie>) :
    ArrayAdapter<Movie>(context, R.layout.item, arrayList) {

    private val genreIconMap = mapOf(
        "Action" to R.drawable.action_genre_icon,
        "Sci-fi" to R.drawable.scifi_genre_icon,
        "Drama" to R.drawable.drama_genre_icon,
        "Horror" to R.drawable.horror_genre_icon,
        "Romance" to R.drawable.romance_genre_icon
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = convertView ?: inflater.inflate(R.layout.item, parent, false)

        val movie = arrayList[position]
        view.findViewById<TextView>(R.id.name).text = movie.name
        view.findViewById<TextView>(R.id.year).text = movie.year
        view.findViewById<TextView>(R.id.genre).text = movie.genre
        view.findViewById<TextView>(R.id.location).text = "Last edited in: ${movie.latitude}, ${movie.longitude}"

        val genreIcon = genreIconMap[movie.genre] ?: R.drawable.default_genre_icon
        view.findViewById<ImageView>(R.id.imageView).setImageResource(genreIcon)

        return view
    }
}