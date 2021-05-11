package com.example.poser

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("Login",false)){
            val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val checkBox: CheckBox
        val login: Button
        val registerView: TextView
        val viewEmailAddress: TextView
        val viewPassword: TextView

        checkBox = findViewById(R.id.checkBox)
        login = findViewById(R.id.loginBtn)
        registerView = findViewById(R.id.registerView)
        viewEmailAddress = findViewById(R.id.viewEmailAddress)
        viewPassword = findViewById(R.id.viewPassword)

        auth = Firebase.auth

//for login remembering
        editor = sharedPreferences.edit()
        checkBox.setOnClickListener {
            if (checkBox.isChecked){
                editor.putBoolean("Login", true)
                editor.apply()
            }else{
                editor.putBoolean("Login",false)
                editor.apply()
            }
        }
//        checkBox.setOnClickListener {
//
//        }



        login.setOnClickListener {
            val email: String = viewEmailAddress.text.toString()
            val password: String = viewPassword.text.toString()

            if (email.isEmpty()) {
                viewEmailAddress.error = "Enter Email Address"
            } else if (password.isEmpty()) {
                viewPassword.error = "Enter Password"
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, "Login Sucessful", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        registerView.setOnClickListener() {
            val intent = Intent(this, registerActivity::class.java)
            startActivity(intent)
        }
    }
}