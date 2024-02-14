package io.dev.tfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import io.dev.tfg.ui.AdminActivity
import io.dev.tfg.ui.MaterialActivity
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btn = findViewById<Button>(R.id.entry)
        val btn2 = findViewById<Button>(R.id.leave)
        val user: EditText = findViewById(R.id.user)
        val pass: EditText = findViewById(R.id.password)

        btn.setOnClickListener{
            val user: String = user.text.toString()
            val pass: String = pass.text.toString()
            val userDocRef =  userRef.document(user)
            userDocRef.get().addOnSuccessListener { documentSnaphot ->
                if (documentSnaphot.exists()){
                    val passBd = documentSnaphot.getString("password")
                    val admin = documentSnaphot.getBoolean("Admin")
                    val singing = documentSnaphot.getBoolean("fichado")
                    if(passBd == pass){
                        if(admin == true)
                        {
                            registerEntryTime(user)
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            registerEntryTime(user)
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
        btn2.setOnClickListener{
            val user: String = user.text.toString()
            val pass: String = pass.text.toString()
            val userDocRef =  userRef.document(user)
            userDocRef.get().addOnSuccessListener { documentSnaphot ->
                if (documentSnaphot.exists()){
                    val passBd = documentSnaphot.getString("password")
                    val admin = documentSnaphot.getBoolean("Admin")
                    val singing = documentSnaphot.getBoolean("fichado")
                    if(passBd == pass){
                        if(admin == true)
                        {
                            registerLeavingTime(user)
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            registerLeavingTime(user)
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
    private fun registerEntryTime(user : String){
        val userDocRef =  userRef.document(user)
        val singingColl = db.collection("fichajes")

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val singingRef = singingColl.document(currentDate).collection("usuarios").document(user)

        singingRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val existData = documentSnapshot.data
                if(existData != null){
                    existData["fichaje_entrada"] = currentTime
                    singingRef.set(existData)
                } else {
                    val signTime = hashMapOf("fichaje_entrada" to currentTime)
                    singingRef.set(signTime)
                }
            }
        userDocRef.update("fichado",true)
    }
    private fun registerLeavingTime(user : String){
        val userDocRef =  userRef.document(user)
        val singingColl = db.collection("fichajes")

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val singingRef = singingColl.document(currentDate).collection("usuarios").document(user)
        singingRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val existData = documentSnapshot.data
                if(existData != null){
                    existData["fichaje_salida"] = currentTime
                    singingRef.set(existData)
                }
            }
        userDocRef.update("fichado",false)
    }
}