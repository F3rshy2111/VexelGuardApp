package com.example.iniciosesion.com.example.iniciosesion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iniciosesion.CustomAdapter
import com.example.iniciosesion.OnItemClickListener
import com.example.iniciosesion.R

class CustomAdapterSpecific (
    private val registros: MutableList<String>,
    private val entSal: MutableList<String>
): RecyclerView.Adapter<CustomAdapterSpecific.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView
        var itemRegistro: TextView

        init {
            itemImage = itemView.findViewById(R.id.specific_card_item_image)
            itemRegistro = itemView.findViewById(R.id.specific_card_item_entrada)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        Log.d("PruebasCA", "BindView")
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.specific_card_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        Log.d("PruebasCA", "Size")
        return registros.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        Log.d("PruebasCA", "BindView")
        viewHolder.itemRegistro.text = registros[i]
        if (entSal[i] == "entrada") {
            viewHolder.itemImage.setImageResource(R.drawable.entrada_icon)
        } else if (entSal[i] == "fail"){
            viewHolder.itemImage.setImageResource(R.drawable.sad_face_icon)
        } else {
            viewHolder.itemImage.setImageResource(R.drawable.salida_icon)
        }
    }
}
