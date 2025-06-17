package com.example.iniciosesion.com.example.iniciosesion

import android.content.Context
import android.content.SharedPreferences
import com.example.iniciosesion.SharedPreferencesManager.Companion.KEY_USER_AREA

class roleManager(context: Context) {
    private val PREFS_NAME = "user_prefs"
    private val KEY_USER_ROLE = "user_role"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserRole(role: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    fun getUserRole(): String {
        return sharedPreferences.getString(KEY_USER_ROLE, "guest") ?: "guest"
    }

    fun  clearUserRole() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER_ROLE)
        editor.apply()
    }

    fun isAdmin(): Boolean {
        return getUserRole() == "Administrador"
    }

    fun isRegular(): Boolean {
        return getUserRole() == "Regular"
    }

    fun isInvitado(): Boolean {
        return getUserRole() == "Invitado"
    }
}