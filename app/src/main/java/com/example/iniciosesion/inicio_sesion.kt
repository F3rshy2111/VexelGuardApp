package com.example.iniciosesion

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class inicio_sesion : AppCompatActivity(), View.OnClickListener  {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var usernameInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var viewMain : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_sesion)

        auth = Firebase.auth
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        viewMain = findViewById(R.id.main)
        viewMain.setOnClickListener{ hideKeyboard(viewMain) }


        val loginBtn : Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener(this)

        val forgotBtn : Button = findViewById(R.id.forgot_psw_btn)
        forgotBtn.setOnClickListener {
            val intencion1 = Intent(this, forgot_password::class.java)
            startActivity(intencion1)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val preferencesManager = SharedPreferencesManager(this)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            rutinaLogin()
        } else {
            preferencesManager.clearAllUserData()
        }
    }

    override fun onClick(v: View?) {
        val nombre = usernameInput.text.toString()
        val contraseña = passwordInput.text.toString()

        hideKeyboard(viewMain)

        if(nombre != "" && contraseña != ""){
            iniciarSesion(nombre,contraseña)
        } else {
            Toast.makeText(this, "Porfavor completa todos los campos", Toast.LENGTH_SHORT).show()
        }


    }

    fun iniciarSesion(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            rutinaLogin()
        } else {
            Toast.makeText(this, "Datos Incorrectos, intentelo de nuevo", Toast.LENGTH_SHORT).show()
        }
    }

    fun rutinaLogin(){
        guardarUsuarioId()
        lifecycleScope.launch {
            guardarUserArea()
            guardarUsuarioTipo()

        }
    }

    fun guardarUsuarioId() {
        val user = Firebase.auth.currentUser
        user?.let {
            val uid = it.uid
            val prefsManager = SharedPreferencesManager(this) // 'this' es el contexto de la Activity
            prefsManager.saveUserId(uid)
            Log.d("SharedPreferences", "ID de usuario guardado: $uid")
        }
    }

    suspend fun guardarUserArea(){
        val preferencesManager = SharedPreferencesManager(this)
        val userId = preferencesManager.getUserId()

        if (userId == null) {
            Log.d("MyFirebaseHandler", "User ID no encontrado, no se puede guardar el área.")
            return
        }

        try {
            val document = db.collection("usuarios").document(userId).get().await()
            if (document.exists()) {
                val area = document.getString("area")
                if (area != null) {
                    preferencesManager.saveUserArea(area)
                    Log.d("SharedPreferences", "Área de usuario guardado: $area")
                } else {
                    Log.d("MyFirebaseHandler", "El campo 'area' es nulo en el documento del usuario.")
                }
            } else {
                Log.d("MyFirebaseHandler", "Documento de usuario no encontrado para ID: $userId")
            }
        } catch (e: Exception) {
            Log.e("MyFirebaseHandler", "Error al obtener el área del usuario: ${e.message}", e)
        }
    }

    suspend fun guardarUsuarioTipo() {
        val roleManager = roleManager(this)
        val preferencesManager = SharedPreferencesManager(this)
        val id = preferencesManager.getUserId()

        if (id == null) {
            Log.d("MyFirebaseHandler", "User ID no encontrado, no se puede guardar el tipo de usuario.")
            return
        }

        try {
            val document = db.collection("usuarios").document(id).get().await()
            if (document.exists()) {
                val tipo = document.getString("tipo")
                val verified = document.get("verificado") as Boolean
                Log.d("Pruebas",verified.toString())
                if (tipo != null) {
                    roleManager.saveUserRole(tipo)
                    Log.d("SharedPreferences", "Tipo de usuario guardado: $tipo")
                } else {
                    Log.d("MyFirebaseHandler", "El campo 'tipo' es nulo en el documento del usuario.")
                }

                if(tipo == "Administrador" || tipo == "Invitado"){
                    guardarTOTPMaster()
                }

                guardarPermisosUsuario(verified)
            } else {
                Log.d("MyFirebaseHandler", "Documento de usuario no encontrado para ID: $id")
            }
        } catch (e: Exception) {
            Log.e("MyFirebaseHandler", "Error al obtener el tipo de usuario: ${e.message}", e)
        }
    }

    private fun guardarTOTPMaster(){
        val preferencesManager = SharedPreferencesManager(this)
        val id = preferencesManager.getUserId()

        if (id != null) {
            db.collection("usuarios").document(id).get().addOnSuccessListener { document ->
                run {
                    if (document != null) {
                        val totp = document.getString("TOTP").toString()
                        preferencesManager.saveMasterTOTP(totp)
                        Log.d("OTP", preferencesManager.getMasterTOTP().toString())
                    }
                }
            }
        }
    }

    suspend fun guardarPermisosUsuario(verified: Boolean){
        val preferencesManager = SharedPreferencesManager(this)
        val area = preferencesManager.getUserArea()
        val roleManager = roleManager(this)
        val invitado = roleManager.isInvitado()
        val userId = preferencesManager.getUserId()

        if (area == null) {
            Log.d("MyFirebaseHandler", "Área de usuario no encontrada, no se pueden obtener los permisos.")
            return
        }

        try {
            var instancia = area?.let { db.collection("Permisos").document(it) }

            if(invitado){
                instancia = userId?.let {
                    db.collection("Permisos").document("invitados")
                        .collection("Invitados").document(it)
                }
            }

            val document = instancia?.get()?.await()
            if (document != null) {
                if (document.exists()) {
                    val lunes = document.get("lunes") as? ArrayList<*> ?: ArrayList<Any>()
                    val martes = document.get("martes") as? ArrayList<*> ?: ArrayList<Any>()
                    val miercoles = document.get("miércoles") as? ArrayList<*> ?: ArrayList<Any>()
                    val jueves = document.get("jueves") as? ArrayList<*> ?: ArrayList<Any>()
                    val viernes = document.get("viernes") as? ArrayList<*> ?: ArrayList<Any>()
                    val sabado = document.get("sábado") as? ArrayList<*> ?: ArrayList<Any>()
                    val domingo = document.get("domingo") as? ArrayList<*> ?: ArrayList<Any>()

                    Log.d("PruebasPermisos", "Lunes: $lunes")
                    Log.d("PruebasPermisos", "Martes: $martes")
                    Log.d("PruebasPermisos", "Miércoles: $miercoles")
                    Log.d("PruebasPermisos", "Jueves: $jueves")
                    Log.d("PruebasPermisos", "Viernes: $viernes")
                    Log.d("PruebasPermisos", "Sábado: $sabado")
                    Log.d("PruebasPermisos", "Domingo: $domingo")

                    preferencesManager.savePermisos(lunes, martes, miercoles, jueves, viernes, sabado, domingo)
                } else {
                    Log.d("MyFirebaseHandler", "Documento de permisos no encontrado para el área: $area")
                }
            }

            siguienteActivity(verified)
        } catch (e: Exception) {
            Log.e("MyFirebaseHandler", "Error al obtener los permisos del usuario: ${e.message}", e)
        }
    }

    fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun siguienteActivity(verificado: Boolean){
        if(!verificado){
            // Navegar a la Activity principal y finalizar la LoginActivity
            val intent = Intent(this, update_password::class.java)
            // Este flag es importante para que la LoginActivity no se quede en el back stack.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // Navegar a la Activity principal y finalizar la LoginActivity
            val intent = Intent(this, Portada::class.java)
            // Este flag es importante para que la LoginActivity no se quede en el back stack.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

}