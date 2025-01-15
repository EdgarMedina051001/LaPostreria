package com.example.lapostreria.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lapostreria.Adapter.CarritoAdapter
import com.example.lapostreria.PagoActivity
import com.example.lapostreria.R
import com.example.lapostreria.databinding.FragmentCarritoBinding
import com.example.lapostreria.model.CarritoItems
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarritoFragment : Fragment() {
    private lateinit var binding: FragmentCarritoBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var postreNombres: MutableList<String>
    private lateinit var postrePrecios: MutableList<String>
    private lateinit var postreImagenesUri: MutableList<String>
    private lateinit var postreDescripciones: MutableList<String>
    private lateinit var postreIngredientes: MutableList<String>
    private lateinit var cantidad: MutableList<Int>
    private lateinit var carritoAdapter: CarritoAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCarritoBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        recuperarCarritoItems()

        binding.btnContinuar.setOnClickListener {
            //Obtener los detalles de la orden despues del carrito.
            ObtenerDetallesItemsOrden()
        }
        return binding.root
    }

    private fun ObtenerDetallesItemsOrden() {

        val ordenIdReference: DatabaseReference =
            database.reference.child("Usuarios").child(userId).child("CarritoItems")

        val postreNombre = mutableListOf<String>()
        val postrePrecio = mutableListOf<String>()
        val postreDescripcion = mutableListOf<String>()
        val postreImagen = mutableListOf<String>()
        val postreIngrediente = mutableListOf<String>()
        //Obtener cantidad item
        val postreCantidad = carritoAdapter.obtenerItemActualizado()

        ordenIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postreSnapshot in snapshot.children) {
                    val ordenItems = postreSnapshot.getValue(CarritoItems::class.java)

                    ordenItems?.postreNombre?.let { postreNombre.add(it) }
                    ordenItems?.postrePrecio?.let { postrePrecio.add(it) }
                    ordenItems?.postreDescripcion?.let { postreDescripcion.add(it) }
                    ordenItems?.postreImagen?.let { postreImagen.add(it) }
                    ordenItems?.postreIngrediente?.let { postreIngrediente.add(it) }
                }
                ordenarAhora(
                    postreNombre,
                    postrePrecio,
                    postreDescripcion,
                    postreImagen,
                    postreIngrediente,
                    postreCantidad
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Algo salio mal. Intenta otra vez.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun ordenarAhora(
        postreNombre: MutableList<String>,
        postrePrecio: MutableList<String>,
        postreDescripcion: MutableList<String>,
        postreImagen: MutableList<String>,
        postreIngrediente: MutableList<String>,
        postreCantidad: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PagoActivity::class.java)
            intent.putExtra("PostreItemNombre", postreNombre as ArrayList<String>)
            intent.putExtra("PostreItemPrecio", postrePrecio as ArrayList<String>)
            intent.putExtra("PostreItemImagen", postreImagen as ArrayList<String>)
            intent.putExtra("PostreItemDescripcion", postreDescripcion as ArrayList<String>)
            intent.putExtra("PostreItemIngrediente", postreIngrediente as ArrayList<String>)
            intent.putExtra("PostreItemCantidad", postreCantidad as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun recuperarCarritoItems() {
        //
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val postreRef: DatabaseReference =
            database.reference.child("Usuarios").child(userId).child("CarritoItems")

        postreNombres = mutableListOf()
        postrePrecios = mutableListOf()
        postreDescripciones = mutableListOf()
        postreImagenesUri = mutableListOf()
        postreIngredientes = mutableListOf()
        cantidad = mutableListOf()

        postreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postreSnapshot in snapshot.children) {
                    val carritoItems = postreSnapshot.getValue(CarritoItems::class.java)

                    carritoItems?.postreNombre?.let { postreNombres.add(it) }
                    carritoItems?.postrePrecio?.let { postrePrecios.add(it) }
                    carritoItems?.postreDescripcion?.let { postreDescripciones.add(it) }
                    carritoItems?.postreImagen?.let { postreImagenesUri.add(it) }
                    carritoItems?.postreCantidad?.let { cantidad.add(it) }
                    carritoItems?.postreIngrediente?.let { postreIngredientes.add(it) }
                }

                setAdapter()
            }

            private fun setAdapter() {
                 carritoAdapter = CarritoAdapter(
                    requireContext(),
                    postreNombres,
                    postrePrecios,
                    postreDescripciones,
                    postreImagenesUri,
                    cantidad,
                    postreIngredientes
                )
                binding.carritorecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.carritorecyclerView.adapter = carritoAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Dato no traido.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {}

}