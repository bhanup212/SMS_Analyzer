package com.bhanu.smsanalyzer.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bhanu.smsanalyzer.ui.ExpensesFragment
import com.bhanu.smsanalyzer.ui.IncomeFragment


/**
 * Created by Bhanu Prakash Pasupula on 21,Jun-2020.
 * Email: pasupula1995@gmail.com
 */
class TabAdapter(fm:FragmentManager):FragmentStatePagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0){
            IncomeFragment()
        }else{
            ExpensesFragment()
        }
    }

    override fun getCount(): Int {
       return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0){
            "Income"
        }else{
            "Expenses"
        }
    }
}