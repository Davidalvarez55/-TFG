package io.dev.tfg.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.dev.tfg.R
import java.text.SimpleDateFormat
import java.util.*

class ModifyDbFragment : Fragment() {

    private lateinit var user: EditText
    private lateinit var singHourText: EditText
    private lateinit var leaveHourText: EditText
    private lateinit var search: Button
    private lateinit var change: Button
    private lateinit var notes: Spinner
    private lateinit var note: String


    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_modify_db, container, false)

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        user = rootView.findViewById(R.id.user)
        singHourText = rootView.findViewById(R.id.singHour)
        leaveHourText = rootView.findViewById(R.id.leaveHour)
        search = rootView.findViewById(R.id.search)
        change = rootView.findViewById(R.id.change)
        notes = rootView.findViewById(R.id.spinner)
        val options = resources.getStringArray(R.array.notes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

        notes.adapter =  adapter

        search.setOnClickListener {
            val userId = user.text.toString()
            val documentUserRef = db
                .collection("fichajes")
                .document(todayDate)
                .collection("usuarios")
                .document(userId)
            documentUserRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if(documentSnapshot.exists()){
                        val singHour = documentSnapshot.getString("fichaje_entrada")
                        val leaveHour = documentSnapshot.getString("fichaje_salida")
                        val note = documentSnapshot.getString("nota")

                        singHourText.setText(singHour)
                        leaveHourText.setText(leaveHour)

                        singHourText.visibility = View.VISIBLE
                        leaveHourText.visibility = View.VISIBLE

                        singHourText.isEnabled = true
                        leaveHourText.isEnabled = true

                        search.visibility = View.INVISIBLE
                        search.isClickable = false

                        change.visibility = View.VISIBLE
                        change.isClickable = true

                        val index : Int = if(note != null){
                            options.indexOf(note)
                        }else{
                            0
                        }
                        notes.visibility = View.VISIBLE
                        notes.isClickable = true
                        notes.setSelection(index)
                    }
                }
        }
        notes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                note = options[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        change.setOnClickListener{
            val userId = user.text.toString()
            val singHourNew = singHourText.text.toString()
            val leaveHourNew = leaveHourText.text.toString()
            val update = hashMapOf<String, Any>(
                "fichaje_entrada" to singHourNew,
                "fichaje_salida" to leaveHourNew,
                "modificado" to true,
                "nota" to note
            )
            db.collection("fichajes")
                .document(todayDate)
                .collection("usuarios")
                .document(userId)
                .update(update)
                .addOnSuccessListener {
                    Toast.makeText(context, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                }
            user.setText("")
            singHourText.setText("")
            leaveHourText.setText("")

            singHourText.visibility = View.INVISIBLE
            leaveHourText.visibility = View.INVISIBLE

            singHourText.isEnabled = false
            leaveHourText.isEnabled = false

            search.visibility = View.VISIBLE
            search.isClickable = true

            change.visibility = View.INVISIBLE
            change.isClickable = false

        }
        return rootView
    }
}