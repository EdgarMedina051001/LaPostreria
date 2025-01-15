package com.example.lapostreria

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.lapostreria.databinding.ActivityPagoBinding
import com.example.lapostreria.model.DetallesOrden
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PagoActivity : AppCompatActivity() {

    lateinit var binding: ActivityPagoBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var nombre:String
    private lateinit var direccion:String
    private lateinit var celular:String
    private lateinit var montoTotal:String
    private lateinit var metodoPago:String
    private lateinit var postreItemNombre:ArrayList<String>
    private lateinit var postreItemPrecio:ArrayList<String>
    private lateinit var postreItemImagen:ArrayList<String>
    private lateinit var postreItemDescripcion:ArrayList<String>
    private lateinit var postreItemIngrediente:ArrayList<String>
    private lateinit var postreItemCantidad:ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var spinnerMetodoPago: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Incializar Firebase y Usuario Detalles
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()
        //Traer datos del usuario
        traerDatosUsuario()

        //Traer detalles del usuario de firebase
        val intent = intent
        postreItemNombre = intent.getStringArrayListExtra("PostreItemNombre") as ArrayList<String>
        postreItemPrecio = intent.getStringArrayListExtra("PostreItemPrecio") as ArrayList<String>
        postreItemImagen = intent.getStringArrayListExtra("PostreItemImagen") as ArrayList<String>
        postreItemDescripcion = intent.getStringArrayListExtra("PostreItemDescripcion") as ArrayList<String>
        postreItemIngrediente = intent.getStringArrayListExtra("PostreItemIngrediente") as ArrayList<String>
        postreItemCantidad = intent.getIntegerArrayListExtra("PostreItemCantidad") as ArrayList<Int>

        montoTotal = calcularMontoTotal().toString() + "MX$"
        /*binding.tvMontoTotalPago.isEnabled = false*/
        binding.tvMontoTotalPago.setText(montoTotal)


        binding.btnRealizarPedido.setOnClickListener {
            //Obtener datos de los textView
            nombre = binding.tietNombrePago.text.toString().trim()
            direccion = binding.tietDireccionPago.text.toString().trim()
            celular = binding.tietCelularPago.text.toString().trim()
            /*metodoPago = listOf(binding.spinnerMetodoPago.toString().trim()).toString()*/
            
            if (nombre.isBlank()||direccion.isBlank()||celular.isBlank()){
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            }else{
                realizarOrden()
            }
        }

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.Color2)

        //Instancia spinner
        spinnerMetodoPago = findViewById(R.id.spinnerMetodoPago)
        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configurar adapter para el Spinner
        val metodoPago= listOf("EFECTIVO 💵", "TRANSFERENCIA 💳")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, metodoPago)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodoPago.adapter = spinnerAdapter

        spinnerMetodoPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                this@PagoActivity.metodoPago = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun realizarOrden() {
        userId = auth.currentUser?.uid?:""
        val tiempo = System.currentTimeMillis()
        val itemPushLlave = databaseReference.child("DetallesOrden").push().key
        val detalllesOrden = DetallesOrden(userId, nombre, postreItemNombre, postreItemPrecio, postreItemImagen, postreItemCantidad
            , direccion, montoTotal, metodoPago, celular, tiempo, itemPushLlave, false, false, )
        val ordenReference = databaseReference.child("DetallesOrden").child(itemPushLlave!!)
        ordenReference.setValue(detalllesOrden).addOnSuccessListener {
            val bottomSheetDialog = CumplidoBottomSheetFragment()
            bottomSheetDialog.show(supportFragmentManager, "TEST")
            removerItemDelCarrito()
            agregarOrdenAlHistorial(detalllesOrden)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al hacer tu pedido. Intenta Nuevamente.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun agregarOrdenAlHistorial(detalllesOrden: DetallesOrden) {
        databaseReference.child("Usuarios").child(userId).child("HistorialCompra")
            .child(detalllesOrden.itemPushLlave!!)
            .setValue(detalllesOrden).addOnSuccessListener {

            }


    }

    private fun removerItemDelCarrito() {
        val carritoItemsReferencia = databaseReference.child("Usuarios").child(userId).child("CarritoItems")
        carritoItemsReferencia.removeValue()
    }

    private fun calcularMontoTotal(): Int {
        var montoTotal = 0
        for (i in 0 until postreItemPrecio.size){
            var precio = postreItemPrecio[i]
            val lastChar = precio.last()
            val precioIntValor = if (lastChar == '$'){
                precio.dropLast(1).toInt()
            } else {
                precio.toInt()
            }
            var cantidad = postreItemCantidad[i]
            montoTotal += precioIntValor * cantidad
        }
        return montoTotal
    }

    private fun traerDatosUsuario() {
        val usuario=auth.currentUser
        if (usuario!=null){
            val usuarioId = usuario.uid
            val usuarioRef = databaseReference.child("Usuarios").child(usuarioId)

            usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val nombres = snapshot.child("nombre").getValue(String::class.java)?:""
                        val direcciones = snapshot.child("direccion").getValue(String::class.java)?:""
                        val celulares = snapshot.child("celular").getValue(String::class.java)?:""

                        binding.apply {
                            tietNombrePago.setText(nombres)
                            tietDireccionPago.setText(direcciones)
                            tietCelularPago.setText(celulares)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
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