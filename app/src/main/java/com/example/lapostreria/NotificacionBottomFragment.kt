package com.example.lapostreria

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lapostreria.Adapter.NotificacionAdapter
import com.example.lapostreria.databinding.FragmentNotificacionBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificacionBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificacionBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificacionBottomBinding.inflate(layoutInflater, container, false)
        val notificaciones = listOf("Tu Pedido ha Sido Cancelado Correctamente.", "Tu Pedido a sido Recogido Por El Repartidor.", "Pedido Entregado. Gracias por tu Pedido.")
        val notificacionesImagenes = listOf(R.drawable.ic_cancelar, R.drawable.ic_repartidor, R.drawable.ic_completado)
        val adapter = NotificacionAdapter(
            ArrayList(notificaciones),
            ArrayList(notificacionesImagenes)
        )
        binding.notificacionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificacionRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {
    }
}