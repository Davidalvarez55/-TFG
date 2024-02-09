package io.dev.tfg.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import io.dev.tfg.R

class AddUserFragment : Fragment() {
    private lateinit var user: EditText
    private lateinit var buttonUser: Button
    private lateinit var confirmation : TextView

    private val db = FirebaseFirestore.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_add_user, container, false)

        user = rootView.findViewById(R.id.User)
        buttonUser = rootView.findViewById(R.id.UserButton)
        confirmation = rootView.findViewById(R.id.confirmation)

        buttonUser.setOnClickListener{
            addUserToFirebase()
        }

        return rootView
    }

    private fun addUserToFirebase(){
        val userName = user.text.toString()
        val password = "1234567"

        if(userName.isNotEmpty()){
            val pass = hashMapOf("password" to password)
            db.collection("Usuarios").document(userName)
                .set(pass)
                .addOnSuccessListener { confirmation.setText("usuario añadido correctamente") }
                .addOnFailureListener { confirmation.setText("usuario no añadido correctamente") }
        } else {
            user.error = "Nombre de usuario no puede estar vacio"
        }
    }
}