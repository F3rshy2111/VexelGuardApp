package com.example.iniciosesion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private lateinit var tvNombre : TextView
private lateinit var tvCorreo : TextView
private lateinit var tvTipo : TextView
private lateinit var tvLunes : TextView
private lateinit var tvMartes : TextView
private lateinit var tvMiercoles : TextView
private lateinit var tvJueves : TextView
private lateinit var tvViernes : TextView
private lateinit var tvSabado : TextView
private lateinit var tvDomingo : TextView
private lateinit var tvArea : TextView
private lateinit var btnUpdatePswrd : Button

class info_usuario : navDrawer() {

    private val db = Firebase.firestore
    private val dias = listOf("lunes","martes","miércoles","jueves","viernes","sábado","domingo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsManager = SharedPreferencesManager(this)
        val obtenerPermisos = obtener_permisos(this)
        val userId = prefsManager.getUserId()
        //prefsManager.getPermisosUser()
        //obtenerPermisos.obtenerPermisosPorDia()
        obtenerPermisos.permisoConcedido()

        tvNombre = findViewById(R.id.infUs_nombre)
        tvCorreo = findViewById(R.id.infUs_correo)
        tvTipo = findViewById(R.id.infUs_tipo)
        tvArea = findViewById(R.id.infUs_area)
        tvLunes = findViewById(R.id.infUs_lunes)
        tvMartes = findViewById(R.id.infUs_martes)
        tvMiercoles = findViewById(R.id.infUs_miercoles)
        tvJueves = findViewById(R.id.infUs_jueves)
        tvViernes = findViewById(R.id.infUs_viernes)
        tvSabado = findViewById(R.id.infUs_sabado)
        tvDomingo = findViewById(R.id.infUs_domingo)
        btnUpdatePswrd = findViewById(R.id.btn_cambiar_cont)

        btnUpdatePswrd.setOnClickListener {
            val intencion1 = Intent(this, update_password::class.java)
            intencion1.putExtra("texto", "Por favor ingresa la nueva contraseña")
            startActivity(intencion1)
        }


        var texto = "";

        if (userId != null) {
            db.collection("usuarios").document(userId).get().addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {

                    // El documento existe, puedes obtener sus datos
                    val nombre = documentSnapshot.getString("nombre")
                    val tipo = documentSnapshot.getString("tipo") // O documentSnapshot.getData() para un Map
                    val correo = documentSnapshot.getString("correo")
                    val email = documentSnapshot.getString("email")
                    val area = documentSnapshot.getString("area")

                    texto = "${nombre} es del tipo ${tipo} y tiene el correo ${correo}";
                    Log.d("Exito", texto)

                    getPermisos(area.toString())
                    val showArea = area.toString()

                    if(email != null && correo == null){tvCorreo.setText("${email}")}
                    else{tvCorreo.setText("${correo}")}

                    tvNombre.setText(nombre)
                    tvTipo.setText("${tipo}")
                    tvArea.setText("${showArea.replaceFirstChar { it.uppercaseChar() }}")

                } else {
                    //Log.d("Firestore", "No existe tal documento!")
                    texto = "No existe tal documento"
                    Log.d("ErrorPrueba",texto)
                }
            }.addOnFailureListener { e: java.lang.Exception? ->
                Log.w("Firestore", "Error al obtener el documento", e)
            }
        }
    }


    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_info_usuario // Asegúrate de tener este layout
    }

    fun getPermisos(area:String){
        db.collection("Permisos").document(area).get().addOnSuccessListener {
            document ->
            run {
                if (document.exists()) {
                    var entrada = ""
                    var salida = ""

                    for(dia in dias){
                        val day =  document.get(dia)
                        if(day == null){
                            val viewDia = buscarDia(dia)
                            viewDia.visibility = View.GONE
                        } else{
                            if(day is List<*>){
                                val permisosDia = day as List<String>
                                entrada = permisosDia[0]
                                salida = permisosDia[1]
                                //Log.d("Pruebas Permisos", "${dia} - Entrada: ${entrada}")
                                //Log.d("Pruebas Permisos", "${dia} - Salida: ${salida}")
                            }
                            val viewDia = buscarDia(dia)
                            viewDia.setText("${dia.replaceFirstChar { it.uppercaseChar() }} - De ${entrada} a ${salida}")
                        }
                    }

                } else {
                    Log.d("Pruebas Permisos", "Campo no encontrado")
                }
            }
        }
    }

    fun buscarDia(dia:String): TextView {
        when(dia){
            "lunes" -> return tvLunes
            "martes" -> return tvMartes
            "miércoles" -> return tvMiercoles
            "jueves" -> return tvJueves
            "viernes" -> return tvViernes
            "sábado" -> return tvSabado
            "domingo" -> return tvDomingo
            else -> {
                Toast.makeText(this,"Error en los Permisos ${dia}", Toast.LENGTH_SHORT).show()
            }
        }
        return tvDomingo
    }

}