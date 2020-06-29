package com.bhanu.smsanalyzer.ui

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.invoke
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bhanu.smsanalyzer.databinding.FragmentPermissionDialogBinding


/**
 * A simple [Fragment] subclass.
 * Use the [PermissionDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PermissionDialogFragment : DialogFragment() {

    companion object{
        private const val TAG = "PermissionDialogFragment"
        private const val PERMISSION_CODE = 101
    }

    private lateinit var binding: FragmentPermissionDialogBinding
    private var onPermissionAllowed: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentPermissionDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
    }
    private fun clickListeners(){
        binding.btnAllowPermission.setOnClickListener {
            requestSmsPermission()
        }
    }
    fun showDialog(manager:FragmentManager,onPermissionAllowed: () -> Unit = {}){
        this.onPermissionAllowed = onPermissionAllowed
        show(manager, TAG)
    }

    private fun requestSmsPermission(){
        askSmsPermission(Manifest.permission.READ_SMS)
    }

    private val askSmsPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if(result){
            Log.e("TAG", "Sms permnission granted")
            this.dismissAllowingStateLoss()
            onPermissionAllowed()
        }else{
            Log.e("TAG", "sms permnission denied")
        }
    }

}