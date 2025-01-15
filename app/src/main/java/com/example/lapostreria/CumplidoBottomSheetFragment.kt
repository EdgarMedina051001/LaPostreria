package com.example.lapostreria

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lapostreria.databinding.FragmentCumplidoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CumplidoBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCumplidoBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCumplidoBottomSheetBinding.inflate(layoutInflater, container, false)
        binding.btnIrMenu.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    companion object {

    }
}