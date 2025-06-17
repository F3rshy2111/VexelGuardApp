package com.example.iniciosesion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.iniciosesion.navDrawer;

public class Portada extends navDrawer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_portada2);
        //Toast.makeText(this, "Bienvenido a Home!", Toast.LENGTH_SHORT).show();
    }

    // Debes sobrescribir este metodo para indicar el layout específico de esta Activity Java
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_portada2; // Asegúrate de tener este layout XML creado
    }


}