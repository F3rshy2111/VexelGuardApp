package com.example.iniciosesion

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.iniciosesion.SharedPreferencesManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class obtener_permisos(context :Context) {
    private val db = Firebase.firestore

    var entrada : String? =""
    var salida : String? =""
    var preferencesManager = SharedPreferencesManager(context)


    fun permisoConcedido():Boolean{
        obtenerPermisosDelDia()

        if (!entrada.isNullOrEmpty() && !salida.isNullOrEmpty()) {
            val validarHora = isCurrentTimeBetween(entrada!!, salida!!)
            Log.d("Validación","${validarHora}. Se encuentra entre ${entrada} y ${salida}")
            if(!validarHora){
                return false
            } else{
                return true
            }
        }
        return true
    }

    fun obtenerPermisosDelDia(){
        Log.d("PruebasPermisos", "Funcion Obtener Permisos por dia")
        val today = obtenerDia()
        when(today){
            "lunes" -> {
                entrada = preferencesManager.getPermisoEntrada(0,0)
                salida = preferencesManager.getPermisoEntrada(0,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es lunes, tienes permiso de ${entrada} a ${salida}")
            }
            "martes"->{
                entrada = preferencesManager.getPermisoEntrada(1,0)
                salida = preferencesManager.getPermisoEntrada(1,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es martes, tienes permiso de ${entrada} a ${salida}")
            }
            "miércoles" -> {
                entrada = preferencesManager.getPermisoEntrada(2,0)
                salida = preferencesManager.getPermisoEntrada(2,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es miércoles, tienes permiso de ${entrada} a ${salida}")
            }
            "jueves"->{
                entrada = preferencesManager.getPermisoEntrada(3,0)
                salida = preferencesManager.getPermisoEntrada(3,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es jueves, tienes permiso de ${entrada} a ${salida}")
            }
            "viernes"->{
                entrada = preferencesManager.getPermisoEntrada(3,0)
                salida = preferencesManager.getPermisoEntrada(3,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es viernes, tienes permiso de ${entrada} a ${salida}")
            }
            "sábado"->{
                entrada = preferencesManager.getPermisoEntrada(3,0)
                salida = preferencesManager.getPermisoEntrada(3,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es sábado, tienes permiso de ${entrada} a ${salida}")
            }
            "domingo"->{
                entrada = preferencesManager.getPermisoEntrada(3,0)
                salida = preferencesManager.getPermisoEntrada(3,1)
                Log.d("PruebasPermisos", "Entrada: ${entrada} - Salida: ${salida}")
                println("Hoy es domingo, tienes permiso de ${entrada} a ${salida}")
            }
        }
    }

    fun obtenerDia() : String{
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Obtener el objeto DayOfWeek
        val dayOfWeek: DayOfWeek = today.dayOfWeek

        // El enum DayOfWeek ya tiene los nombres en inglés por defecto
        println("Hoy es (en inglés): $dayOfWeek")

        // Para obtener el nombre en español, necesitarás un mapeo o una función de formato
        val dayNameSpanish = when (dayOfWeek) {
            DayOfWeek.MONDAY -> "lunes"
            DayOfWeek.TUESDAY -> "martes"
            DayOfWeek.WEDNESDAY -> "miércoles"
            DayOfWeek.THURSDAY -> "jueves"
            DayOfWeek.FRIDAY -> "viernes"
            DayOfWeek.SATURDAY -> "sábado"
            DayOfWeek.SUNDAY -> "domingo"
        }
        return dayNameSpanish
    }

    fun isCurrentTimeBetween(horaEntrada: String, horaSalida: String): Boolean {
        try {
            val ahora = LocalTime.now()
            val entrada = LocalTime.parse(horaEntrada)
            val salida = LocalTime.parse(horaSalida)

            val estaDentro = (ahora.equals(entrada) || ahora.isAfter(entrada)) &&
                    (ahora.equals(salida) || ahora.isBefore(salida))

            return estaDentro

        } catch (e: DateTimeParseException) {
            Log.e("TimeChecker", "Error al parsear las horas: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.e("TimeChecker", "Error inesperado al comprobar la hora: ${e.message}")
            return false
        }
    }
}

