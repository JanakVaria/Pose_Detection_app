package com.example.poser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class registerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailWidget: EditText
        val passwordWidget: EditText
        val reenterPasswordWidget: EditText
        val registerBtn: Button

        emailWidget = findViewById(R.id.emailWidget)
        passwordWidget = findViewById(R.id.passwordWidget)
        reenterPasswordWidget = findViewById(R.id.reenterpasswordWidget)
        registerBtn = findViewById(R.id.registerBtn)



        auth = Firebase.auth


        registerBtn.setOnClickListener {

            val email: String = emailWidget.text.toString()
            val password: String = passwordWidget.text.toString()
            val repassword: String = reenterPasswordWidget.text.toString()

            if (email.isEmpty()) {
                emailWidget.error = "Enter Email Address"
            } else if (password.isEmpty()) {
                passwordWidget.error = "Enter Password"
            } else if (repassword.isEmpty()) {
                reenterPasswordWidget.error = "Re-Enter Password"
            } else if (password.length <= 6) {
                passwordWidget.error = "Password Must Be greater Than 6 Words"
            } else if (!password.equals(repassword)) {
                reenterPasswordWidget.error = "The Passwords Do Not Match . Enter Password Again "
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(
                                baseContext, "Authentication Sucessful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}