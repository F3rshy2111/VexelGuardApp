package com.example.iniciosesion

import android.Manifest
import android.app.Activity
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
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.iniciosesion.com.example.iniciosesion.DocumentChecker
import com.example.iniciosesion.com.example.iniciosesion.ImageAdapter
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

import java.io.IOException
class registro_regular : navDrawer() {
    private val calendar = Calendar.getInstance()
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
    private lateinit var vista: View
    private lateinit var spinArea: Spinner
    private lateinit var btnRegistrar: Button
    private lateinit var etNewArea : EditText
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

    // Selección de Fotos
    private val PICK_IMAGES_REQUEST_CODE = 101
    private val PERMISSION_REQUEST_CODE = 102
    private val urlFace =
        "https://almacenarembedding-668387496305.us-central1.run.app/procesar_foto"
    private lateinit var statusText: TextView
    private lateinit var layout: LinearLayout
    private lateinit var btnSeleccionar: Button
    private lateinit var btnEnviar: Button
    private lateinit var gridView: GridView
    private val imageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsManager = SharedPreferencesManager(this)
        val roleManager = roleManager(this)
        val auth = Firebase.auth

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
        vista = findViewById(R.id.reg_regular_constrait)
        etNewArea = findViewById(R.id.edAreas_etCrearArea)
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


        statusText = findViewById(R.id.statusText)
        layout = findViewById(R.id.main)
        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        gridView = findViewById(R.id.gridView)
        progressBar = findViewById(R.id.progressBar)


        imageAdapter = ImageAdapter(this, imageUris)
        gridView.adapter = imageAdapter

        btnSeleccionar.setOnClickListener {
            pedirPermisos()
        }

        /*
        btnEnviar.setOnClickListener {
            enviarVariasFotos("OAhCu3GjD0MAnsCd5WlPJjDItSf2")
        }*/

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

        vista.setOnClickListener{ hideKeyboard(vista) }

        findViewById<CheckBox>(R.id.cb_crearArea).setOnCheckedChangeListener{
            buttonView, isChecked ->
            run {
                if(buttonView.isChecked){
                    rlSpinner.visibility = Spinner.GONE
                    linlayCrearArea.visibility = EditText.VISIBLE
                    clearPermisos(true)
                } else {
                    rlSpinner.visibility = Spinner.VISIBLE
                    linlayCrearArea.visibility = EditText.GONE
                }
            }
        }

        btnRegistrar.isEnabled = false

        btnRegistrar.setOnClickListener{
            val correo = etCorreo.text.toString()
            val nombre = etNombre.text.toString()
            var area = ""
            if (cbNewArea.isChecked){
                area = etNewArea.text.toString()
            } else {
                area = spinArea.selectedItem.toString()
            }
            if(roleManager.isAdmin()){
                if(cbNewArea.isChecked){
                    if(nombre != null && correo != null && area != null){
                        crearUsuario(correo, "123456", area, nombre)
                        crearArea(showSemana)
                    } else {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    if(nombre != null && correo != null && area != null){
                        crearUsuario(correo, "123456", area, nombre)
                    } else {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }

                }
            } else{
                Toast.makeText(this,"No tienes permiso para ejecutar esta acción", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Proporciona el ID del layout específico de HomeActivity
    override fun getLayoutResId(): Int {
        return R.layout.activity_registro_regular // Asegúrate de tener este layout
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

    fun hideKeyboard(view: View){
        val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
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
                }
            }
        }
    }

    private fun crearUsuario(email: String, password:String, area:String, nombre:String){
        Log.d("Test", "Correo: ${email}")
        Log.d("Test", "Nombre: ${nombre}")
        Log.d("Test", "Área: ${area}")
        val db = Firebase.firestore
        val secondaryApp = FirebaseApp.getInstance("secondaryFirebaseAuth")
        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

        secondaryAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val uid = user?.uid ?: return@addOnCompleteListener

                        val userData = hashMapOf(
                            "correo" to email,
                            "nombre" to nombre,
                            "tipo" to "Regular",
                            "area" to area,
                            "foto" to "URL",
                            "permisos" to area,
                            "verificado" to false
                        )

                        db.collection("usuarios").document(uid).set(userData)
                            .addOnSuccessListener {
                                enviarVariasFotos(uid)
                                crearControlAsistencia(uid)
                                /*Toast.makeText(this, "Usuario registrado y guardado", Toast.LENGTH_SHORT).show()
                                val intencion1 = Intent(this, Portada::class.java)
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

    private fun crearArea(permisos: Array<*>){
        val documentChecker = DocumentChecker()

        val mapDatosArea : MutableMap<String, List<String>> = mutableMapOf()
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

                    CoroutineScope(Dispatchers.Main).launch {
                        documentChecker.crearDocumentoConId("Permisos",etNewArea.text.toString(),mapDatosArea)
                    }
                }
            }
        }
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
            "metodo" to "RF")

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
                btnRegistrar.isEnabled = false
            } else {
                imageAdapter.notifyDataSetChanged()
                btnRegistrar.isEnabled = true
            }
        }
    }

    private fun enviarVariasFotos(usId : String) {

        runOnUiThread {
            btnEnviar.isEnabled = false
            progressBar.visibility = View.VISIBLE
            statusText.text = "Enviando fotos..."
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.MINUTES)  // Tiempo para conectar
            .writeTimeout(4, TimeUnit.MINUTES)    // Tiempo para enviar datos
            .readTimeout(4, TimeUnit.MINUTES)     // Tiempo para esperar respuesta
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
                        btnEnviar.isEnabled = true
                        progressBar.visibility = View.GONE
                        statusText.text = "Error al enviar las fotos"
                        Log.d("PruebasHTTP", "Error:"+ e )
                    }
                }


                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        btnEnviar.isEnabled = true
                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            statusText.text = "Fotos enviadas correctamente"
                            Toast.makeText(applicationContext, "Usuario registrado y guardado", Toast.LENGTH_SHORT).show()
                            val intencion1 = Intent(applicationContext, Portada::class.java)
                            startActivity(intencion1)
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
}