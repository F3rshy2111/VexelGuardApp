package com.example.iniciosesion

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.iniciosesion.com.example.iniciosesion.roleManager

class SharedPreferencesManager(context: Context) {

    private var role = roleManager(context)

    //Claves para guardar la información del usuario
    private val PREF_NAME = "MyAppPrefs" // Nombre del archivo de preferencias
    companion object {
        val KEY_USER_ID = "user_id"
        val KEY_USER_AREA = "user_area"
        val KEY_USER_ENTRADA_LUNES = "user_ent_lunes"
        val KEY_USER_ENTRADA_MARTES = "user_ent_martes"
        val KEY_USER_ENTRADA_MIERCOLES = "user_ent_miercoles"
        val KEY_USER_ENTRADA_JUEVES = "user_ent_jueves"
        val KEY_USER_ENTRADA_VIERNES = "user_ent_viernes"
        val KEY_USER_ENTRADA_SABADO = "user_ent_sabado"
        val KEY_USER_ENTRADA_DOMINGO = "user_ent_domingo"
        val KEY_USER_SALIDA_LUNES = "user_sal_lunes"
        val KEY_USER_SALIDA_MARTES = "user_sal_martes"
        val KEY_USER_SALIDA_MIERCOLES = "user_sal_miercoles"
        val KEY_USER_SALIDA_JUEVES = "user_sal_jueves"
        val KEY_USER_SALIDA_VIERNES = "user_sal_viernes"
        val KEY_USER_SALIDA_SABADO = "user_sal_sabado"
        val KEY_USER_SALIDA_DOMINGO = "user_sal_domingo"
        val KEY_USER_MASTER = "user_master"
        val KEY_USER_OTP_TEMPORAL = "otp_temporal"
        val KEY_USER_EXPIRES_IN = "codigo_expira_en"
    }


    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Guardar el ID de usuario
    fun saveUserId(userId: String) {
        val editor = sharedPrefs.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    // Obtener el ID de usuario
    fun getUserId(): String? {
        // El segundo argumento es el valor por defecto si la clave no se encuentra
        return sharedPrefs.getString(KEY_USER_ID, null)
    }

    // Borrar el ID de usuario (ej. al cerrar sesión)
    fun clearUserId() {
        val editor = sharedPrefs.edit()
        editor.remove(KEY_USER_ID)
        editor.apply()
    }

    fun saveUserArea(area: String) {
        val editor = sharedPrefs.edit()
        editor.putString(KEY_USER_AREA, area)
        editor.apply()
    }

    fun getUserArea(): String? {
        return sharedPrefs.getString(KEY_USER_AREA, null)
    }

    fun clearUserArea() {
        val editor = sharedPrefs.edit()
        editor.remove(KEY_USER_AREA)
        editor.apply()
    }

    fun savePermisos(
        lunes:ArrayList<*>,
        martes:ArrayList<*>,
        miercoles:ArrayList<*>,
        jueves:ArrayList<*>,
        viernes:ArrayList<*>,
        sabado:ArrayList<*>,
        domingo:ArrayList<*>){
        val editor = sharedPrefs.edit()

        val semanaDeDatos = arrayOf(lunes,martes, miercoles,jueves,viernes,sabado,domingo)

        val clavesPorDia = arrayOf(
            arrayOf(KEY_USER_ENTRADA_LUNES, KEY_USER_SALIDA_LUNES),
            arrayOf(KEY_USER_ENTRADA_MARTES, KEY_USER_SALIDA_MARTES),
            arrayOf(KEY_USER_ENTRADA_MIERCOLES, KEY_USER_SALIDA_MIERCOLES),
            arrayOf(KEY_USER_ENTRADA_JUEVES, KEY_USER_SALIDA_JUEVES),
            arrayOf(KEY_USER_ENTRADA_VIERNES, KEY_USER_SALIDA_VIERNES),
            arrayOf(KEY_USER_ENTRADA_SABADO, KEY_USER_SALIDA_SABADO),
            arrayOf(KEY_USER_ENTRADA_DOMINGO, KEY_USER_SALIDA_DOMINGO)
        )

        // Iteramos usando el índice para emparejar los datos con sus claves
        for (i in semanaDeDatos.indices) {
            val dia = semanaDeDatos[i]
            val claves = clavesPorDia[i]

            // Asegurarse de que las listas de día tengan al menos 2 elementos
            if (dia.size >= 2) {
                val entrada = dia[0] as String
                val salida = dia[1] as String

                editor.putString(claves[0], entrada)
                Log.d("SharedPreferencesManager", "Guardando ${claves[0]}: $entrada")

                editor.putString(claves[1], salida)
                Log.d("SharedPreferencesManager", "Guardando ${claves[1]}: $salida")
            } else {
                Log.w("SharedPreferencesManager", "La lista del día $i no tiene suficientes elementos para guardar entrada y salida.")
                // Podrías manejar aquí el caso de listas incompletas, por ejemplo, guardando "" o null
                editor.putString(claves[0], "") // Guardar vacío si no hay valor
                editor.putString(claves[1], "") // Guardar vacío si no hay valor
            }
        }
        // Aplica todos los cambios una sola vez al final del bucle
        editor.apply()
        Log.d("SharedPreferencesManager", "Todos los permisos guardados.")
    }

    fun getPermisoEntrada(dia:Int,entSal:Int): String? {
        val clavesPorDia = arrayOf(
            arrayOf(KEY_USER_ENTRADA_LUNES, KEY_USER_SALIDA_LUNES),
            arrayOf(KEY_USER_ENTRADA_MARTES, KEY_USER_SALIDA_MARTES),
            arrayOf(KEY_USER_ENTRADA_MIERCOLES, KEY_USER_SALIDA_MIERCOLES),
            arrayOf(KEY_USER_ENTRADA_JUEVES, KEY_USER_SALIDA_JUEVES),
            arrayOf(KEY_USER_ENTRADA_VIERNES, KEY_USER_SALIDA_VIERNES),
            arrayOf(KEY_USER_ENTRADA_SABADO, KEY_USER_SALIDA_SABADO),
            arrayOf(KEY_USER_ENTRADA_DOMINGO, KEY_USER_SALIDA_DOMINGO)
        )

        return sharedPrefs.getString(clavesPorDia[dia][entSal], null)
    }

    fun getPermisoSalida(dayKey: String): String? {
        return sharedPrefs.getString(dayKey, null)
    }

    fun getPermisosUser(){
        Log.d("PruebasPermisosGet","Lunes: ${sharedPrefs.getString(KEY_USER_ENTRADA_LUNES, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_LUNES, "No definido")}")
        Log.d("PruebasPermisosGet","Martes: ${sharedPrefs.getString(KEY_USER_ENTRADA_MARTES, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_MARTES,"No definido")}")
        Log.d("PruebasPermisosGet","Miércoles: ${sharedPrefs.getString(KEY_USER_ENTRADA_MIERCOLES, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_MIERCOLES,"No definido")}")
        Log.d("PruebasPermisosGet","Jueves: ${sharedPrefs.getString(KEY_USER_ENTRADA_JUEVES, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_JUEVES,"No definido")}")
        Log.d("PruebasPermisosGet","Viernes: ${sharedPrefs.getString(KEY_USER_ENTRADA_VIERNES, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_VIERNES,"No definido")}")
        Log.d("PruebasPermisosGet","Sábado: ${sharedPrefs.getString(KEY_USER_ENTRADA_SABADO, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_SABADO,"No definido")}")
        Log.d("PruebasPermisosGet","Domingo: ${sharedPrefs.getString(KEY_USER_ENTRADA_DOMINGO, "No definido")} - ${sharedPrefs.getString(KEY_USER_SALIDA_DOMINGO,"No definido")}")
    }

    fun saveMasterTOTP(master: String){
        val editor = sharedPrefs.edit()
        editor.putString(KEY_USER_MASTER, master)
        editor.apply()
    }

    fun getMasterTOTP(): String?{
        return sharedPrefs.getString(KEY_USER_MASTER, null)
    }

    fun setCodigoExpiraEn(timestamp: Long) {
        sharedPrefs.edit().putLong(KEY_USER_EXPIRES_IN, timestamp).apply()
    }

    fun getCodigoExpiraEn(): Long {
        return sharedPrefs.getLong(KEY_USER_EXPIRES_IN, 0)
    }

    fun clearCodigoExpiraEn() {
        sharedPrefs.edit().remove(KEY_USER_EXPIRES_IN).remove(KEY_USER_OTP_TEMPORAL).apply()
    }

    fun setOtpTemporal(otp: String) {
        sharedPrefs.edit().putString(KEY_USER_OTP_TEMPORAL, otp).apply()
    }

    fun getOtpTemporal(): String? {
        return sharedPrefs.getString(KEY_USER_OTP_TEMPORAL, null)
    }

    fun clearOtpTemporal() {
        sharedPrefs.edit().remove(KEY_USER_OTP_TEMPORAL).apply()
    }

    // Metodo para borrar todas las preferencias
    fun clearAllUserData() {
        role.clearUserRole()
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }
}