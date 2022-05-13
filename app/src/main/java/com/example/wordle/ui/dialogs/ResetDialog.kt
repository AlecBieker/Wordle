package com.example.wordle.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.wordle.databinding.DialogResetBinding
import com.example.wordle.ui.model.SettingsViewModel

/**
 * This is the confirmation dialog that displays when the user taps the reset statistics button
 * on the settings screen
 */
class ResetDialog : DialogFragment() {

    // Binding object instance corresponding to the [dialog_reset.xml] layout
    private var binding: DialogResetBinding? = null

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ResetDialog", "onCreateView() called")
        binding = DialogResetBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("ResetDialog", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = settingsViewModel

            // Assign the fragment
            resetDialog = this@ResetDialog
        }
    }

    fun reset() {
        Log.d("ResetDialog", "reset() called")
        settingsViewModel.reset()
        Toast.makeText(context, "Statistics Reset", Toast.LENGTH_LONG).show()
        dismiss()
    }

    fun cancel() {
        Log.d("ResetDialog", "cancel() called")
        dismiss()
    }

    override fun onDestroyView() {
        Log.d("ResetDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}
