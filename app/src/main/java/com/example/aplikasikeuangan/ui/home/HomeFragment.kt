package com.example.aplikasikeuangan.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.aplikasikeuangan.R
import com.example.aplikasikeuangan.database.TransaksiViewModel
import com.example.aplikasikeuangan.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transaksiViewModel: TransaksiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transaksiViewModel = TransaksiViewModel(requireActivity().application)

        transaksiViewModel.semuaTransaksi.observe(viewLifecycleOwner) { listTransaksi ->
            val totalPemasukan = listTransaksi
                .filter { it.kategori == "Pemasukan" }
                .sumOf { it.nominal }

            val totalPengeluaran = listTransaksi
                .filter { it.kategori == "Pengeluaran" }
                .sumOf { it.nominal }

            val saldo = totalPemasukan - totalPengeluaran

            binding.tvSaldo.text = formatRupiah(saldo)
            binding.tvPemasukan.text = formatRupiah(totalPemasukan)
            binding.tvPengeluaran.text = formatRupiah(totalPengeluaran)

            val entries = listOf(
                PieEntry(totalPemasukan.toFloat(), "Pemasukan"),
                PieEntry(totalPengeluaran.toFloat(), "Pengeluaran")
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
                valueTextColor = Color.BLACK

            }

            val pieData = PieData(dataSet)

            binding.pieChart.apply {
                data = pieData
                description.isEnabled = false
                isRotationEnabled = false
                centerText = "Ringkasan"
                setCenterTextSize(18f)
                setEntryLabelColor(Color.BLACK)
                legend.isEnabled = true
                animateY(1400)
                invalidate()
            }
        }
    }




    private fun formatRupiah(angka: Int): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(angka).replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
