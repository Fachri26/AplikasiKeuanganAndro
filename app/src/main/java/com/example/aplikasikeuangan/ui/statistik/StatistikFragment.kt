package com.example.aplikasikeuangan.ui.statistik

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aplikasikeuangan.databinding.FragmentStatistikBinding
import com.example.aplikasikeuangan.model.Transaksi
import com.example.aplikasikeuangan.database.MilestoneViewModel
import com.example.aplikasikeuangan.database.TransaksiViewModel
import com.example.aplikasikeuangan.network.RetrofitClient
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StatistikFragment : Fragment() {

    private var _binding: FragmentStatistikBinding? = null
    private val binding get() = _binding!!

    private val transaksiViewModel: TransaksiViewModel by viewModels()
    private val milestoneViewModel: MilestoneViewModel by viewModels()

    private var currencyRate = 1.0
    private var currencySymbol = "Rp"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatistikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val selectedCurrency = sharedPref.getString("currency_display", "IDR") ?: "IDR"

        if (selectedCurrency != "IDR") {
            getExchangeRate("IDR", selectedCurrency)
        } else {
            currencySymbol = "Rp"
            currencyRate = 1.0
            observeRingkasan()
            observeTransaksiChart()
            observeMilestoneAktif()
        }

        binding.btnSisihkan.setOnClickListener { showSisihkanDialog() }

        binding.btnResetMilestone.setOnClickListener {
            milestoneViewModel.resetMilestone()
            Toast.makeText(requireContext(), "Milestone direset", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuatMilestone.setOnClickListener {
            startActivity(Intent(requireContext(), BuatMilestoneActivity::class.java))
        }
    }

    private fun getExchangeRate(base: String, target: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val accessKey = "f4701638ea595f77bc8cc12ca905fd24"
                val response = RetrofitClient.api.getRates(accessKey, base, target)
                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        if (!isAdded || _binding == null) return@withContext

                        val key = base + target
                        currencyRate = body?.quotes?.get(key) ?: 1.0
                        currencySymbol = getSymbolFromCurrency(target)

                        Log.d("EXCHANGE_RATE", "Rate: $currencyRate $currencySymbol")

                        observeRingkasan()
                        observeTransaksiChart()
                        observeMilestoneAktif()
                    }
                } else {
                    Log.e("EXCHANGE_RATE", "Gagal: ${response.code()} - ${response.message()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Gagal mengambil kurs", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("EXCHANGE_RATE", "Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun getSymbolFromCurrency(code: String): String {
        return when (code) {
            "IDR" -> "Rp"
            "USD" -> "$"
            "EUR" -> "€"
            "JPY" -> "¥"
            "GBP" -> "£"
            "SGD" -> "S$"
            "AUD" -> "A$"
            else -> code
        }
    }

    private fun observeRingkasan() {
        transaksiViewModel.semuaTransaksi.observe(viewLifecycleOwner) { list ->
            val binding = _binding ?: return@observe

            val pemasukan = list.filter { it.kategori == "Pemasukan" }.sumOf { it.nominal }
            val pengeluaran = list.filter { it.kategori == "Pengeluaran" }.sumOf { it.nominal }
            val saldo = pemasukan - pengeluaran

            val pemasukanConverted = pemasukan.toDouble() * currencyRate
            val pengeluaranConverted = pengeluaran.toDouble() * currencyRate
            val saldoConverted = saldo.toDouble() * currencyRate

            binding.tvTotalPemasukan.text = "Pemasukan: $currencySymbol ${"%,.2f".format(pemasukanConverted)}"
            binding.tvTotalPengeluaran.text = "Pengeluaran: $currencySymbol ${"%,.2f".format(pengeluaranConverted)}"
            binding.tvSaldo.text = "Total: $currencySymbol ${"%,.2f".format(saldoConverted)}"

            val entries = listOf(
                PieEntry(pemasukanConverted.toFloat()),
                PieEntry(pengeluaranConverted.toFloat())
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
                valueTextColor = Color.BLACK
                valueTextSize = 0.1f
            }

            val pieData = PieData(dataSet)
            binding.pieChart.apply {
                data = pieData
                description.isEnabled = false
                isRotationEnabled = false
                centerText = "Ringkasan"
                setCenterTextSize(0.1f)
                setEntryLabelColor(Color.BLACK)
                legend.isEnabled = true
                animateY(1400)
                invalidate()
            }
        }
    }

    private fun observeTransaksiChart() {
        transaksiViewModel.semuaTransaksi.observe(viewLifecycleOwner) { list ->
            val binding = _binding ?: return@observe

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val entries = mutableListOf<BarEntry>()
            val map = mutableMapOf<Int, Int>()

            for (i in 6 downTo 0) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val date = sdf.format(calendar.time)
                val total = list.filter { it.tanggal == date }
                    .sumOf { if (it.kategori == "Pemasukan") it.nominal else -it.nominal }
                map[6 - i] = total
            }

            for ((x, y) in map) {
                entries.add(BarEntry(x.toFloat(), (y * currencyRate).toFloat()))
            }

            val dataSet = BarDataSet(entries, "Transaksi Mingguan")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            val data = BarData(dataSet)

            binding.barChart.apply {
                this.data = data
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                description.isEnabled = false
                animateY(1100)
                invalidate()
            }
        }
    }

    private fun observeMilestoneAktif() {
        milestoneViewModel.activeMilestone.observe(viewLifecycleOwner) { milestone ->
            val binding = _binding ?: return@observe

            if (milestone != null) {
                binding.milestoneCard.visibility = View.VISIBLE
                binding.btnBuatMilestone.visibility = View.GONE
                binding.tvJudulMilestone.text = milestone.judul

                val persen = (milestone.terkumpul * 100 / milestone.target).coerceAtMost(100)
                binding.progressMilestone.progress = persen

                val terkumpulConverted = milestone.terkumpul * currencyRate
                val targetConverted = milestone.target * currencyRate

                binding.tvDetailMilestone.text =
                    "$currencySymbol ${"%.2f".format(terkumpulConverted)} / $currencySymbol ${"%.2f".format(targetConverted)} ($persen%)"
            } else {
                binding.milestoneCard.visibility = View.GONE
                binding.btnBuatMilestone.visibility = View.VISIBLE
            }
        }
    }

    private fun showSisihkanDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Masukkan nominal"

        AlertDialog.Builder(requireContext())
            .setTitle("Sisihkan ke Milestone")
            .setView(editText)
            .setPositiveButton("Simpan") { _, _ ->
                val input = editText.text.toString().toIntOrNull()
                if (input != null && input > 0) {
                    milestoneViewModel.sisihkanUang(input) { judulMilestone ->
                        val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val transaksi = Transaksi(
                            judul = "Sisihkan ke $judulMilestone",
                            nominal = input,
                            tanggal = tanggal,
                            kategori = "Pengeluaran"
                        )
                        transaksiViewModel.insert(transaksi)
                        Toast.makeText(requireContext(), "Berhasil disisihkan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Nominal tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
