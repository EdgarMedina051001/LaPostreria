package com.example.lapostreria

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lapostreria.Adapter.CompraRecienteAdapter
import com.example.lapostreria.databinding.ActivityOrdenRecienteItemsBinding
import com.example.lapostreria.model.DetallesOrden

class ordenRecienteItemsActivity : AppCompatActivity() {
    private val binding: ActivityOrdenRecienteItemsBinding by lazy {
        ActivityOrdenRecienteItemsBinding.inflate(layoutInflater)
    }

    private lateinit var todosPostreNombres: ArrayList<String>
    private lateinit var todosPostrePrecios: ArrayList<String>
    private lateinit var todosPostreImagenes: ArrayList<String>
    private lateinit var todosPostreCantidades: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.Color2)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        //
        val ordenRecienteItems =
            intent.getSerializableExtra("CompraRecienteOrdenItem") as ArrayList<DetallesOrden>
        ordenRecienteItems?.let { detallesOrden ->
            if (detallesOrden.isNotEmpty()) {
                val ordenRecienteItem = detallesOrden[0]
                todosPostreNombres = ordenRecienteItem.postreNombres as ArrayList<String>
                todosPostreImagenes = ordenRecienteItem.postreImagenes as ArrayList<String>
                todosPostrePrecios = ordenRecienteItem.postrePrecios as ArrayList<String>
                todosPostreCantidades = ordenRecienteItem.postreCantidades as ArrayList<Int>

            }
        }
        asignarAdapter()
    }

    private fun asignarAdapter() {
        val recyclerView = binding.ordenRecienteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CompraRecienteAdapter(this, todosPostreNombres, todosPostreImagenes,todosPostrePrecios, todosPostreCantidades)
        recyclerView.adapter = adapter
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