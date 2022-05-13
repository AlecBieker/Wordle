package com.example.wordle.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.FragmentSettingsBinding
import com.example.wordle.ui.model.SettingsViewModel

/**
 * Settings Fragment where user can change their preferences and reset app to default state
 */
class SettingsFragment : Fragment() {

    // Binding object instance corresponding to the fragment_settings.xml layout
    private var binding: FragmentSettingsBinding? = null

    // reference to the viewModel
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SettingsFragment", "onCreateView() called")
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SettingsFragment", "onViewCreated() called")
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner
            // Assign the view model to a property in the binding class
            viewModel = settingsViewModel
            // Assign the fragment
            settingsFragment = this@SettingsFragment
        }
    }

    fun reset() {
        Log.d("SettingsFragment", "reset() called")
        findNavController().navigate(R.id.action_settingsFragment_to_resetDialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SettingsFragment", "onDestroyView() called")
        binding = null
    }
}