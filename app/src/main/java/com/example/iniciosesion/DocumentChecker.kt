package com.example.iniciosesion.com.example.iniciosesion

import android.widget.Toast
import com.google.api.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DocumentChecker() {
    private val db = Firebase.firestore

    suspend fun verificarExistenciaDoc(coleccion: String, documentId: String): Boolean {
        return try {
            val documentRef = db.collection(coleccion).document(documentId)
            val documentSnapshot = documentRef.get().await()
            // Retorna true si el documento existe, false si no
            documentSnapshot.exists()
        } catch (e: Exception) {
            // Manejar cualquier error que pueda ocurrir durante la operación
            // Por ejemplo, problemas de conexión, permisos, etc.
            println("Error al verificar la existencia del documento: ${e.message}")
            // Asumimos que no existe o no podemos verificarlo
            false
        }
    }

    suspend fun crearDocumentoConId(coleccion : String, documento: String, datos: Map<String, List<String>>) {
        if (verificarExistenciaDoc(coleccion, documento)) {
            println("El documento con ID '${documento}' ya existe en la colección '${coleccion}'. No se creará uno nuevo.")
        } else {
            try {
                db.collection(coleccion).document(documento).set(datos).await()
                println("Documento con ID '${documento}' creado exitosamente en la colección '${coleccion}'.")
            } catch (e: Exception) {
                println("Error al crear el documento: ${e.message}")
            }
        }
    }

    suspend fun crearAccesoConId(coleccion1 : String, documento1: String, coleccionAccesos: String, fecha: String, datos: Map<String, Any>) {
        try {
            db.collection(coleccion1).document(documento1).collection(coleccionAccesos).document(fecha).set(datos).await()
            println("Documento con ID '${fecha}' creado exitosamente en la colección 'Accesos'.")
        } catch (e: Exception) {
            println("Error al crear el documento: ${e.message}")
        }
    }

    suspend fun crearPermisosInvitados(uid: String, datos: Map<String, Any>){
        try {
            db.collection("Permisos").document("invitados")
                .collection("Invitados").document(uid).set(datos).await()
            println("Documento con ID '${uid}' creado exitosamente en la colección 'Invitados' dentro de Permisos.")
        } catch (e: Exception) {
            println("Error al crear el documento: ${e.message}")
        }

    }
}