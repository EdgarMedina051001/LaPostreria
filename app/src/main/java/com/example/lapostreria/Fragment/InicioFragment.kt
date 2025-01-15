package com.example.lapostreria.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.lapostreria.Adapter.MenuAdapter
import com.example.lapostreria.MenuBottomSheetFragment
import com.example.lapostreria.R
import com.example.lapostreria.databinding.FragmentInicioBinding
import com.example.lapostreria.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InicioFragment : Fragment() {
    private lateinit var binding: FragmentInicioBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInicioBinding.inflate(inflater, container, false)

        binding.tvVerMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "TEST")
        }

        //
        recuperarMostrarRecomendadoItems()
        return binding.root

    }

    private fun recuperarMostrarRecomendadoItems() {
        //Obtener referencia de la base de datos
        database = FirebaseDatabase.getInstance()
        val postreRef = database.reference.child("Menú")
        menuItems = mutableListOf()

        //Obtener items del menu de la base de datos
        postreRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postreSnapshot in snapshot.children){
                    val menuItem = postreSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                //Mostrar un postre random
                randomPostreItem()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun randomPostreItem() {
        //Crear como shuffled list del menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemParaMostrar = 6
        val subsetMenuItems = index.take(numItemParaMostrar).map { menuItems[it] }

        setRecomendadoItemsAdapter(subsetMenuItems)
    }

    private fun setRecomendadoItemsAdapter(subsetMenuItems: List<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItems, requireContext())
        binding.recomendadoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recomendadoRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaImagenes = ArrayList<SlideModel>()
        listaImagenes.add(SlideModel(R.drawable.banner1, ScaleTypes.CENTER_CROP))
        listaImagenes.add(SlideModel(R.drawable.banner2, ScaleTypes.CENTER_CROP))
        listaImagenes.add(SlideModel(R.drawable.banner3, ScaleTypes.CENTER_CROP))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(listaImagenes)
        imageSlider.setImageList(listaImagenes, ScaleTypes.CENTER_CROP)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosicion = listaImagenes[position]
                val itemMensaje = "Imagen Seleccionada: $position"
                Toast.makeText(requireContext(), itemMensaje, Toast.LENGTH_SHORT).show()
            }
        })

    }

}