package io.dev.tfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import io.dev.tfg.ui.AdminActivity
import io.dev.tfg.ui.MaterialActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btn = findViewById<Button>(R.id.button)
        val user: EditText = findViewById(R.id.user)
        val pass: EditText = findViewById(R.id.password)
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Usuarios")

        btn.setOnClickListener{
            val user: String = user.text.toString()
            val pass: String = pass.text.toString()
            val userDocRef =  userRef.document(user)
            userDocRef.get().addOnSuccessListener { documentSnaphot ->
                if (documentSnaphot.exists()){
                    val passBd = documentSnaphot.getString("password")
                    val admin = documentSnaphot.getBoolean("Admin")
                    if(passBd == pass){
                        if(admin == true)
                        {
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            val intent = Intent(this, MaterialActivity::class.java)
                            startActivity(intent)

                        }
                    }
                    else{
                    }
                }
            }
                .addOnFailureListener{}
        }
    }
}