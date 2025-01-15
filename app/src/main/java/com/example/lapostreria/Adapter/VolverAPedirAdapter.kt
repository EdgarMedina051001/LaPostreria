package com.example.lapostreria.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lapostreria.databinding.ComprarNuevamenteItemBinding

class VolverAPedirAdapter(private val volverPedirPostreNombre:MutableList<String>,
                          private val volverPedirPostrePrecio:MutableList<String>,
                          private val volverPedirPostreImagen:MutableList<String>,
                            private var requireContext: Context) :
    RecyclerView.Adapter<VolverAPedirAdapter.VolverAPedirViewHolder>() {

    override fun onBindViewHolder(holder: VolverAPedirViewHolder, position: Int) {
        holder.bind(volverPedirPostreNombre[position], volverPedirPostrePrecio[position], volverPedirPostreImagen[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolverAPedirViewHolder {
        val binding = ComprarNuevamenteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolverAPedirViewHolder(binding)
    }

    override fun getItemCount(): Int = volverPedirPostreNombre.size

    inner class VolverAPedirViewHolder(private val binding: ComprarNuevamenteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(PostreNombre: String, PostrePrecio: String, PostreImagen: String) {
            binding.tvNombrePostreCN.text = PostreNombre
            binding.tvPrecioPostreCN.text = PostrePrecio
            val uriString = PostreImagen
            val uri = Uri.parse(uriString)
            Glide.with(requireContext).load(uri).into(binding.ivImagenPostreCN)

        }

    }

}