package com.bhanu.smsanalyzer.ui

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bhanu.smsanalyzer.R

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    private val handler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onResume() {
        super.onResume()
        splashScreenDelay()
    }

    private fun splashScreenDelay() {
        handler.postDelayed({ navigateToDoublePlan() }, SPLASH_SCREEN_DELAY)
    }

    private fun navigateToDoublePlan() {
        try {
            findNavController().navigate(R.id.splashFragment_to_dashboardFragment)
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(null)
    }

    companion object {
        const val SPLASH_SCREEN_DELAY: Long = 2000
    }

}