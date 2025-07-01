package com.example.iniciosesion

import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class historial_accesos : navDrawer(), OnItemClickListener {
    val fechas: MutableList<String> = mutableListOf()
    val metodos: MutableList<String> = mutableListOf()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var customAdapter: CustomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerUsers: Spinner
    private lateinit var adapterSpiner: ArrayAdapter<String>
    private lateinit var nombresMap: Map<String,String>
    private lateinit var rlSpinner: RelativeLayout
    private lateinit var nombre : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsManager = SharedPreferencesManager(this)
        val userId = prefsManager.getUserId()
        val roleManager = roleManager(this)
        val db = Firebase.firestore

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        nombre = ""
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        customAdapter = CustomAdapter(fechas, metodos, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter

        spinnerUsers = findViewById(R.id.spinnerUsuarios)

        adapterSpiner = ArrayAdapter(this, R.layout.spinner_selected_item, mutableListOf())
        adapterSpiner.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerUsers.adapter = adapterSpiner

        if (userId != null) {
            obtenerIdDoc(userId, customAdapter)
        }

        rlSpinner = findViewById(R.id.rl_historial)

        //Spinnner
        if (roleManager.isAdmin()) {
            nombresUsuarios(adapterSpiner)
            spinnerUsers.visibility = Spinner.VISIBLE

            spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedUserName = parent?.getItemAtPosition(position).toString()
                    val selectedUserId = nombresMap[selectedUserName] // Obtener el ID del usuario
                    Log.d("SpinnerSelection", "Usuario seleccionado: $selectedUserName (ID: $selectedUserId)")

                    // Aquí es donde harías la nueva solicitud a la base de datos
                    // por ejemplo, para cargar los accesos de este usuario seleccionado
                    if (selectedUserId != null) {
                        obtenerIdDoc(selectedUserId, customAdapter)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No hacer nada si no hay nada seleccionado (puede que no sea necesario en tu caso)
                }
            }
        } else {
            rlSpinner.visibility = Spinner.GONE
            if (userId != null) {
                obtenerIdDoc(userId, customAdapter)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            // Aquí haces lo que quieras al refrescar
            refrescarContenido()
        }
    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_historial_accesos // Asegúrate de tener este layout
    }

    // 3. Implementa el metodo onItemClick de la interfaz
    override fun onItemClick(documentId: String) {
        val roleManager = roleManager(this)
        Log.d("HistorialAccesos", "Se hizo clic en el documento con ID: $documentId")

        val intent = Intent(this, acceso_especifico::class.java)
        intent.putExtra("documentId", documentId)
        if(roleManager.isAdmin()){
            intent.putExtra("nombreUsuario", spinnerUsers.selectedItem.toString())
        } else{
            intent.putExtra("nombreUsuario", nombre)
        }

        startActivity(intent)
    }

    fun obtenerIdDoc(userId:String, adapter: CustomAdapter){
        val db = Firebase.firestore

        db.collection("usuarios").document(userId).get().addOnSuccessListener {
                documentSnapshot: DocumentSnapshot ->
            if(documentSnapshot.exists()){
                nombre = documentSnapshot.getString("nombre").toString()
                val doc_historialAcc = documentSnapshot.getString("historial_accesos")
                if (doc_historialAcc != null) {
                    obtenerAccesos(doc_historialAcc,adapter)
                } else {
                    fechas.clear()
                    metodos.clear()
                    fechas.add("No hay registros existentes")
                    metodos.add("fail")
                    adapter.notifyDataSetChanged()
                }
            } else {
                //Log.d("Firestore", "No existe tal documento!")
                val texto = "No exixste tal documento"
                Log.d("ErrorPrueba",texto)
            }
        }.addOnFailureListener {
            //Toast.makeText(this, "Error al generar el obtener documento", Toast.LENGTH_SHORT).show()
        }
    }

    fun obtenerAccesos(doc: String, adapter: CustomAdapter){
        val db = Firebase.firestore

        db.collection("Control_Asistencia").document(doc).collection("Accesos").get().addOnCompleteListener {
                task ->
            run {
                if (task.isSuccessful()) {
                    fechas.clear()
                    metodos.clear()

                    for (document in task.result) {
                        val metodo = document.get("metodo")
                        val fecha = document.id

                        if(fecha != "01-01-36"){
                            fechas.add(fecha.toString())
                            metodos.add(metodo.toString())
                        }
                    }

                    adapter.notifyDataSetChanged()
                }
                else {
                    Log.w("Firestore", "Error al obtener documentos.", task.getException());
                }
            }
        }
        return
    }

    fun nombresUsuarios(adapter: ArrayAdapter<String>){
        val db = Firebase.firestore
        val nombres: MutableMap<String, String> = mutableMapOf()
        val sharedPreferences = SharedPreferencesManager(this)
        val idUserActual = sharedPreferences.getUserId()

        db.collection("usuarios").orderBy("nombre",Query.Direction.ASCENDING).get().addOnSuccessListener {
            task ->
                run {
                    if (!task.isEmpty) {
                        for(document in task.documents){
                            val nombre = document.getString("nombre").toString()
                            val userId = document.id
                            nombres.put(nombre,userId)
                            if(userId == idUserActual){
                                val defaultposition = nombres.size
                                spinnerUsers.setSelection(defaultposition-1)
                            }
                        }
                        nombresMap = nombres
                        // Update the adapter's data and notify it
                        adapter.clear() // Clear existing items
                        adapter.addAll(nombres.keys.toList()) // Add new items
                        adapter.notifyDataSetChanged() // Notify the adapter that data has changed
                    }
                }
        }
    }

    private fun refrescarContenido() {
        // Simula una carga de datos (por ejemplo desde red)
        Handler(Looper.getMainLooper()).postDelayed({
            // Actualiza tus datos aquí
            val roleManager = roleManager(this)
            val preferencesManager = SharedPreferencesManager(this)
            val userId = preferencesManager.getUserId()

            if(!roleManager.isAdmin()){
                rlSpinner.visibility = Spinner.GONE
                if (userId != null) {
                    obtenerIdDoc(userId, customAdapter)
                }
            }

            Toast.makeText(this, "Contenido actualizado", Toast.LENGTH_SHORT).show()

            // Finaliza el spinner de refresco
            swipeRefreshLayout.isRefreshing = false
        }, 2000) // Espera de 2 segundos simulada
    }
}

