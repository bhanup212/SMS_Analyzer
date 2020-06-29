package com.bhanu.smsanalyzer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.bhanu.smsanalyzer.R
import com.bhanu.smsanalyzer.databinding.FragmentDashboardBinding
import com.bhanu.smsanalyzer.extentions.shareFile
import com.bhanu.smsanalyzer.extentions.toBitmap
import com.bhanu.smsanalyzer.manager.SmsWorkManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.exp


/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {

    companion object {
        private val TAG = "DashboardFragment"
    }

    private lateinit var binding: FragmentDashboardBinding

    private val permissionDialog = PermissionDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, Callback())
    }

    inner class Callback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        clickListeners()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requireContext().checkSelfPermission(
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionDialog.showDialog(childFragmentManager) {
                startSmsWorker()
            }
        } else {
            startSmsWorker()
        }
    }

    private fun clickListeners() {
        binding.share.setOnClickListener {
            val bitmap = binding.mainPieChart.toBitmap()
            shareIntent(bitmap)
        }
        binding.viewFullDetails.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment_to_tabFragment)
        }
    }

    private fun startSmsWorker() {
        val smsOneTimeRequest: OneTimeWorkRequest =
            OneTimeWorkRequest.Builder(SmsWorkManager::class.java).build()

        val workerManager = WorkManager.getInstance(requireContext())
        workerManager.enqueue(smsOneTimeRequest)
        observeSmsData(smsOneTimeRequest.id)
    }

    private fun observeSmsData(id: UUID) {
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(id)
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    if (it != null && it.state == WorkInfo.State.SUCCEEDED) {
                        Log.d(TAG, "work manager finished")
                        val income = it.outputData.getInt(SmsWorkManager.Income, 0)
                        val expenses = it.outputData.getInt(SmsWorkManager.Expenses, 0);
                        Log.d(TAG, "Income is: $income")
                        Log.d(TAG, "expenses is: $expenses")
                        updateChart(income, expenses)

                        val creditList = it.outputData
                        Log.d(TAG, "output data: $creditList")
                    } else {
                        Log.e(TAG, "Error: ${it.state}")
                    }
                })
    }

    private fun updateChart(income: Int, expenses: Int) {

        binding.apply {
            totalExpenses.text = "Rs.$expenses"
            totalIncome.text = "Rs.$income"
        }
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(income.toFloat(), "Income"))
            add(PieEntry(expenses.toFloat(), "Expenses"))
        }

        val pieDataSet = PieDataSet(entries, "").apply {
            colors = arrayListOf(Color.CYAN, Color.GRAY)
        }

        val data = PieData(pieDataSet)
        binding.pieChart.data = data
        binding.pieChart.description = Description().apply {
            text = ""
        }
        binding.pieChart.invalidate()
    }

    private fun shareIntent(bitmap: Bitmap) {
        val folder = File(context?.cacheDir, "Pictures")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, "pie_chart_${Date().time}.jpg")
        if (!file.exists()) {
            file.createNewFile()

            val fos = FileOutputStream(file)
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                Log.e(TAG, "writing file error: ${e.message}")
            }
        }
        context?.shareFile(file)

    }
}