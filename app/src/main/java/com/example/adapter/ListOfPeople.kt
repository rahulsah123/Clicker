package com.example.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.clicker.R
import com.example.model.ListOfPeopleData

public class ListOfPeople(context: Context, clicks_data:ArrayList<ListOfPeopleData>): RecyclerView.Adapter<ListOfPeople.ViewHolder>(){
    var context:Context=context
    var clicks_data:ArrayList<ListOfPeopleData> = clicks_data

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int):ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listofpersonwithscore, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
      return  clicks_data.size
    }

    override fun onBindViewHolder(holder:ViewHolder, p1: Int) {
        var singlecickdata = clicks_data[p1]
        val id=p1+1
        val name = singlecickdata.name
        val score = singlecickdata.score
        val datetime = singlecickdata.datetime
        holder.id.setText(id.toString())
        holder.name.setText(name.toString())
        holder.score.setText(score.toString())
        holder.datetime.setText(datetime.toString())
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        lateinit var id:TextView
        lateinit var name:TextView
        lateinit var score:TextView
        lateinit var datetime:TextView
        init {
            id=itemView.findViewById(R.id.id)
            name=itemView.findViewById(R.id.name)
            score=itemView.findViewById(R.id.score)
            datetime=itemView.findViewById(R.id.datetime)
        }
    }

}