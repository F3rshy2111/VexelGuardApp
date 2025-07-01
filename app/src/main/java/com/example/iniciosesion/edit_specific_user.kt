package com.example.iniciosesion

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.example.iniciosesion.com.example.iniciosesion.DocumentChecker
import com.example.iniciosesion.com.example.iniciosesion.ImageAdapter
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class edit_specific_user : navDrawer() {
    private val calendar = Calendar.getInstance()
    private lateinit var correoTv : TextView
    private lateinit var tipoTv : TextView
    private lateinit var datosBioTv : TextView
    private lateinit var otpTv : TextView
    private lateinit var lastOtpTv : TextView
    private lateinit var datosBioRL : RelativeLayout
    private lateinit var otpRL : RelativeLayout
    private lateinit var lastOtpRL : RelativeLayout
    private lateinit var nombreET : EditText
    private lateinit var btnUpdate : Button
    private lateinit var btnCambiarUser:Button
    private lateinit var formularioInvitados: LinearLayout
    private lateinit var formularioRegulares: LinearLayout
    val db = Firebase.firestore
    var userTipo = ""
    var userDatosBio = ""
    var userOtp = ""
    var userLastOtp = ""
    var userArea = ""

    private val urlOTP = "https://crearotpusuario-668387496305.us-central1.run.app/crear_otp_usuario"
    private val client = OkHttpClient()

    //Formulario areas
    private lateinit var etCorreo : EditText
    private lateinit var etNombre : EditText
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
    private lateinit var vista: ScrollView
    private lateinit var spinArea: Spinner
    private lateinit var btnRegistrar: Button
    private lateinit var cbNewArea : CheckBox
    private lateinit var rlSpinner: RelativeLayout
    private lateinit var linlayCrearArea: LinearLayout
    private val opcionesList =ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var showLunes : Array<*>
    private lateinit var showMartes : Array<*>
    private lateinit var showMiercoles: Array<*>
    private lateinit var showJueves : Array<*>
    private lateinit var showViernes : Array<*>
    private lateinit var showSabado : Array<*>
    private lateinit var showDomingo : Array<*>
    private lateinit var showSemana : Array<*>
    private var gruposMap : MutableMap<String, Map<String,Any>?> = mutableMapOf()

    private lateinit var etFechaInicio: EditText
    private lateinit var etFechaFin: EditText
    private lateinit var etHoraInicio: EditText
    private lateinit var etHoraFin: EditText
    private lateinit var elementos: Array<*>
    private val calendarFechaInicio = Calendar.getInstance()
    private val calendarFechaFin = Calendar.getInstance()
    private val calendarHoraInicio = Calendar.getInstance()
    private val calendarHoraFin = Calendar.getInstance()

    // Selección de Fotos
    private val PICK_IMAGES_REQUEST_CODE = 101
    private val PERMISSION_REQUEST_CODE = 102
    private val urlFace =
        "https://almacenarembedding-668387496305.us-central1.run.app/procesar_foto"
    private lateinit var statusText: TextView
    //private lateinit var layout: LinearLayout
    private lateinit var btnSeleccionar: Button
    private lateinit var gridView: GridView
    private val imageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        val uid = intent.getStringExtra("uid")
        userArea = intent.getStringExtra("areaUser").toString()
        var btnCambio = false
        super.onCreate(savedInstanceState)

        // Vistas Areas
        etCorreo = findViewById(R.id.et_reg_regu_correo)
        etNombre = findViewById(R.id.et_reg_regu_nombre)
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
        vista = findViewById(R.id.editSpecificUser)
        btnRegistrar = findViewById(R.id.btn_reg_regu)
        cbNewArea = findViewById(R.id.cb_crearArea)
        rlSpinner = findViewById(R.id.edAreas_RL_Spinner)
        linlayCrearArea = findViewById(R.id.edAreas_crearArea)
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
        cbNewArea.visibility = CheckBox.GONE
        //

        etFechaInicio = findViewById(R.id.et_reg_inv_fechaInicio)
        etFechaFin = findViewById(R.id.et_reg_inv_fechaFin)
        etHoraInicio = findViewById(R.id.et_reg_inv_horaInicio)
        etHoraFin = findViewById(R.id.et_reg_inv_horaFin)
        elementos = arrayOf(etFechaInicio, etFechaFin, etHoraInicio, etHoraFin)
        etFechaInicio.setOnClickListener { showDatePickerDialog(etFechaInicio, calendarFechaInicio, null) }
        etFechaFin.setOnClickListener { showDatePickerDialog(etFechaFin, calendarFechaFin, calendarFechaInicio ) }
        etHoraInicio.setOnClickListener { showTimePickerDialog(etHoraInicio, calendarHoraInicio, null, null) }
        etHoraFin.setOnClickListener {
            showTimePickerDialog(etHoraFin, calendarHoraFin, calendarHoraInicio,
                if (etFechaInicio.text.isNotEmpty() && etFechaFin.text.isNotEmpty() &&
                    calendarFechaInicio.timeInMillis == calendarFechaFin.timeInMillis) calendarHoraInicio else null)
        }

        correoTv = findViewById(R.id.updUser_correo)
        tipoTv = findViewById(R.id.updUser_tipo)
        datosBioTv = findViewById(R.id.updUser_datosBio)
        otpTv = findViewById(R.id.updUser_OTP)
        lastOtpTv = findViewById(R.id.updUser_lastOTP)
        datosBioRL = findViewById(R.id.rl_updUser_Bio)
        otpRL = findViewById(R.id.rl_updUser_OTP)
        lastOtpRL = findViewById(R.id.rl_updUser_lastOTP)
        nombreET = findViewById(R.id.et_updUser_nombre)
        btnUpdate = findViewById(R.id.btnUpdateUser)
        btnCambiarUser = findViewById(R.id.btnChange)
        formularioRegulares = findViewById(R.id.formularioFotos)
        formularioInvitados = findViewById(R.id.fomularioInvitados)

        statusText = findViewById(R.id.statusText)
        //layout = findViewById(R.id.main)
        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        gridView = findViewById(R.id.gridView)
        progressBar = findViewById(R.id.progressBar)
        imageAdapter = ImageAdapter(this, imageUris)
        gridView.adapter = imageAdapter
        btnSeleccionar.setOnClickListener {
            pedirPermisos()
        }

        // --------- Spiner Area ---------------------
        adapter = ArrayAdapter(this,
            R.layout.spinner_selected_item, opcionesList)
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no hay nada seleccionado (puede que no sea necesario en tu caso)
            }
        }

        //vista.setOnClickListener{ hideKeyboard(vista) }


        if (uid != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener { document ->
                if(document != null){
                    var correo = document.getString("correo")
                    if(correo.isNullOrEmpty()){ correo = document.getString("email")}
                    val tipo = document.getString("tipo")
                    val datosBio:Boolean = document.contains("id_datos_biometricos")
                    val otp:Boolean = document.contains("TOTP")
                    val lastOtp = document.contains("last_totp")
                    val nombre = document.getString("nombre")
                    val area = document.getString("area")

                    userTipo = document.getString("tipo").toString()
                    userDatosBio = document.getString("id_datos_biometricos").toString()
                    userOtp = document.getString("TOTP").toString()
                    userLastOtp = document.getString("last_totp").toString()

                    correoTv.setText(correo)
                    tipoTv.setText(tipo)
                    datosBioTv.setText(datosBio.toString())
                    otpTv.setText(otp.toString())
                    lastOtpTv.setText(lastOtp.toString())
                    nombreET.setText(nombre)
                }
            }
        }

        btnCambiarUser.setOnClickListener {
            var change = false
            if(userTipo=="Invitado" && !btnCambio){
                formularioRegulares.visibility = LinearLayout.VISIBLE
                btnCambiarUser.setText("Cancelar cambio")
                btnCambio = true
                change = true
                btnUpdate.isEnabled = false
            }

            if(userTipo=="Regular" && !btnCambio){
                formularioInvitados.visibility = LinearLayout.VISIBLE
                btnCambiarUser.setText("Cancelar cambio")
                btnCambio = true
                change = true
            }

            if(btnCambio && !change){
                formularioRegulares.visibility = LinearLayout.GONE
                formularioInvitados.visibility = LinearLayout.GONE
                btnCambiarUser.setText("Cambiar tipo de usuario")
                btnCambio = false
            }

        }

        btnUpdate.setOnClickListener {
            if(userTipo=="Invitado"){
                if(!uid.isNullOrEmpty() && btnCambio){
                    invitadoToRegular(uid)
                } else {
                    val userData = hashMapOf<String, Any>(
                        "nombre" to nombreET.text.toString().trim(),
                        "area" to spinArea.selectedItem.toString()
                    )

                    if (uid != null) {
                        db.collection("usuarios").document(uid).update(userData)
                            .addOnSuccessListener {
                                dialogPromt(true)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al editar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            if(userTipo=="Regular"){
                if(btnCambio){
                    val comprobacion = comprobacionCamposInvitado()
                    if(comprobacion && !uid.isNullOrEmpty()){
                        regularToInvitado(uid)
                    } else {
                        Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val userData = hashMapOf<String, Any>(
                        "nombre" to nombreET.text.toString().trim(),
                        "area" to spinArea.selectedItem.toString(),
                        "permisos" to spinArea.selectedItem.toString()
                    )

                    if (uid != null) {
                        db.collection("usuarios").document(uid).update(userData)
                            .addOnSuccessListener {
                                dialogPromt(true)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al editar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }


    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_edit_specific_user // Asegúrate de tener este layout
    }

    fun eliminarDocumentosTipo(uid: String){
        if(tipoTv.text.toString() == "Invitado"){
            val updates = hashMapOf<String, Any>(
                "TOTP" to FieldValue.delete(),
                "last_totp" to FieldValue.delete()
            )

            db.collection("usuarios").document(uid).update(updates).addOnSuccessListener {
                Log.d("CambioTipo", "Campos de invitado eliminados")
            }

            val rutaInvitados = db.collection("Permisos").document("invitados").collection("Invitados")

            rutaInvitados.document(uid).delete().addOnSuccessListener {
                Log.d("CambioTipo", "Documento de permisos de invitado eliminados")
            }
        }

        if(tipoTv.text.toString() == "Regular"){
            val updates = hashMapOf<String, Any>(
                "id_datos_biometricos" to FieldValue.delete()
            )

            db.collection("usuarios").document(uid).update(updates).addOnSuccessListener {
                Log.d("CambioTipo", "Campos de regulares eliminados")
            }

            db.collection("Datos_Biometricos").document(userDatosBio).delete().addOnSuccessListener {
                Log.d("CambioTipo", "Campos de regulares eliminados")
            }
        }

    }

    private fun showDatePickerDialog(et: EditText) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Actualiza el objeto Calendar con la fecha seleccionada
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Formatea la fecha y la establece en el EditText
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                et.setText(dateFormat.format(calendar.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
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

    private fun showDatePickerDialog(et: EditText, currentCalendar: Calendar, minDateCalendar: Calendar?) {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                currentCalendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                et.setText(dateFormat.format(currentCalendar.time))

                // Si se selecciona una nueva fecha de inicio, resetear la fecha de fin si es anterior
                if (et == etFechaInicio && etFechaFin.text.isNotEmpty()) {
                    if (currentCalendar.time.after(calendarFechaFin.time)) {
                        etFechaFin.setText("") // Limpiar la fecha de fin si es anterior
                        // Opcionalmente, puedes establecer la fecha de fin igual a la de inicio
                        calendarFechaFin.time = currentCalendar.time
                        etFechaFin.setText(dateFormat.format(calendarFechaFin.time))
                    }
                }
            },
            year,
            month,
            day
        )

        minDateCalendar?.let {
            datePickerDialog.datePicker.minDate = it.timeInMillis
        }
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(et: EditText, currentCalendar: Calendar, minTimeCalendar: Calendar?, restrictByDate: Calendar?) {
        val hour = currentCalendar.get(Calendar.HOUR_OF_DAY) // Hora actual en formato 24h
        val minute = currentCalendar.get(Calendar.MINUTE)    // Minuto actual

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Actualiza el objeto Calendar con la hora seleccionada
                currentCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                currentCalendar.set(Calendar.MINUTE, selectedMinute)

                // Formatea la hora y la establece en el EditText
                // "HH:mm" para formato 24h (ej. 15:30)
                // "hh:mm a" para formato 12h con AM/PM (ej. 03:30 PM)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                et.setText(timeFormat.format(currentCalendar.time))

                // Lógica para resetear la hora de fin si es anterior a la de inicio en el mismo día
                if (et == etHoraInicio && etHoraFin.text.isNotEmpty() && restrictByDate != null &&
                    calendarFechaInicio.timeInMillis == calendarFechaFin.timeInMillis &&
                    currentCalendar.time.after(calendarHoraFin.time)) {
                    etHoraFin.setText("") // Limpiar la hora de fin si es anterior
                }
            },
            hour,
            minute,
            true // true para formato 24h, false para formato 12h con AM/PM
        )

        /*// Restringir el TimePicker si es necesario
        // Solo aplica la restricción de hora si las fechas son las mismas
        if (restrictByDate != null && calendarFechaInicio.timeInMillis == calendarFechaFin.timeInMillis) {
            minTimeCalendar?.let {
                // No hay un metodo directo para establecer un minTime en TimePickerDialog.
                // En su lugar, puedes validar la selección después de que el usuario elige una hora.
                // Sin embargo, si quieres "deshabilitar" horas, tendrías que crear un TimePicker personalizado.
                // Para una solución simple, validaremos después de la selección.
            }
        }*/
        timePickerDialog.show()
    }

    fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
    }

    //-------------------------------- FORMULARIO AREAS --------------------------------------
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
                    }
                    gruposMap = grupos
                    // Update the adapter's data and notify it
                    adapter.clear() // Clear existing items
                    adapter.addAll(grupos.keys.toList()) // Add new items
                    adapter.notifyDataSetChanged() // Notify the adapter that data has changed

                    val defaultValue = userArea
                    val spinnerPosition = adapter.getPosition(defaultValue)
                    spinArea.setSelection(spinnerPosition)
                }
            }
        }
    }

    //-------------------------------- FORMULARIO REGULAR --------------------------------------

    private fun invitadoToRegular(uid:String){
        eliminarDocumentosTipo(uid)

        val userData = hashMapOf<String, Any>(
            "nombre" to nombreET.text.toString().trim(),
            "area" to spinArea.selectedItem.toString(),
            "permisos" to spinArea.selectedItem.toString(),
            "tipo" to "Regular"
        )

        db.collection("usuarios").document(uid).update(userData)
            .addOnSuccessListener {
                //Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al editar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        enviarVariasFotos(uid)
    }

    private fun pedirPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                abrirGaleria()
            }
        } else {
            abrirGaleria()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            abrirGaleria()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(
            Intent.createChooser(intent, "Selecciona 9 imágenes"),
            PICK_IMAGES_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUris.clear()

            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    imageUris.add(clipData.getItemAt(i).uri)
                }
            } ?: data?.data?.let { uri ->
                imageUris.add(uri)
            }

            if (imageUris.size != 9) {
                Toast.makeText(this, "Debes seleccionar exactamente 9 imágenes", Toast.LENGTH_LONG)
                    .show()
                btnUpdate.isEnabled = false
            } else {
                imageAdapter.notifyDataSetChanged()
                //Toast.makeText(this, "Arriba los Vaqueros", Toast.LENGTH_LONG).show()
                btnUpdate.isEnabled = true
            }
        }
    }

    private fun enviarVariasFotos(usId : String) {

        runOnUiThread {
            progressBar.visibility = View.VISIBLE
            statusText.text = "Enviando fotos..."
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)  // Tiempo para conectar
            .writeTimeout(5, TimeUnit.MINUTES)    // Tiempo para enviar datos
            .readTimeout(5, TimeUnit.MINUTES)     // Tiempo para esperar respuesta
            .build()
        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        try {
            for ((index, uri) in imageUris.withIndex()) {
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val bytes = stream.readBytes()
                    val fileName = "foto${index + 1}.jpg"
                    multipartBuilder.addFormDataPart(
                        "fotos",
                        fileName,
                        bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                }
            }

            // Añadir campo extra id_usuario
            multipartBuilder.addFormDataPart("id_usuario", usId)

            val requestBody = multipartBuilder.build()

            val request = Request.Builder()
                .url(urlFace)
                .post(requestBody)
                .build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        statusText.text = "Error al enviar las fotos"
                        Log.d("PruebasHTTP", "Error:"+ e )
                    }
                }


                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            /*statusText.text = "Fotos enviadas correctamente"
                            Toast.makeText(applicationContext, "Usuario registrado y guardado", Toast.LENGTH_SHORT).show()
                            val intencion1 = Intent(applicationContext, Portada::class.java)
                            startActivity(intencion1)*/
                            dialogPromt()
                        } else {
                            statusText.text = "Error en servidor: ${response.code}"
                        }
                    }
                }
            })
        }
        catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                statusText.text = "Error preparando fotos: ${e.message}"
            }
        }
    }

    //-------------------------------- FORMULARIO INVITADOS --------------------------------------

    private fun regularToInvitado(uid:String){
        eliminarDocumentosTipo(uid)

        val userData = hashMapOf<String, Any>(
            "nombre" to nombreET.text.toString().trim(),
            "area" to spinArea.selectedItem.toString(),
            "permisos" to uid,
            "tipo" to "Invitado"
        )

        db.collection("usuarios").document(uid).update(userData)
            .addOnSuccessListener {
                //Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al editar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        crearPermisosInvitados(uid)
        crearMaestra(uid)
    }

    private fun crearMaestra(uid: String) {
        try {
            val json = JSONObject().apply {
                put("user_id", uid)
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(urlOTP)
                .post(body)
                .addHeader("Content-Type", "application/json") // Añade esto explícitamente
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {

                }
            })

            dialogPromt()
        } catch (e: Exception) {
            Log.d("Error","Error general: ${e.message}")
        }
    }

    private fun crearPermisosInvitados(uid:String){
        val documentChecker = DocumentChecker()

        val fechaInicio = etFechaInicio.text.toString()
        val fechaFin = etFechaFin.text.toString()
        val horaEntrada = etHoraInicio.text.toString()
        val horaSalida = etHoraFin.text.toString()
        val horario : List<String> = listOf(horaEntrada, horaSalida)

        var datos : Map<String, Any> = mapOf(
            "fecha_inicio" to fechaInicio,
            "fecha_fin" to fechaFin,
            "lunes" to horario,
            "martes" to horario,
            "miércoles" to horario,
            "jueves" to horario,
            "viernes" to horario,
            "sábado" to horario,
            "domingo" to horario,
        )

        CoroutineScope(Dispatchers.Main).launch {
            documentChecker.crearPermisosInvitados(uid,datos)
        }

    }

    private fun comprobacionCamposInvitado() : Boolean{
        var comprobacion = true
        // Comprueba que todos los elementos del formulario esten completos
        for(i in elementos){
            val elemento = i as EditText
            if(elemento.text.toString().isNullOrEmpty()){
                comprobacion = false
                break
            }
        }
        //Comprueba que las horas seleccionadas sean válidas
        val entrada = LocalTime.parse(etHoraInicio.text.toString())
        val salida = LocalTime.parse(etHoraFin.text.toString())

        val validacionHoras = entrada.isBefore(salida)
        if(!validacionHoras){
            comprobacion = false
        }

        return comprobacion
    }

    private fun dialogPromt(){
        val customView = layoutInflater.inflate(R.layout.dialog_cambio_exitoso, null)
        AlertDialog.Builder(this@edit_specific_user)
            .setView(customView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                val intencion1 = Intent(applicationContext, Portada::class.java)
                startActivity(intencion1)
            }.show()
    }

    private fun dialogPromt(actualizacion:Boolean){
        val customView = layoutInflater.inflate(R.layout.dialog_actualizacion_exitosa, null)
        AlertDialog.Builder(this@edit_specific_user)
            .setView(customView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                val intencion1 = Intent(applicationContext, Portada::class.java)
                startActivity(intencion1)
            }.show()
    }
}