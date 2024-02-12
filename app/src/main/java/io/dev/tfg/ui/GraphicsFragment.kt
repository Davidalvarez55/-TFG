package io.dev.tfg.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.dev.tfg.R
import io.dev.tfg.classes.Singing
import java.text.SimpleDateFormat
import java.util.*

class GraphicsFragment : Fragment() {

    private lateinit var list: ListView
    private lateinit var singingList: List<Singing>
    private lateinit var adapter: SinginAdapter
    private lateinit var confirmation : Button

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_graphics, container, false)

        list = rootView.findViewById(R.id.listView)
        confirmation = rootView.findViewById(R.id.confirmation)

        val todayDate =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val query : Query = db
            .collection("fichajes")
            .document(todayDate)
            .collection("usuarios")
        query.get()
            .addOnSuccessListener { documents ->
                singingList = documents.map {document->
                    val user = document.id
                    val singHour = document.getString("fichaje_entrada") ?: ""
                    val leavingHour = document.getString("fichaje_salida") ?: ""
                    val totalTime = calculateDiff(singHour,leavingHour)
                    Singing(user,singHour,leavingHour,totalTime)
                }
                adapter = SinginAdapter(requireContext(), singingList)
                list.adapter = adapter

            }
            .addOnFailureListener {  }
        confirmation.setOnClickListener{
            val confirm = hashMapOf(
                "confirmado" to true
            )
            db.collection("fichajes").document(todayDate)
                .set(confirm)
        }
        return rootView
    }
    private fun calculateDiff(singHour: String, leavingHour: String): String{
        try {
            val diff = SimpleDateFormat("HH:mm", Locale.getDefault())

            val singHour = diff.parse(singHour)
            val leaveHour = diff.parse(leavingHour)
            val diffTime = leaveHour.time - singHour.time
            val hour = diffTime / (60 * 60 * 1000)
            val minutes = (diffTime % (60 * 60 * 1000)) / (60 * 1000)

            return String.format(Locale.getDefault(), "%02d:%02d", hour, minutes)
        }catch(e: Exception){
            e.printStackTrace()
            return ""
        }
    }
}