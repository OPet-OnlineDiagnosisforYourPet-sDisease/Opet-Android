package com.example.meowbottom.ui.location

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.meowbottom.R
import com.example.meowbottom.databinding.FragmentLocationBinding
import com.example.meowbottom.ui.map.MapsActivity


class LocationFragment : Fragment() {

    private lateinit var binding: FragmentLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ivLocCommunity.setOnClickListener {
            val intentLocCom = Intent(context, MapsActivity::class.java)
            intentLocCom.putExtra("TARGET", "Community")
            startActivity(intentLocCom)
        }
        binding.ivRs.setOnClickListener {
            val intentLocRs = Intent(context, MapsActivity::class.java)
            intentLocRs.putExtra("TARGET", "Clinic")
            startActivity(intentLocRs)
        }
    }

    companion object {

        fun newInstance(): LocationFragment {
            return LocationFragment()
        }
    }
}