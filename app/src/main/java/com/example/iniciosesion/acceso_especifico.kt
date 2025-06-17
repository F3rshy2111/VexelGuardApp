package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iniciosesion.com.example.iniciosesion.CustomAdapterSpecific
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class acceso_especifico() : navDrawer() {
    val registros : MutableList<String> = mutableListOf()
    val entSal : MutableList<String> = mutableListOf()
    private lateinit var customAdapter: CustomAdapterSpecific
    private lateinit var recyclerView: RecyclerView

    private val db = Firebase.firestore
    private lateinit var tvAccEsp : TextView
    private lateinit var tvUsuario : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val documentId = intent.getStringExtra("documentId")
        val nombreUsuario = intent.getStringExtra("nombreUsuario")

        tvAccEsp = findViewById(R.id.tvAccEsp)
        tvUsuario = findViewById(R.id.usuario_accEsp)
        tvAccEsp.setText("Accesos del ${documentId}")
        tvUsuario.setText("Usuario: ${nombreUsuario}")

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEsp)
        customAdapter = CustomAdapterSpecific(registros, entSal)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter

        db.collection("usuarios").whereEqualTo("nombre",nombreUsuario).get().addOnSuccessListener {
            result -> for(document in result){
                val userHistorial = document.getString("historial_accesos")
                    if (userHistorial != null) { if (documentId != null) {
                        getAccesoEspecifico(userHistorial, documentId,customAdapter)
                    }
            }
        }
        }

        val backBtn : Button = findViewById(R.id.btnRegresarAE)
        backBtn.setOnClickListener(this)
    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_acceso_especifico // Asegúrate de tener este layout
    }

    override fun onClick(v: View?) {
        val id = v?.id;

        when(id){
            R.id.btnRegresarAE ->{
                // Crea una nueva intención para iniciar la actividad 'Portada'.
                val intencion1 = Intent(this, historial_accesos::class.java)
                startActivity(intencion1) // Inicia la actividad.
            }
        }
    }

    fun getAccesoEspecifico(userPermisos: String, documentId:String, adapter: CustomAdapterSpecific){
        db.collection("Control_Asistencia").document(userPermisos).collection("Accesos")
            .document(documentId).get().addOnSuccessListener { document ->
                run {
                    if (document != null) {
                        val datos = document.data
                        mostrarDatos(datos,adapter)
                    }
                }
            }.addOnFailureListener{
                registros.clear()
                entSal.clear()
                registros.add("No hay registros existentes")
                entSal.add("fail")
                adapter.notifyDataSetChanged()
            }
    }

    fun mostrarDatos(datos:MutableMap<String, Any>?, adapter: CustomAdapterSpecific){
        if (datos != null) {
            registros.clear()
            entSal.clear()

            if(datos.get("num_registros") != null){
                val numReg = datos.get("num_registros").toString().toInt()
                Log.d("PruebasAE", "${numReg}")
                for(i in 1..numReg){
                    var texto = ""
                    var textEntSAl = ""
                    if((i%2)==0){
                        texto = "Salida: ${datos.get("registro_${i}")}"
                        textEntSAl = "salida"
                    } else{
                        texto = "Entrada: ${datos.get("registro_${i}")}"
                        textEntSAl = "entrada"
                    }

                    registros.add(texto)
                    entSal.add(textEntSAl)
                }
            }else {
                registros.clear()
                entSal.clear()
                registros.add("No hay registros existentes")
                entSal.add("fail")
            }

            adapter.notifyDataSetChanged()

        }

    }
}