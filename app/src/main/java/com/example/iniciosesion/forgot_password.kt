package com.example.iniciosesion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class forgot_password : AppCompatActivity() {
    private lateinit var email : EditText
    private lateinit var forgotBtn : Button
    private lateinit var loginBtn : Button
    private lateinit var vista : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        email = findViewById(R.id.forPassword_email_input)
        forgotBtn = findViewById(R.id.send_email_btn)
        loginBtn = findViewById(R.id.login_return_btn)
        vista = findViewById(R.id.main)

        vista.setOnClickListener{ hideKeyboard(vista) }

        forgotBtn.setOnClickListener {
            val correo = email.text.toString()
            if(correo != ""){
                recuperarContraseña(correo)
                forgotBtn.isEnabled = false
            } else {
                Toast.makeText(this, "Por favor, escribe tu correo electronico",Toast.LENGTH_SHORT).show()
            }
        }

        // Finalizar activity (finish()) al enviar el correo

        loginBtn.setOnClickListener {
            val intencion1 = Intent(this, inicio_sesion::class.java)
            startActivity(intencion1)
        }
    }

    private fun recuperarContraseña(correo:String){
        val auth = Firebase.auth

        auth.sendPasswordResetEmail(correo).addOnSuccessListener{
            Toast.makeText(this, "El correo para reestablecer la contraseña se ha enviado exitosamente", Toast.LENGTH_LONG).show()
            val intencion1 = Intent(this, inicio_sesion::class.java)
            startActivity(intencion1)
        }.addOnFailureListener{ Exception ->
            run {
                Toast.makeText(this, "Error: "+ Exception.message,Toast.LENGTH_LONG).show()
                forgotBtn.isEnabled = true
            }
        }
    }

    fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }
}