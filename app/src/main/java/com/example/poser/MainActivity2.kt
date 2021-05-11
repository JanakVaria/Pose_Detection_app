package com.example.poser

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.internal.MultiFlavorDetectorCreator.getInstance

class MainActivity2 : AppCompatActivity() {

    var imageViewPose: ImageView? = null
    var btnRefresh: LinearLayout? = null
    var angleTextView: TextView? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        imageViewPose = findViewById(R.id.imageView2)
        angleTextView = findViewById(R.id.angleText)
        btnRefresh = findViewById(R.id.refresh)

        btnRefresh?.setOnClickListener ({
            backMain()
        })
        auth = Firebase.auth

        val intent = intent
        val text = intent.getStringExtra("Text")
        angleTextView?.setText(text)
        var singleton: Singleton? = Singleton
        if (singleton != null) {
            imageViewPose?.setImageBitmap(singleton.getMyImage())
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id==R.id.logout){
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun backMain() {
        val intent = Intent(this@MainActivity2, MainActivity::class.java)
        startActivity(intent)
    }
}