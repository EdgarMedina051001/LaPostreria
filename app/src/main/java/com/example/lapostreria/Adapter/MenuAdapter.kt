package com.example.lapostreria.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lapostreria.DetallesActivity
import com.example.lapostreria.databinding.MenuItemBinding
import com.example.lapostreria.model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    abrirActivityDetalles(position)
                }
            }
        }

        private fun abrirActivityDetalles(position: Int) {
            val menuItem = menuItems[position]
            //
            val intent = Intent(requireContext, DetallesActivity::class.java).apply {
                putExtra("MenuItemNombre", menuItem.postreNombre)
                putExtra("MenuItemImagen", menuItem.postreImagen)
                putExtra("MenuItemDescripcion", menuItem.postreDescripcion)
                putExtra("MenuItemIngredientes", menuItem.postreIngredientes)
                putExtra("MenuItemPrecio", menuItem.postrePrecio)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                tvNombrePostreMenu.text = menuItem.postreNombre
                tvPrecioPostreMenu.text = menuItem.postrePrecio
                val uri = Uri.parse(menuItem.postreImagen)
                Glide.with(requireContext).load(uri).into(civImagenPostreMenu)
            }
        }

    }
}
