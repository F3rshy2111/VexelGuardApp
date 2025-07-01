package com.example.iniciosesion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Dentro de tu archivo CustomAdapter.kt o en un archivo separado
interface OnItemClickListener {
    fun onItemClick(documentId: String)
}

class CustomAdapter (
    private val fechas: MutableList<String>,
    private val metodos: MutableList<String>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemFecha: TextView
        //var itemEntrada: TextView
        //var itemSalida: TextView

        init{
            itemImage = itemView.findViewById(R.id.card_item_image)
            itemFecha = itemView.findViewById(R.id.card_item_fecha)
            //itemEntrada = itemView.findViewById(R.id.card_item_entrada)
            //itemSalida = itemView.findViewById(R.id.card_item_salida)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        Log.d("PruebasCA","BindView")
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_layout, viewGroup,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        //Log.d("PruebasCA","Size")
        return fechas.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        Log.d("PruebasCA","BindView")
        viewHolder.itemFecha.text = fechas[i]
        if(metodos[i] == "QR"){
            viewHolder.itemImage.setImageResource(R.drawable.qr_code_card)
        } else if(metodos[i] == "fail"){
            viewHolder.itemImage.setImageResource(R.drawable.sad_face_icon)
        } else{
            viewHolder.itemImage.setImageResource(R.drawable.face_card)
        }

        viewHolder.itemView.setOnClickListener {
            // Cuando se hace clic en un elemento, llama al metodo onItemClick del listener
            // Pasa el ID del documento
            listener.onItemClick(fechas[i])
        }

    }

}