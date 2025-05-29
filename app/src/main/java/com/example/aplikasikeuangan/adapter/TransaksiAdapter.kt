package com.example.aplikasikeuangan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikeuangan.databinding.ItemTransaksiBinding
import com.example.aplikasikeuangan.model.Transaksi
import java.text.NumberFormat
import java.util.*

class TransaksiAdapter(
    private val list: MutableList<Transaksi>, // ubah jadi MutableList biar bisa tambah data
    private val onClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    inner class TransaksiViewHolder(val binding: ItemTransaksiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaksi: Transaksi) {
            binding.tvJudul.text = transaksi.judul
            binding.tvTanggal.text = transaksi.tanggal
            binding.tvNominal.text = formatRupiah(transaksi.nominal)
            binding.tvNominal.setTextColor(
                if (transaksi.kategori == "Pemasukan") 0xFF2E7D32.toInt()
                else 0xFFC62828.toInt()
            )

            binding.root.setOnClickListener {
                onClick(transaksi)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val binding = ItemTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransaksiViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Tambahkan fungsi ini supaya fragment bisa menambah data baru
    fun tambahTransaksi(transaksi: Transaksi) {
        list.add(transaksi)
        notifyItemInserted(list.size - 1)
    }

    private fun formatRupiah(angka: Int): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(angka).replace(",00", "")
    }

    fun setData(newList: List<Transaksi>) {
        (list as MutableList).clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}
