package com.bhanu.smsanalyzer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bhanu.smsanalyzer.R
import com.bhanu.smsanalyzer.databinding.FragmentTabsBinding
import com.bhanu.smsanalyzer.ui.adapter.TabAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [TabsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TabsFragment : Fragment() {

    private lateinit var adapter:TabAdapter

    private lateinit var binding:FragmentTabsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TabAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)
        binding.viewpager.adapter = adapter
    }
}