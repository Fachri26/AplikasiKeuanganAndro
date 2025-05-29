package com.example.aplikasikeuangan.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikeuangan.database.TransaksiViewModel
import com.example.aplikasikeuangan.databinding.ActivityDetailTransaksiBinding
import com.example.aplikasikeuangan.model.Transaksi
import java.text.NumberFormat
import java.util.*

class DetailTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransaksiBinding
    private val transaksiViewModel: TransaksiViewModel by viewModels()
    private var transaksi: Transaksi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaksi = intent.getParcelableExtra("transaksi")
        transaksi?.let { tampilkanDetail(it) }

        binding.btnHapus.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Hapus Transaksi")
            builder.setMessage("Apakah Anda yakin ingin menghapus transaksi ini?")

            builder.setPositiveButton("Ya") { _, _ ->
                transaksi?.let {
                    transaksiViewModel.delete(it)
                    Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun tampilkanDetail(transaksi: Transaksi) {
        binding.tvJudul.text = transaksi.judul
        binding.tvNominal.text = formatRupiah(transaksi.nominal)
        binding.tvTanggal.text = transaksi.tanggal
        binding.tvKategori.text = transaksi.kategori
    }

    private fun formatRupiah(angka: Int): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(angka).replace(",00", "")
    }
}

