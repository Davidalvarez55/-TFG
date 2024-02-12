package io.dev.tfg.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import io.dev.tfg.R
import io.dev.tfg.classes.Singing

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
        //primer elemento
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

        return view
    }
    private class ViewHolder(view: View) {
        val user: TextView = view.findViewById(R.id.user)
        val singHour: TextView = view.findViewById(R.id.singingHour)
        val leaveHour: TextView = view.findViewById(R.id.leavingHour)
        val totalHour: TextView = view.findViewById(R.id.totalHour)
    }

}