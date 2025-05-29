package com.example.aplikasikeuangan.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikeuangan.adapter.TransaksiAdapter
import com.example.aplikasikeuangan.databinding.FragmentTransaksiBinding
import com.example.aplikasikeuangan.model.Transaksi
import com.example.aplikasikeuangan.ui.detail.DetailTransaksiActivity
import com.example.aplikasikeuangan.database.TransaksiViewModel

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!

    private val transaksiViewModel: TransaksiViewModel by viewModels()
    private lateinit var transaksiAdapter: TransaksiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTransaksi()

        binding.fabTambah.setOnClickListener {
            val intent = Intent(requireContext(), TambahTransaksiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        transaksiAdapter = TransaksiAdapter(mutableListOf()) { transaksi ->
            val intent = Intent(requireContext(), DetailTransaksiActivity::class.java)
            intent.putExtra("transaksi", transaksi)
            startActivity(intent)
        }

        binding.rvTransaksi.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transaksiAdapter
        }
    }

    private fun observeTransaksi() {
        transaksiViewModel.semuaTransaksi.observe(viewLifecycleOwner, Observer { list ->
            transaksiAdapter.setData(list)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
