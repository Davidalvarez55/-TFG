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
    private lateinit var office: Switch
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
        office = rootView.findViewById(R.id.officeSwitch)
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
        admin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                office.isChecked = false
            }
        }
        office.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                admin.isChecked = false
            }
        }

        buttonUser.setOnClickListener{
            addUserToFirebase()
            clearAll()
        }

        return rootView
    }

    private fun addUserToFirebase(){
        val user = user.text.toString()
        val sur = surname.text.toString()
        val sur2 = surname2.text.toString()
        val id = id.text.toString()
        val switchAdmin: Boolean = admin.isChecked
        val switchOffice: Boolean = office.isChecked
        val password = createPassword()
        val userName = documentIdentificator(user,sur,sur2)

        if(userName.isNotEmpty()){
            val pass = hashMapOf(
                "Primer Apellido" to sur,
                "Segundo Apellido" to sur2,
                "Nombre" to user,
                "DNI" to id,
                "password" to password,
                "Admin" to switchAdmin,
                "Oficina" to switchOffice,
                "fichado" to false,
                "hora_entrada" to hour
            )
            db.collection("Usuarios").document(userName)
                .set(pass)
                .addOnSuccessListener { confirmation.setText("usuario añadido correctamente") }
                .addOnFailureListener { confirmation.setText("usuario no añadido correctamente") }
        } else {
        }
    }

    private fun documentIdentificator(name : String,surname : String, surname2 : String) : String{
        val namet2 = name.take(2).toLowerCase()
        val surnamet2 = surname.take(2).toLowerCase()
        val surnamet22 = surname2.take(2).toLowerCase()
        return "$namet2$surnamet2$surnamet22"
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

    private fun clearAll() {
        user.text.clear()
        surname.text.clear()
        surname2.text.clear()
        id.text.clear()
        admin.isChecked = false
    }
}