package io.dev.tfg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import io.dev.tfg.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btn = findViewById<Button>(R.id.button)
        val user: EditText = findViewById(R.id.user)
        val pass: EditText = findViewById(R.id.password)
        btn.setOnClickListener{
            val user: String = user.text.toString()
            val pass: String = pass.text.toString()
        }
    }
}