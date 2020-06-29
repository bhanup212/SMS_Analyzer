package com.bhanu.smsanalyzer.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bhanu.smsanalyzer.R
import com.bhanu.smsanalyzer.SmsData
import com.bhanu.smsanalyzer.databinding.ExpensesRowItemBinding
import com.bhanu.smsanalyzer.databinding.FragmentIncomeBinding
import com.bhanu.smsanalyzer.extentions.toDate
import com.bhanu.smsanalyzer.manager.SmsWorkManager


/**
 * A simple [Fragment] subclass.
 * Use the [IncomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncomeFragment : Fragment() {

    private lateinit var adapter:IncomeAdapter
    private lateinit var binding:FragmentIncomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = ArrayList<SmsData>(SmsWorkManager.creditHashMap.values.flatten())
        list.reverse()
        adapter = IncomeAdapter(list)
        binding.incomeRv.adapter = adapter
    }

    inner class IncomeAdapter(private val list:ArrayList<SmsData>): RecyclerView.Adapter<IncomeAdapter.ViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ExpensesRowItemBinding.inflate(LayoutInflater.from(requireContext()),parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = list[position]
            holder.bindData(data)
        }

        inner class ViewHolder(private val binding: ExpensesRowItemBinding): RecyclerView.ViewHolder(binding.root){

            fun bindData(data: SmsData){
                binding.apply {
                    body.text = data.body
                    date.text = data.timeInMills.toDate()
                    amount.text = "+ Rs.${data.amount}"
                    amount.setTextColor(Color.GREEN)
                }
            }
        }
    }
}