package com.example.iniciosesion

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Inicialización de la instancia de Firebase por defecto (la que usa tu administrador)
        // Esto usualmente se hace automáticamente con google-services.json,
        // pero puedes añadirlo explícitamente si quieres asegurarte.
        FirebaseApp.initializeApp(this) // No es estrictamente necesario si ya está funcionando

        // 2. Inicialización de la instancia secundaria para la creación de usuarios
        // Necesitas obtener los datos de tu google-services.json
        val options = FirebaseOptions.Builder()
            .setApplicationId("1:668387496305:android:2d25e835487923845b26d0") // Busca 'app_id' en google-services.json
            .setApiKey("AIzaSyAbny3fJpqLQ__wPaBM4JdCuYz3NoIb72g") // Busca 'current_key' bajo 'api_key' en google-services.json
            .setStorageBucket("pruebatt-fe778.firebasestorage.app") // Busca 'storage_bucket'
            .setProjectId("pruebatt-fe778") // Busca 'project_id'
            .build()

        // ¡IMPORTANTE! Aquí le das un nombre a tu instancia secundaria
        FirebaseApp.initializeApp(this, options, "secondaryFirebaseAuth") // "secondaryFirebaseAuth" es el nombre que usarás
    }
}