package com.example.wordle.fragments

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
import com.example.wordle.model.GameViewModel

/**
 * Settings Fragment where user can change their preferences and reset app to default state
 */
class SettingsFragment : Fragment() {

    // Binding object instance corresponding to the fragment_settings.xml layout
    private var binding: FragmentSettingsBinding? = null

    // reference to the viewModel
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SettingsFragment", "onCreateView() called")
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("SettingsFragment", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner
            // Assign the view model to a property in the binding class
            viewModel = gameViewModel
            // Assign the fragment
            settingsFragment = this@SettingsFragment
        }
    }

    fun reset() {
        findNavController().navigate(R.id.action_settingsFragment_to_resetDialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}