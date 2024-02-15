package io.dev.tfg.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView
import com.google.firebase.firestore.FirebaseFirestore
import io.dev.tfg.R
import io.dev.tfg.classes.Singing
import java.security.SecureRandom

class AddUserFragment : Fragment() {
    private lateinit var user: EditText
    private lateinit var surname: EditText
    private lateinit var surname2: EditText
    private lateinit var id: EditText
    private lateinit var buttonUser: Button
    private lateinit var confirmation : TextView
    private lateinit var admin: Switch
    private lateinit var hours: Spinner
    private lateinit var hour : String

    private val db = FirebaseFirestore.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_user, container, false)

        user = rootView.findViewById(R.id.User)
        surname = rootView.findViewById(R.id.surname)
        surname2 = rootView.findViewById(R.id.surname2)
        id = rootView.findViewById(R.id.id)
        buttonUser = rootView.findViewById(R.id.UserButton)
        confirmation = rootView.findViewById(R.id.confirmation)
        admin = rootView.findViewById(R.id.adminSwitch)
        hours = rootView.findViewById(R.id.spinner)
        val options = resources.getStringArray(R.array.hours)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

        hours.adapter =  adapter
        hours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                hour = options[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        buttonUser.setOnClickListener{
            addUserToFirebase()
        }

        return rootView
    }

    private fun addUserToFirebase(){
        val userName = user.text.toString()
        val switchAdmin: Boolean = admin.isChecked
        val password = createPassword()

        if(userName.isNotEmpty()){
            val pass = hashMapOf(
                "Primer Apellido" to surname,
                "Segundo Apellido" to surname2,
                "Dni" to id,
                "password" to password,
                "Admin" to switchAdmin,
                "fichado" to false,
                "hora_entrada" to hour
            )
            db.collection("Usuarios").document(userName)
                .set(pass)
                .addOnSuccessListener { confirmation.setText("usuario añadido correctamente") }
                .addOnFailureListener { confirmation.setText("usuario no añadido correctamente") }
        } else {
            user.error = "Nombre de usuario no puede estar vacio"
        }
    }
    private fun createPassword(): String{
        val passLength = 6
        val passChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        val secureRandom =  SecureRandom()
        val pass = StringBuilder(passLength)

        repeat(passLength){
            val random = secureRandom.nextInt(passChars.length)
            pass.append(passChars[random])
        }
        return pass.toString()
    }
}