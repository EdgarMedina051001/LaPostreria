package com.example.lapostreria

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.lapostreria.databinding.ActivityDetallesBinding
import com.example.lapostreria.model.CarritoItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.log

class DetallesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetallesBinding

    private var postreDescripcion : String? = null
    private var postreImagen : String? = null
    private var postreNombre : String? = null
    private var postreIngredientes : String? = null
    private var postrePrecio : String? = null
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.Color2)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnAgregarCarritoDetalles.setOnClickListener {
            agregarItemAlCarrito()
        }

        postreNombre = intent.getStringExtra("MenuItemNombre")
        postrePrecio = intent.getStringExtra("MenuItemPrecio")
        postreDescripcion = intent.getStringExtra("MenuItemDescripcion")
        postreIngredientes = intent.getStringExtra("MenuItemIngredientes")
        postreImagen = intent.getStringExtra("MenuItemImagen")

        with(binding){
            tvNombrePostreDetalles.text=postreNombre
            tvPrecioPostreDetalles.text=postrePrecio
            tvDescripcionPostreDetalles.text=postreDescripcion
            tvIngredientesPostreDetalles.text=postreIngredientes

            Glide.with(this@DetallesActivity).load(Uri.parse(postreImagen)).into(ivImagenPostreDetalles)
        }
    }

    private fun agregarItemAlCarrito() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid?:""

        //Crear un objeto Carrito
        val carritoItem = CarritoItems(postreNombre.toString(), postrePrecio.toString(), postreDescripcion.toString(),postreImagen.toString(), 1, postreIngredientes.toString())
        //Guardar datos para el carrito de Firebase database
        database.child("Usuarios").child(userId).child("CarritoItems").push().setValue(carritoItem).addOnSuccessListener {
            Toast.makeText(this, "Se ha agregado al carrito correctamente.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "No se agrego al carrito.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}