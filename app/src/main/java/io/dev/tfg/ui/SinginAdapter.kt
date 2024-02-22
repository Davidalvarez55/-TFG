package io.dev.tfg.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.dev.tfg.R
import io.dev.tfg.classes.Singing
import java.text.SimpleDateFormat
import java.util.*

class SinginAdapter(private val context: Context, private val users: List<Singing>) : BaseAdapter(){
    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(p: Int): Any {
       return users[p]
    }

    override fun getItemId(p: Int): Long {
        return p.toLong()
    }

    override fun getView(p: Int, covertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder: ViewHolder
        if(covertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_singing_item,parent,false)
            holder = ViewHolder(view)
            view.tag = holder
        }else {
            view = covertView
            holder = view.tag as ViewHolder
        }

        val user = users[p]
        holder.user.text = user.user
        holder.singHour.text = user.singHour
        holder.leaveHour.text = user.leavingHour
        holder.totalHour.text = user.totalHour
        holder.note.text = user.note

        if(!moreThan8Hour(user.totalHour))
        {
            holder.totalHour.setTextColor(Color.RED)
        }
        else if(!lessThan8_30hour(user.totalHour)){
            holder.totalHour.setTextColor(Color.BLUE)
        }
        else{
            holder.totalHour.setTextColor(Color.GREEN)
        }
        return view
    }
    private class ViewHolder(view: View) {
        val user: TextView = view.findViewById(R.id.user)
        val singHour: TextView = view.findViewById(R.id.singingHour)
        val leaveHour: TextView = view.findViewById(R.id.leavingHour)
        val totalHour: TextView = view.findViewById(R.id.totalHour)
        val note: TextView = view.findViewById(R.id.note)
    }

    private fun moreThan8Hour(totalHour : String): Boolean{
    try{
        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
        val totalHourTime = time.parse(totalHour)
        val calendar = Calendar.getInstance()
        calendar.time = totalHourTime
        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val mins = calendar.get(Calendar.MINUTE)

        val totalMins = hours * 60 + mins
        return (totalMins >= (8*60))
    } catch(e: Exception){
        e.printStackTrace()
        return false
    }
    }
    private fun lessThan8_30hour(totalHour : String): Boolean{
        try{
            val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            val totalHourTime = time.parse(totalHour)
            val calendar = Calendar.getInstance()
            calendar.time = totalHourTime
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val mins = calendar.get(Calendar.MINUTE)

            val totalMins = hours * 60 + mins
            return (totalMins <= (8*60+30))
        } catch(e: Exception){
            e.printStackTrace()
            return false
        }
    }
}