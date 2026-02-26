package com.example.expensetracker.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.expensetracker.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        observeViewModel()
    }

    private fun setupCharts() {
        // Pie chart setup
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 55f
            setHoleColor(Color.TRANSPARENT)
            setUsePercentValues(true)
            legend.isEnabled = true
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(11f)
            setNoDataText("No expense data yet")
        }

        // Bar chart setup
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            legend.isEnabled = false
            setNoDataText("No data available")
            animateY(800)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.chartData.collect { data ->
                        updatePieChart(data.pieEntries)
                        updateBarChart(data.barEntries)
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.progressBar.isVisible = loading
                    }
                }
            }
        }
    }

    private fun updatePieChart(data: Map<String, Float>) {
        if (data.isEmpty()) {
            binding.pieChart.clear()
            return
        }
        val entries = data.map { PieEntry(it.value, it.key) }
        val dataSet = PieDataSet(entries, "Categories").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList() + ColorTemplate.VORDIPLOM_COLORS.toList()
            valueTextSize = 10f
            valueTextColor = Color.WHITE
            sliceSpace = 2f
        }
        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
        binding.pieChart.animateY(800)
    }

    private fun updateBarChart(data: Map<String, Float>) {
        if (data.isEmpty()) {
            binding.barChart.clear()
            return
        }
        val labels = data.keys.toList()
        val entries = data.values.mapIndexed { idx, value -> BarEntry(idx.toFloat(), value) }
        val dataSet = BarDataSet(entries, "Monthly Expenses").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 10f
        }
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.data = BarData(dataSet)
        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
