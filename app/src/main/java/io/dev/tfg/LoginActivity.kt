package io.dev.tfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import io.dev.tfg.ui.AdminActivity
import io.dev.tfg.ui.MaterialActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

        btn.setOnClickListener {
            val user: String = user.text.toString()
            val pass: String = pass.text.toString()
            val userDocRef = userRef.document(user)
            userDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val passBd = documentSnapshot.getString("password")
                    val admin = documentSnapshot.getBoolean("Admin")
                    if (passBd == pass) {
                        if (admin == true) {
                            registerEntryTime(user)
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        } else {
                            lifecycleScope.launch {
                                if (entryTime(user)) {
                                    registerEntryTime(user)
                                    val intent =
                                        Intent(this@LoginActivity, MaterialActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    println("La diferencia de tiempo no está en el rango permitido")
                                }
                            }
                        }
                    }
                } else {
                    // Tratar caso donde el documento no existe
                }
            }
        }
            btn2.setOnClickListener {
                val user: String = user.text.toString()
                val pass: String = pass.text.toString()
                val userDocRef = userRef.document(user)
                userDocRef.get().addOnSuccessListener { documentSnaphot ->
                    if (documentSnaphot.exists()) {
                        val passBd = documentSnaphot.getString("password")
                        val admin = documentSnaphot.getBoolean("Admin")
                        if (passBd == pass) {
                            if (admin == true) {
                                registerLeavingTime(user)
                                val intent = Intent(this, AdminActivity::class.java)
                                startActivity(intent)
                            } else {
                                registerLeavingTime(user)
                                val intent = Intent(this, MaterialActivity::class.java)
                                startActivity(intent)

                            }
                        } else {
                        }
                    }
                }
                    .addOnFailureListener {}
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
    private suspend fun entryTime(user : String) : Boolean= withContext(Dispatchers.IO){
        val userDocRef =  userRef.document(user)
        try{
            val documentSnapshot = Tasks.await(userDocRef.get())
            if (documentSnapshot.exists()) {
                val storedTime = documentSnapshot.getString("hora_entrada")
                val currentTime = SimpleDateFormat("HH:mm", Locale("es","ES")).format(Date())

                val parsedStoreTime =
                    SimpleDateFormat("HH:mm", Locale.getDefault()).parse(storedTime)
                val parsedCurrentTime =
                    SimpleDateFormat("HH:mm", Locale.getDefault()).parse(currentTime)

                val diff = parsedStoreTime.time - parsedCurrentTime.time
                val diffInMins = TimeUnit.MILLISECONDS.toMinutes(diff)
                println(storedTime)
                println(currentTime)
                println(diffInMins)
                return@withContext (diffInMins in -10..10)
            }
            println("nose que pasa")
    } catch (e: Exception){
            println("excepcion no se que pasa $e")
        }
        return@withContext false
    }
}