package com.example.iniciosesion

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.VoiceInteractor
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.iniciosesion.com.example.iniciosesion.DocumentChecker
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class registro_invitado : navDrawer() {

    private lateinit var etNombre : EditText
    private lateinit var etCorreo : EditText
    private lateinit var spinnerArea: EditText
    private lateinit var etFechaInicio: EditText
    private lateinit var etFechaFin: EditText
    private lateinit var etHoraInicio: EditText
    private lateinit var etHoraFin: EditText
    private lateinit var btnRegistrar : Button
    private lateinit var vista: View
    private lateinit var elementos: Array<*>
    private val urlOTP = "https://crearotpusuario-668387496305.us-central1.run.app/crear_otp_usuario"
    private val client = OkHttpClient()
    //private val calendar = Calendar.getInstance()

    private val calendarFechaInicio = Calendar.getInstance()
    private val calendarFechaFin = Calendar.getInstance()
    private val calendarHoraInicio = Calendar.getInstance()
    private val calendarHoraFin = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        etNombre = findViewById(R.id.et_reg_inv_nombre)
        etCorreo = findViewById(R.id.et_reg_inv_correo)
        spinnerArea = findViewById(R.id.et_reg_inv_area)
        btnRegistrar = findViewById(R.id.btn_reg_inv)
        etFechaInicio = findViewById(R.id.et_reg_inv_fechaInicio)
        etFechaFin = findViewById(R.id.et_reg_inv_fechaFin)
        etHoraInicio = findViewById(R.id.et_reg_inv_horaInicio)
        etHoraFin = findViewById(R.id.et_reg_inv_horaFin)
        vista = findViewById(R.id.reg_invitado)
        elementos = arrayOf(etNombre, etCorreo, spinnerArea, etFechaInicio, etFechaFin, etHoraInicio, etHoraFin)

        etFechaInicio.setOnClickListener { showDatePickerDialog(etFechaInicio, calendarFechaInicio, null) }
        etFechaFin.setOnClickListener { showDatePickerDialog(etFechaFin, calendarFechaFin, calendarFechaInicio ) }
        etHoraInicio.setOnClickListener { showTimePickerDialog(etHoraInicio, calendarHoraInicio, null, null) }
        etHoraFin.setOnClickListener {
            showTimePickerDialog(etHoraFin, calendarHoraFin, calendarHoraInicio,
                if (etFechaInicio.text.isNotEmpty() && etFechaFin.text.isNotEmpty() &&
                    calendarFechaInicio.timeInMillis == calendarFechaFin.timeInMillis) calendarHoraInicio else null)
        }
        vista.setOnClickListener{ hideKeyboard(vista) }

        btnRegistrar.setOnClickListener {
            val comprobacion = comprobaciónCampos()
            if(comprobacion){
                val nombre = etNombre.text.toString()
                val correo = etCorreo.text.toString()
                val area = spinnerArea.text.toString()
                crearInvitado(correo, nombre, area)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_registro_invitado // Asegúrate de tener este layout
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

    private fun comprobaciónCampos() : Boolean{
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

    private fun crearInvitado(correo:String, nombre:String, area:String){
        val db = Firebase.firestore
        val secondaryApp = FirebaseApp.getInstance("secondaryFirebaseAuth")
        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

        secondaryAuth.createUserWithEmailAndPassword(correo, "123456")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val uid = user?.uid ?: return@addOnCompleteListener

                    val userData = hashMapOf(
                        "correo" to correo.trim(),
                        "nombre" to nombre.trim(),
                        "tipo" to "Invitado",
                        "area" to area.trim(),
                        "foto" to "URL",
                        "permisos" to uid,
                        "verificado" to false
                    )

                    db.collection("usuarios").document(uid).set(userData)
                        .addOnSuccessListener {
                            crearMaestra(uid)
                            crearControlAsistencia(uid)
                            crearPermisosInvitados(uid)
                            Toast.makeText(this, "Usuario registrado y guardado", Toast.LENGTH_SHORT).show()
                            /*val intencion1 = Intent(this, Portada::class.java)
                            startActivity(intencion1)*/
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun crearPermisosInvitados(uid:String){
        val documentChecker = DocumentChecker()
        val db = Firebase.firestore

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

        val customView = layoutInflater.inflate(R.layout.dialog_registro_exitoso, null)
        AlertDialog.Builder(this@registro_invitado)
            .setView(customView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                val intencion1 = Intent(applicationContext, Portada::class.java)
                startActivity(intencion1)
            }.show()

    }

    private fun crearControlAsistencia(uid:String){
        val documentChecker = DocumentChecker()
        val db = Firebase.firestore
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("es", "MX"))
        val formattedDate = currentDate.format(formatter)
        var idCA = ""

        val mapaControl = hashMapOf(
            "id_usuario" to uid,
            "num_accesos" to 0
        )

        val mapaAsistencia : Map<String, Any> = mapOf(
            "salida" to "",
            "num_registros" to 0,
            "metodo" to "QR")

        db.collection("Control_Asistencia")
            .add(mapaControl)
            .addOnSuccessListener { documentReference ->
                idCA = documentReference.id
                println("Documento agregado con ID: ${documentReference.id}")

                CoroutineScope(Dispatchers.Main).launch {
                    documentChecker.crearAccesoConId("Control_Asistencia",idCA,"Accesos","01-01-36",mapaAsistencia)
                }

                db.collection("usuarios").document(uid).update("historial_accesos",idCA).addOnSuccessListener { documentReference ->
                    println("Documento actualizado") }
                    .addOnFailureListener { e ->
                        println("Error al agregar el documento: $e")
                    }
            }
            .addOnFailureListener { e ->
                println("Error al agregar el documento: $e")
            }
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
                    /*runOnUiThread {
                        layout.setBackgroundColor(Color.RED)
                        statusText.text = "Error al enviar la OTP"
                        cerrarVentana()
                    }*/
                }

                override fun onResponse(call: Call, response: Response) {
                    /*runOnUiThread {
                        if (response.code == 200) {
                            val json = JSONObject(response.body?.string())
                            val usuario = json.getString("mensaje")
                            layout.setBackgroundColor(Color.parseColor("#1F693C"))
                            imageView.setImageResource(R.drawable.autorizo)
                            statusText.text = "Acceso autorizado \n\n Bienvenido: \n$usuario"
                        } else {
                            val json = JSONObject(response.body?.string())
                            val mensaje = json.getString("mensaje")
                            layout.setBackgroundColor(Color.parseColor("#F03A47"))
                            imageView.setImageResource(R.drawable.denegado)
                            statusText.text = "Acceso denegado: \n\n$mensaje"
                    }
                        //cerrarVentana()
                    }*/
                }
            })
        } catch (e: Exception) {
            Log.d("Error","Error general: ${e.message}")
        }
    }

}