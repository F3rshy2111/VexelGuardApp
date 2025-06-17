package com.example.iniciosesion

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.example.iniciosesion.com.example.iniciosesion.DocumentChecker
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executor

class editar_areas : navDrawer() {
    private val calendar = Calendar.getInstance()
    private lateinit var titulo :TextView
    private lateinit var etHoraEntLunes : EditText
    private lateinit var etHoraEntMartes : EditText
    private lateinit var etHoraEntMiercoles : EditText
    private lateinit var etHoraEntJueves : EditText
    private lateinit var etHoraEntViernes : EditText
    private lateinit var etHoraEntSabado : EditText
    private lateinit var etHoraEntDomingo : EditText
    private lateinit var etHoraSalidaLunes : EditText
    private lateinit var etHoraSalidaMartes : EditText
    private lateinit var etHoraSalidaMiercoles : EditText
    private lateinit var etHoraSalidaJueves : EditText
    private lateinit var etHoraSalidaViernes : EditText
    private lateinit var etHoraSalidaSabado : EditText
    private lateinit var etHoraSalidaDomingo : EditText
    private lateinit var cbLunes : CheckBox
    private lateinit var cbMartes : CheckBox
    private lateinit var cbMiercoles : CheckBox
    private lateinit var cbJueves : CheckBox
    private lateinit var cbViernes : CheckBox
    private lateinit var cbSabado : CheckBox
    private lateinit var cbDomingo : CheckBox
    private lateinit var vista: View
    private lateinit var spinArea: Spinner
    private lateinit var btnEditar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var rlEditar : RelativeLayout
    private lateinit var rlSaveCancel : RelativeLayout
    private val opcionesList =ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var updated: Any

    private lateinit var showLunes : Array<*>
    private lateinit var showMartes : Array<*>
    private lateinit var showMiercoles: Array<*>
    private lateinit var showJueves : Array<*>
    private lateinit var showViernes : Array<*>
    private lateinit var showSabado : Array<*>
    private lateinit var showDomingo : Array<*>
    private lateinit var showSemana : Array<*>
    private var gruposMap : MutableMap<String, Map<String,Any>?> = mutableMapOf()

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titulo = findViewById(R.id.edAreas_Titulos)
        cbLunes = findViewById(R.id.edAreas_Rbtn_Lunes)
        cbMartes = findViewById(R.id.edAreas_Rbtn_Martes)
        cbMiercoles = findViewById(R.id.edAreas_Rbtn_Miercoles)
        cbJueves = findViewById(R.id.edAreas_Rbtn_Jueves)
        cbViernes = findViewById(R.id.edAreas_Rbtn_Viernes)
        cbSabado = findViewById(R.id.edAreas_Rbtn_Sabado)
        cbDomingo = findViewById(R.id.edAreas_Rbtn_Domingo)
        spinArea = findViewById(R.id.edAreas_Spinner)
        etHoraEntLunes = findViewById(R.id.edAreas_etEnt_Lunes)
        etHoraEntMartes = findViewById(R.id.edAreas_etEnt_Martes)
        etHoraEntMiercoles = findViewById(R.id.edAreas_etEnt_Miercoles)
        etHoraEntJueves = findViewById(R.id.edAreas_etEnt_Jueves)
        etHoraEntViernes = findViewById(R.id.edAreas_etEnt_Viernes)
        etHoraEntSabado = findViewById(R.id.edAreas_etEnt_Sabado)
        etHoraEntDomingo = findViewById(R.id.edAreas_etEnt_Domingo)
        etHoraSalidaLunes = findViewById(R.id.edAreas_etSal_Lunes)
        etHoraSalidaMartes = findViewById(R.id.edAreas_etSal_Martes)
        etHoraSalidaMiercoles = findViewById(R.id.edAreas_etSal_Miercoles)
        etHoraSalidaJueves = findViewById(R.id.edAreas_etSal_Jueves)
        etHoraSalidaViernes = findViewById(R.id.edAreas_etSal_Viernes)
        etHoraSalidaSabado = findViewById(R.id.edAreas_etSal_Sabado)
        etHoraSalidaDomingo = findViewById(R.id.edAreas_etSal_Domingo)
        vista = findViewById(R.id.edAreas_constrait)
        rlEditar = findViewById(R.id.edAreas_RL_Edit)
        rlSaveCancel = findViewById(R.id.edAreas_RL_SaveCancel)
        btnEditar = findViewById(R.id.edAreas_button)
        btnGuardar = findViewById(R.id.edAreas_Guardar_button)
        btnCancelar = findViewById(R.id.edAreas_Cancelar_button)

        etHoraEntLunes.setOnClickListener{ showTimePickerDialog(etHoraEntLunes) }
        etHoraEntMartes.setOnClickListener{ showTimePickerDialog(etHoraEntMartes) }
        etHoraEntMiercoles.setOnClickListener{ showTimePickerDialog(etHoraEntMiercoles) }
        etHoraEntJueves.setOnClickListener{ showTimePickerDialog(etHoraEntJueves) }
        etHoraEntViernes.setOnClickListener{ showTimePickerDialog(etHoraEntViernes) }
        etHoraEntSabado.setOnClickListener{ showTimePickerDialog(etHoraEntSabado) }
        etHoraEntDomingo.setOnClickListener{ showTimePickerDialog(etHoraEntDomingo) }
        etHoraSalidaLunes.setOnClickListener{ showTimePickerDialog(etHoraSalidaLunes) }
        etHoraSalidaMartes.setOnClickListener{ showTimePickerDialog(etHoraSalidaMartes) }
        etHoraSalidaMiercoles.setOnClickListener{ showTimePickerDialog(etHoraSalidaMiercoles) }
        etHoraSalidaJueves.setOnClickListener{ showTimePickerDialog(etHoraSalidaJueves) }
        etHoraSalidaViernes.setOnClickListener{ showTimePickerDialog(etHoraSalidaViernes) }
        etHoraSalidaSabado.setOnClickListener{ showTimePickerDialog(etHoraSalidaSabado) }
        etHoraSalidaDomingo.setOnClickListener{ showTimePickerDialog(etHoraSalidaDomingo) }

        showLunes = arrayOf(cbLunes, etHoraEntLunes,etHoraSalidaLunes)
        showMartes = arrayOf(cbMartes,etHoraEntMartes,etHoraSalidaMartes)
        showMiercoles = arrayOf(cbMiercoles,etHoraEntMiercoles,etHoraSalidaMiercoles)
        showJueves = arrayOf(cbJueves, etHoraEntJueves, etHoraSalidaJueves)
        showViernes = arrayOf(cbViernes, etHoraEntViernes, etHoraSalidaViernes)
        showSabado = arrayOf(cbSabado, etHoraEntSabado, etHoraSalidaSabado)
        showDomingo = arrayOf(cbDomingo, etHoraEntDomingo, etHoraSalidaDomingo)
        showSemana = arrayOf(showLunes,showMartes,showMiercoles, showJueves, showViernes, showSabado, showDomingo)

        var intentPasado = intent.getStringExtra("updated_area")
        if( intentPasado != null){
            updated = intentPasado
        } else{
            updated = ""
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val roleManager = roleManager(applicationContext)
                if(roleManager.isAdmin()){
                    updateArea(showSemana)
                } else {
                    Toast.makeText(applicationContext,"No eres administrador. ¿Qué haces aquí?", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verificación biométrica")
            .setSubtitle("Confirma tu identidad para continuar")
            .setNegativeButtonText("Cancelar")
            .build()

        adapter = ArrayAdapter(this, R.layout.spinner_selected_item, opcionesList)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinArea.setAdapter(adapter)
        obtenerGrupos(adapter)
        spinArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedArea = parent?.getItemAtPosition(position).toString()
                val areaDocs = gruposMap.getValue(selectedArea) // Obtener el ID del usuario
                if (areaDocs != null) {
                    showPermisos(areaDocs)
                }

                titulo.setText("Áreas")
                rlEditar.visibility = RelativeLayout.VISIBLE
                rlSaveCancel.visibility = RelativeLayout.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no hay nada seleccionado (puede que no sea necesario en tu caso)
            }
        }

        vista.setOnClickListener{ hideKeyboard(vista) }

        btnEditar.setOnClickListener{
            habilitarEdicion()
        }
        btnCancelar.setOnClickListener{
            val position = spinArea.selectedItemPosition
            val selectedArea = spinArea.getItemAtPosition(position).toString()
            val areaDocs = gruposMap.getValue(selectedArea) // Obtener el ID del usuario
            if (areaDocs != null) {
                showPermisos(areaDocs)
            }
            titulo.setText("Áreas")
            rlEditar.visibility = RelativeLayout.VISIBLE
            rlSaveCancel.visibility = RelativeLayout.GONE
        }
        btnGuardar.setOnClickListener {
            if (canAuthenticate()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "La autenticación biométrica no está disponible", Toast.LENGTH_LONG).show()
            }
        }

    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_editar_areas
    }

    private fun obtenerGrupos(adapter: ArrayAdapter<String>){
        val db = Firebase.firestore
        val grupos: MutableMap<String, Map<String,Any>?> = mutableMapOf()

        db.collection("Permisos").get().addOnSuccessListener {
                task ->
            run {
                if (!task.isEmpty) {
                    for(document in task.documents){
                        val datos = document.data
                        val area = document.id
                        grupos.put(area,datos)
                        if(updated == area){
                            spinArea.setSelection(grupos.size-1)
                        }
                    }
                    gruposMap = grupos
                    // Update the adapter's data and notify it
                    adapter.clear() // Clear existing items
                    adapter.addAll(grupos.keys.toList()) // Add new items
                    adapter.notifyDataSetChanged() // Notify the adapter that data has changed
                }
            }
        }
    }

    private fun showPermisos(permisos: Map<String,Any>){
        clearPermisos()

        permisos.forEach({ (key,value) ->
            val dia = obtenerDia(key)
            if(dia[0] is CheckBox){
                val checkBox = dia[0] as CheckBox
                checkBox.isChecked = true
                checkBox.isEnabled = false
            }
            if(value is List<*>){
                val permisosDia = value as List<String>
                if(dia[1] is EditText){
                    val entrada = dia[1] as EditText
                    entrada.setText(permisosDia[0])
                    entrada.isEnabled = false
                }
                if(dia[2] is EditText){
                    val salida = dia[2] as EditText
                    salida.setText(permisosDia[1])
                    salida.isEnabled = false
                }
            }
        })
    }

    private fun clearPermisos(){
        for(dia in showSemana){
            if (dia is Array<*>){
                for(i in dia){
                    if(i is CheckBox){
                        i.isChecked = false
                        i.isEnabled = false
                    }
                    if(i is EditText){
                        i.setText("00:00")
                        i.isEnabled = false
                    }
                }
            }
        }
    }

    private fun clearPermisos(permisos:Boolean){
        for(dia in showSemana){
            if (dia is Array<*>){
                for(i in dia){
                    if(i is CheckBox){
                        i.isChecked = false
                        i.isEnabled = true
                    }
                    if(i is EditText){
                        i.setText("00:00")
                        i.isEnabled = true
                    }
                }
            }
        }
    }

    fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }

    private fun obtenerDia(dia: String) : Array<*> {
        when (dia) {
            "lunes" -> return showLunes
            "martes" -> return showMartes
            "miércoles" -> return showMiercoles
            "jueves" -> return showJueves
            "viernes" -> return showViernes
            "sábado" -> return showSabado
            "domingo" -> return showDomingo
            else -> {
                Toast.makeText(this, "Error en los Permisos ${dia}", Toast.LENGTH_SHORT).show()
            }
        }
        return showDomingo
    }

    private fun obtenerDia(checkBox: CheckBox) : String {
        when (checkBox) {
            cbLunes -> return "lunes"
            cbMartes -> return "martes"
            cbMiercoles -> return "miércoles"
            cbJueves -> return "jueves"
            cbViernes -> return "viernes"
            cbSabado -> return "sábado"
            cbDomingo -> return "domingo"
            else -> {
                Toast.makeText(this, "Error en texto de los CheckBox ${checkBox}", Toast.LENGTH_SHORT).show()
            }
        }
        return "domingo"
    }

    private fun habilitarEdicion(){
        for(dia in showSemana){
            if(dia is Array<*>){
                val checkboxdia = dia[0] as CheckBox
                val etEntrada = dia[1] as EditText
                val etSalida = dia[2] as EditText

                checkboxdia.isEnabled = true
                etEntrada.isEnabled = true
                etSalida.isEnabled = true
            }
        }
        titulo.setText("Editar Áreas")
        rlEditar.visibility = RelativeLayout.GONE
        rlSaveCancel.visibility = RelativeLayout.VISIBLE
    }

    private fun showTimePickerDialog(et: EditText) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // Hora actual en formato 24h
        val minute = calendar.get(Calendar.MINUTE)    // Minuto actual

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Actualiza el objeto Calendar con la hora seleccionada
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                // Formatea la hora y la establece en el EditText
                // "HH:mm" para formato 24h (ej. 15:30)
                // "hh:mm a" para formato 12h con AM/PM (ej. 03:30 PM)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                et.setText(timeFormat.format(calendar.time))
            },
            hour,
            minute,
            true // true para formato 24h, false para formato 12h con AM/PM
        )
        timePickerDialog.show()
    }

    private fun updateArea(permisos: Array<*>){
        val db = Firebase.firestore
        val userRef = db.collection("Permisos").document(spinArea.selectedItem.toString())

        val mapDatosArea : MutableMap<String, List<String>> = mutableMapOf()
        val deletes : MutableMap<String, Any> = mutableMapOf()
        for(oneDay in permisos){
            if(oneDay is Array<*>){
                val checkBox = oneDay[0] as CheckBox
                if(checkBox.isChecked){
                    val dia = obtenerDia(checkBox)
                    val ent = oneDay[1] as EditText
                    val entrada = ent.text.toString()
                    val sal = oneDay[2] as EditText
                    val salida = sal.text.toString()
                    val entSal :  MutableList<String> = mutableListOf()
                    entSal.add(entrada)
                    entSal.add(salida)
                    mapDatosArea.put(dia,entSal)
                } else{
                    val dia = obtenerDia(checkBox)
                    deletes.put(dia, FieldValue.delete())
                }
            }
        }
        userRef.update(mapDatosArea as Map<String, Any>).addOnSuccessListener {
            //Toast.makeText(this,"Cambios guardados exitosamente",Toast.LENGTH_SHORT).show()
        }
        userRef.update(deletes).addOnSuccessListener {
            Toast.makeText(this,"Cambios guardados exitosamente",Toast.LENGTH_SHORT).show()
        }
        intent.putExtra("updated_area", spinArea.selectedItem.toString())
        finish()
        startActivity(intent)
    }

    private fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

}