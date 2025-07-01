package com.example.iniciosesion.com.example.iniciosesion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iniciosesion.OnItemClickListener
import com.example.iniciosesion.R

class CustomAdapterEditUser (
    private val nombres: MutableList<String>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<CustomAdapterEditUser.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemNombre: TextView

        init {
            itemNombre = itemView.findViewById(R.id.editUser_card_item_nombre)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        Log.d("PruebasCA", "BindView")
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.edituser_card_layout, viewGroup, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        //Log.d("PruebasCA", "Size")
        return nombres.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        Log.d("PruebasCA", "BindView")
        viewHolder.itemNombre.text = nombres[i]

        viewHolder.itemView.setOnClickListener {
            // Cuando se hace clic en un elemento, llama al metodo onItemClick del listener
            // Pasa el ID del documento
            listener.onItemClick(nombres[i])
        }
    }
}
