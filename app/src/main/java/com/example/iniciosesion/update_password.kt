package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class update_password : navDrawer() {
    private lateinit var userName :TextView
    private lateinit var userMail :TextView
    private lateinit var currentPswrd : EditText
    private lateinit var newPswrd : EditText
    private lateinit var confirmPswrd : EditText
    private lateinit var btnUpdate : Button
    private lateinit var btnBack : Button
    private lateinit var text : TextView
    private lateinit var passwordRequirementsNew: TextView
    private lateinit var passwordRequirementsCheck: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        val db = Firebase.firestore
        val sharedPreferences = SharedPreferencesManager(this)
        val userId = sharedPreferences.getUserId()
        val newText = intent.getStringExtra("texto")

        super.onCreate(savedInstanceState)
        disableNavDrawerInteraction()

        userName = findViewById(R.id.updPswrd_nombre)
        userMail = findViewById(R.id.updPswrd_correo)
        currentPswrd = findViewById(R.id.updPswrd_actual)
        newPswrd = findViewById(R.id.updPswrd_nueva1)
        confirmPswrd = findViewById(R.id.updPswrd_nueva2)
        btnUpdate = findViewById(R.id.btnUpdatePswrd)
        btnBack = findViewById(R.id.btnBack)
        text = findViewById(R.id.updPswrd_txt)
        passwordRequirementsNew = findViewById(R.id.password_requirements_new)
        passwordRequirementsCheck = findViewById(R.id.password_requirements_check)


        if(!newText.isNullOrEmpty()){
            btnBack.visibility = Button.VISIBLE
            text.setText(newText)
        }

        if (userId != null) {
            db.collection("usuarios").document(userId).get().addOnSuccessListener {
                documentSnapshot ->
                run {
                    if (documentSnapshot.exists()) {
                        val nombre = documentSnapshot.getString("nombre")
                        val correo = documentSnapshot.getString("correo")
                        val email = documentSnapshot.getString("email")

                        if(email != null && correo == null){
                            userMail.setText(email)
                        } else {
                            userMail.setText(correo)
                        }

                        userName.setText(nombre)
                    }
                }
            }
        }

        btnUpdate.setOnClickListener {
            val actual = currentPswrd.text.toString()
            val nueva = newPswrd.text.toString()
            val confirmacion = confirmPswrd.text.toString()
            if(actual != "" && nueva != "" && confirmacion != ""){
                if(nueva == confirmacion && nueva != actual){
                    if (!esContraseñaSegura(nueva)) {
                        Toast.makeText(this, "La contraseña debe tener al menos una mayúscula, una minúscula, un número y un carácter especial.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    actualizarContraseña(actual, nueva, onSuccess = {
                        changeValidation()
                        Toast.makeText(this, "Contraseña cambiada exitosamente.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Portada::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    },
                        onFailure = { exception ->
                            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                            // Si el error es FirebaseAuthRecentLoginRequiredException, podrías mostrar un diálogo
                            // pidiendo la contraseña actual. Si ya lo estás haciendo con reautenticarYCambiarContrasena,
                            // entonces el error aquí podría ser "Contraseña actual incorrecta".
                        })
                } else if(nueva == actual) {
                    Toast.makeText(this, "La nueva contraseña no puede ser igual a la anterior", Toast.LENGTH_SHORT).show()
                }else
                 {
                    Toast.makeText(this, "Las contraseñas ingresadas no coinciden", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, info_usuario::class.java)
            startActivity(intent)
        }

        newPswrd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (esContraseñaSegura(password)) {
                    passwordRequirementsNew.visibility = View.GONE
                } else {
                    passwordRequirementsNew.visibility = View.VISIBLE
                }
            }
        })

        confirmPswrd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (esContraseñaSegura(password)) {
                    passwordRequirementsCheck.visibility = View.GONE
                } else {
                    passwordRequirementsCheck.visibility = View.VISIBLE
                }
            }
        })

    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_password
    }

    fun actualizarContraseña(actual:String, nueva:String, onSuccess: () -> Unit,
                             onFailure: (Exception) -> Unit){
        val auth = Firebase.auth
        val user: FirebaseUser? = auth.currentUser

        if (user != null && user.email != null) {
            // 1. Crear una credencial con la contraseña actual del usuario
            val credential = EmailAuthProvider.getCredential(user.email!!, actual)

            // 2. Reautenticar al usuario
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // 3. Si la reautenticación es exitosa, procede a cambiar la contraseña
                        user.updatePassword(nueva)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    onSuccess()
                                } else {
                                    onFailure(updateTask.exception ?: Exception("Error al cambiar la contraseña después de reautenticar."))
                                }
                            }
                    } else {
                        onFailure(reauthTask.exception ?: Exception("Error de reautenticación. Contraseña actual incorrecta o problema de red."))
                    }
                }
        } else {
            onFailure(Exception("No hay un usuario activo o el usuario no tiene email para reautenticar."))
        }
    }

    fun changeValidation(){
        val db = Firebase.firestore
        val sharedPreferences = SharedPreferencesManager(this)
        val userId = sharedPreferences.getUserId()
        val updates = mapOf("verificado" to true)

        if (userId != null) {
            db.collection("usuarios").document(userId).update(updates).addOnSuccessListener {
                Log.d("Firestore", "Usuario verifiado")
            }
        }
    }

    fun esContraseñaSegura(password: String): Boolean {
        val patron = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-={}:;\"'<>?,./]).{6,}\$")
        return patron.matches(password)
    }


}