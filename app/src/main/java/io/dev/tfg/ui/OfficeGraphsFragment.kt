package io.dev.tfg.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.dev.tfg.R
import io.dev.tfg.classes.Singing
import java.text.SimpleDateFormat
import java.util.*

class OfficeGraphsFragment : Fragment() {
    private lateinit var search : Button
    private lateinit var adapter: SinginAdapter
    private lateinit var list: ListView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_office_graphs, container, false)

        list = rootView.findViewById(R.id.list)
        search = rootView.findViewById(R.id.search)
        val startDate = rootView.findViewById<AutoCompleteTextView>(R.id.startDate)
        val endDate = rootView.findViewById<AutoCompleteTextView>(R.id.endDate)

        showDatePicker(startDate)
        showDatePicker(endDate)

        search.setOnClickListener{
            val start = startDate.text.toString()
            val end = endDate.text.toString()
            println(start)
            println(end)
            val singingList = mutableListOf<Singing>()
            val query : Query = db
                .collection("fichajes")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(),start)
                .whereLessThanOrEqualTo(FieldPath.documentId(),end)
            query.get().addOnSuccessListener { documents ->
                for (document in documents) {

                    val query2 : Query = db
                        .collection("fichajes")
                        .document(document.id)
                        .collection("usuarios")

                    query2.get().addOnSuccessListener { user ->
                        val singingListDoc = user.map { userDoc ->
                            val user = userDoc.id
                            println(user)
                            val singHour = userDoc.getString("fichaje_entrada") ?: ""
                            val leavingHour = userDoc.getString("fichaje_salida") ?: ""
                            val note = userDoc.getString("nota") ?: ""
                            val totalTime = calculateDiff(singHour, leavingHour)
                            Singing(user, singHour, leavingHour, totalTime, note)

                        }
                        singingList.addAll(singingListDoc)

                        adapter = SinginAdapter(requireContext(),singingList)
                        list.adapter = adapter
                    }
                }
            }
        }


        return rootView
    }

    private fun showDatePicker(date: AutoCompleteTextView){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selecDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(selectedYear-1900, selectedMonth, selectedDay)
                )
                date.setText(selecDate)
            },
            year,
            month,
            day
        )
        date.setOnClickListener {
            datePickerDialog.show()
        }
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