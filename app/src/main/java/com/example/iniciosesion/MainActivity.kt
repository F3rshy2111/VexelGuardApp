package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// La clase MainActivity hereda de AppCompatActivity e implementa View.OnClickListener
class MainActivity : AppCompatActivity(), View.OnClickListener {

    // El metodo onCreate se llama cuando la actividad es creada por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el layout de la actividad desde activity_main.xml
        setContentView(R.layout.activity_main)

        // Declara e inicializa los botones usando findViewById.
        // 'val' se usa para variables de solo lectura (inmutables),
        // mientras que 'var' se usa para variables mutables.
        // En este caso, los botones son 'val' porque la referencia al botón no cambia.
        val btnPortada: Button = findViewById(R.id.btnPortada)
        val btnInicioSesion: Button = findViewById(R.id.btnInicioSesion)
        val btnMenu_lateral: Button = findViewById(R.id.btnMenu_lateral)
        val btnCiclo: Button = findViewById(R.id.btnCiclo)

        // Asigna el listener de clics (esta misma actividad) a cada botón.
        btnPortada.setOnClickListener(this)
        btnInicioSesion.setOnClickListener(this)
        btnMenu_lateral.setOnClickListener(this)
        btnCiclo.setOnClickListener(this)
    }

    // El metodo onClick se llama cuando se hace clic en una vista que tiene este listener.
    override fun onClick(v: View) {
        // Obtiene el ID de la vista que fue clicada.
        val id = v.id

        // Usa una expresión 'when' (similar a un switch en Java) para manejar los clics
        // basados en el ID del botón.
        when (id) {
            R.id.btnPortada -> {
                // Crea una nueva intención para iniciar la actividad 'Portada'.
                val intencion1 = Intent(this, Portada::class.java)
                startActivity(intencion1) // Inicia la actividad.
            }
            R.id.btnInicioSesion -> {
                // Descomenta las siguientes líneas para habilitar el inicio de la actividad Calculadora.
                 val intencion2 = Intent(this, inicio_sesion::class.java)
                 startActivity(intencion2)
            }
            R.id.btnMenu_lateral -> {
                // Descomenta las siguientes líneas para habilitar el inicio de la actividad Convertidor.
                 val intencion3 = Intent(this, navDrawer::class.java)
                 startActivity(intencion3)
            }
            R.id.btnCiclo -> {
                // Descomenta las siguientes líneas para habilitar el inicio de la actividad CicloVida.
                // val intencion4 = Intent(this, CicloVida::class.java)
                // startActivity(intencion4)
            }
        }
    }
}
