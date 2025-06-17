package com.example.iniciosesion

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.compose.ui.tooling.data.Group
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.iniciosesion.com.example.iniciosesion.roleManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

abstract open class navDrawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var itemAdmin : MenuItem
    private lateinit var itemCodAcc : MenuItem
    private lateinit var navigationView: NavigationView
    private lateinit var btnUserInfo : ImageButton
    private lateinit var btnInicio : ImageButton

    // Variable para controlar si el drawer está habilitado o deshabilitado
    // Por defecto, estará habilitado
    private var isDrawerInteractionEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nav_drawer)

        btnUserInfo = findViewById(R.id.btn_user_info);
        btnInicio = findViewById(R.id.btn_inicio_toolbar);
        btnUserInfo.setOnClickListener(this)
        btnInicio.setOnClickListener(this)

        navigationView= findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        itemAdmin = navigationView.menu.findItem(R.id.nav_item_funAdmin)
        itemCodAcc = navigationView.menu.findItem(R.id.nav_item_codigoAcceso)
        vistasXRol()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title=""
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val contentFrame = findViewById<ViewGroup>(R.id.content_frame)
        LayoutInflater.from(this).inflate(getLayoutResId(),contentFrame,true)

        val btnLogOut = findViewById<ImageButton>(R.id.btn_LogOut)
        btnLogOut.setOnClickListener{
            cerrarSesión()
        }
    }

    protected abstract fun getLayoutResId(): Int

    /*
     * Función para deshabilitar las interacciones del Navigation Drawer.
     * - Bloquea la apertura por deslizamiento (swipe).
     * - Oculta/deshabilita el icono de hamburguesa en la Toolbar.
     * - Deshabilita todos los ítems del menú del Navigation Drawer.
     */
    fun disableNavDrawerInteraction() {
        // Deshabilita la apertura del drawer por deslizamiento
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // Oculta el icono de hamburguesa en la Toolbar.
        // Esto también impide que se abra al hacer clic en él.
        toggle.isDrawerIndicatorEnabled = false
        toggle.syncState() // Sincroniza el estado del toggle después del cambio

        // Deshabilita todos los ítems del menú del NavigationView
        val menu: Menu = navigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isEnabled = false // Hace que el ítem no sea clickeable
        }
        isDrawerInteractionEnabled = false // Actualiza el estado

        btnInicio.isEnabled = false
        btnUserInfo.isEnabled = false
        //Toast.makeText(this, "Funciones del Nav Drawer deshabilitadas.", Toast.LENGTH_SHORT).show()
    }

    /*
     * Función para habilitar las interacciones del Navigation Drawer.
     * - Desbloquea la apertura por deslizamiento (swipe).
     * - Muestra/habilita el icono de hamburguesa en la Toolbar.
     * - Habilita todos los ítems del menú del Navigation Drawer.
     * (Nota: la visibilidad de los ítems seguirá siendo controlada por `vistasXRol()`).
     */
    fun enableNavDrawerInteraction() {
        // Habilita la apertura del drawer por deslizamiento
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        // Muestra el icono de hamburguesa en la Toolbar y lo hace clickeable
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState() // Sincroniza el estado del toggle después del cambio

        // Habilita todos los ítems del menú del NavigationView
        val menu: Menu = navigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isEnabled = true // Hace que el ítem sea clickeable
        }
        isDrawerInteractionEnabled = true // Actualiza el estado

        btnInicio.isEnabled = true
        btnUserInfo.isEnabled = true
        //Toast.makeText(this, "Funciones del Nav Drawer habilitadas.", Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Si la interacción del drawer está deshabilitada, no se procesa el clic
        if (!isDrawerInteractionEnabled) {
            Toast.makeText(this, "El menú de navegación está deshabilitado.", Toast.LENGTH_SHORT).show()
            drawer.closeDrawer(GravityCompat.START) // Asegúrate de cerrarlo si estaba abierto
            return true // Consumimos el evento para que no haga nada
        }

        val sharedPreferences = SharedPreferencesManager(this)

        when(item.itemId){
            R.id.nav_item_portada -> {
                val intencion1 = Intent(this, Portada::class.java)
                startActivity(intencion1)
            }
            R.id.nav_item_codigoAcceso -> {
                val intencion3 = Intent(this, codigo_acceso::class.java)
                startActivity(intencion3)
            }
            R.id.nav_item_historialAcceso -> {
                val intencion4 = Intent(this, historial_accesos::class.java)
                startActivity(intencion4)
            }
            R.id.nav_item_regRegular-> {
                val intencion5 = Intent(this, registro_regular::class.java)
                startActivity(intencion5)
            }
            R.id.nav_item_regInvitado -> {
                val intencion6 = Intent(this, registro_invitado::class.java)
                startActivity(intencion6)
            }
            R.id.nav_item_infoUsuario -> {
                val intencion7 = Intent(this, info_usuario::class.java)
                startActivity(intencion7)
            }
            R.id.nav_item_editarAreas -> {
                val intencion8 = Intent(this, editar_areas::class.java)
                startActivity(intencion8)
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Para manejar el botón de retroceso cerrando el Drawer primero
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id;
        when(id){
            R.id.btn_user_info ->{
                val intencion1 = Intent(this, info_usuario::class.java)
                startActivity(intencion1)
            }
            R.id.btn_inicio_toolbar->{
                val intencion2 = Intent(this, Portada::class.java)
                startActivity(intencion2)
            }
        }
    }

    fun cerrarSesión(){
        val preferencesManager = SharedPreferencesManager(this)
        preferencesManager.clearAllUserData()
        Firebase.auth.signOut()

        // 2. Navegar a la LoginActivity y limpiar la pila de retroceso
        val intent = Intent(this, inicio_sesion::class.java)
        // Estos flags son cruciales:
        // FLAG_ACTIVITY_NEW_TASK: Inicia la Activity en una nueva tarea.
        // FLAG_ACTIVITY_CLEAR_TASK: Borra cualquier Activity existente en la tarea.
        // Combinados, aseguran que la LoginActivity sea la única Activity en la nueva tarea.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // 3. Finalizar la Activity actual para que no se quede en la pila (aunque CLEAR_TASK ya hace esto)
        finish()
    }

    fun vistasXRol(){
        val roleManager = roleManager(this)
        if(roleManager.isAdmin()){
            itemAdmin.setVisible(true)
        } else{
            itemAdmin.setVisible(false)
        }

        if(roleManager.isAdmin() || roleManager.isInvitado()){
            itemCodAcc.setVisible(true)
        } else{
            itemCodAcc.setVisible(false)
        }
    }


}