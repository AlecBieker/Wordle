package com.example.wordle.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.wordle.databinding.DialogResetBinding
import com.example.wordle.model.GameViewModel

/**
 * This is the dialog that displays when the user taps the new game button on the start screen
 * while a game is currently in progress
 */
class ResetDialog : DialogFragment() {

    // Binding object instance corresponding to the dialog_new_game.xml layout
    private var binding: DialogResetBinding? = null

    private val viewModel: GameViewModel by activityViewModels()

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
            viewModel = viewModel

            // Assign the fragment
            resetDialog = this@ResetDialog
        }
    }

    fun reset() {
        viewModel.resetStats()
        Toast.makeText(context, "Statistics Reset", Toast.LENGTH_LONG).show()
        dismiss()
    }

    fun cancel() {
        dismiss()
    }

    override fun onDestroyView() {
        Log.d("ResetDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}
