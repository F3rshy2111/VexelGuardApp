package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.key
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iniciosesion.com.example.iniciosesion.CustomAdapterEditUser
import com.example.iniciosesion.com.example.iniciosesion.CustomAdapterSpecific
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class editar_usuarios : navDrawer(), OnItemClickListener  {
    val nombres : MutableList<String> = mutableListOf()
    private lateinit var customAdapter: CustomAdapterEditUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerArea: Spinner
    private lateinit var areasAdapter: ArrayAdapter<String>
    val db = Firebase.firestore
    private val areasList : MutableList<String>  = mutableListOf()
    private val usersMap : MutableMap<String, String>  = mutableMapOf()
    private val areasMap : MutableMap<String, String>  = mutableMapOf()
    private val tiposMap : MutableMap<String, String>  = mutableMapOf()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        areasList.add("Todas")

        //Spinner para seleccionar tipo de usuario
        spinnerTipo = findViewById(R.id.spinnerTipo)
        val tiposArray = arrayOf("Todos", "Administrador", "Invitado", "Regular")
        val tiposAdapter = ArrayAdapter(this, R.layout.spinner_selected_item, tiposArray)
        tiposAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo.adapter = tiposAdapter

        //Spinner para seleccionar el area a la que pertenece
        spinnerArea = findViewById(R.id.spinnerArea)
        areasAdapter = ArrayAdapter(this, R.layout.spinner_selected_item, areasList)
        areasAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerArea.adapter = areasAdapter


        recyclerView = findViewById(R.id.recyclerViewEditUser)
        customAdapter = CustomAdapterEditUser(nombres, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter

        db.collection("usuarios").orderBy("nombre",Query.Direction.ASCENDING)
            .get().addOnSuccessListener { result ->
                areasMap.clear()
                tiposMap.clear()
                nombres.clear() // Limpia la lista antes de añadir nuevos datos
                areasList.clear()
                usersMap.clear()
                areasList.add("Todas")
                for (document in result) {
                    // Intenta convertir el documento a tu data class
                    val nombre = document.getString("nombre")
                    val area = document.getString("area")
                    val id = document.id
                    val tipo = document.getString("tipo")

                    //Log.d("Espacios","${id} pertenece a ${area}//")

                    if (nombre != null) {
                        nombres.add(nombre)
                        usersMap.put(id, nombre)
                    }

                    if(area != null){
                        if(!areasList.contains(area)){
                            areasList.add(area)
                        }
                        areasMap.put(id, area)
                    }

                    if(tipo != null){
                        tiposMap.put(id, tipo)
                    }
                }
                areasAdapter.notifyDataSetChanged()
                customAdapter.notifyDataSetChanged()
        }
            .addOnFailureListener { exception ->
                Log.d("FirestoreData", "Error al obtener documentos: ", exception)
            }


        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showByFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showByFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_editar_usuarios // Asegúrate de tener este layout
    }

    private fun showByFilters(){
        val tipoSpinText = spinnerTipo.selectedItem.toString()
        val areaSpinText = spinnerArea.selectedItem.toString()
        val usuariosXTipo : List<String>
        val usuariosXArea : List<String>
        nombres.clear()
        if(tipoSpinText != "Todos"){
            usuariosXTipo = getIdsByType(tiposMap,tipoSpinText)
        } else {
            var lista = mutableListOf<String>()
            usersMap.forEach{user ->
                val id = user.key
                lista.add(id)
            }
            usuariosXTipo = lista
        }

        if(areaSpinText != "Todas"){
            usuariosXArea = getIdsByArea(areasMap,areaSpinText)
        } else {
            var lista = mutableListOf<String>()
            usersMap.forEach{user ->
                val id = user.key
                lista.add(id)
            }
            usuariosXArea = lista
        }

        var mayor : List<String>
        var menor : List<String>
        if(usuariosXArea.size > usuariosXTipo.size){
            mayor = usuariosXArea
            menor = usuariosXTipo
        } else {
            mayor = usuariosXTipo
            menor = usuariosXArea
        }

        for(i in mayor){
            if(menor.contains(i)){
                val name = usersMap[i]
                if (name != null) {
                    nombres.add(name)
                }
            }
        }

        if(nombres.isNullOrEmpty()){
            nombres.add("No existen usuarios con estas caracteristicas")
        }

        customAdapter.notifyDataSetChanged()
    }

    fun getIdsByType(usuariosMap: Map<String, String>, type: String): List<String> {
        return usuariosMap.filter { it.value == type } // Filtra las entradas que coinciden con el tipo de usuario
            .map { it.key } // Mapea las entradas filtradas para obtener solo las claves (IDs de usuario)
    }

    fun getIdsByArea(usuariosMap: Map<String, String>, area: String): List<String> {
        return usuariosMap.filter { it.value == area } // Filtra las entradas que coinciden con el tipo de usuario
            .map { it.key } // Mapea las entradas filtradas para obtener solo las claves (IDs de usuario)
    }

    override fun onItemClick(nombre: String) {
        val uid = usersMap.entries.find { it.value == nombre }?.key
        //Toast.makeText(this, "${uid}",Toast.LENGTH_SHORT).show()
        val area = areasMap.entries.find { it.key == uid }?.value
        //Toast.makeText(this, "${area}",Toast.LENGTH_SHORT).show()

        val intent = Intent(this, edit_specific_user::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("areaUser",area)
        startActivity(intent)

    }

}