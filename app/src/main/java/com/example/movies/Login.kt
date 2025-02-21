package com.example.movies

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val btnLogin = findViewById<Button>(R.id.buttonLogin)
        val email = findViewById<EditText>(R.id.editTextEmail)
        val password = findViewById<EditText>(R.id.editTextPassword)
//use email: ehinojosa427@gmail.com pwd: 123456
        btnLogin.setOnClickListener{
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener{
                result ->
                if (result.isSuccessful){
                    Toast.makeText(this, "Logged in succesfuly", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,MainActivity::class.java).putExtra("idusuario", auth.currentUser!!.uid.toString()))
                    finish()
                }
                else {
                    Toast.makeText(this, result.exception?.message?:"No idea", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        val message: String
        if (currentUser != null) {
            startActivity(Intent(this,MainActivity::class.java).putExtra("idusuario", auth.currentUser!!.uid.toString()))
            message = "Welcome back " + currentUser.email.toString()
        }
        else message = "User not logged in"

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}