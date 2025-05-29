package com.example.aplikasikeuangan.ui.pengaturan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.aplikasikeuangan.R

class PengaturanFragment : Fragment() {

    private lateinit var themeSwitch: Switch
    private lateinit var spinnerCurrency: Spinner

    private val currencyList = listOf("IDR","USD", "EUR", "JPY", "SGD", "AUD")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pengaturan, container, false)

        themeSwitch = view.findViewById(R.id.themeSwitch)
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency)

        val sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Mode gelap
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
        }

        // Spinner mata uang
        val selectedCurrency = sharedPref.getString("currency_display", "USD") ?: "USD"
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter
        spinnerCurrency.setSelection(currencyList.indexOf(selectedCurrency))

        spinnerCurrency.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = currencyList[position]
                sharedPref.edit().putString("currency_display", selected).apply()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        return view
    }
}
