package com.example.wordle.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.DialogNewGameBinding
import com.example.wordle.model.GameViewModel

/**
* This is the confirmation dialog that displays when the user taps the new game button 
 * on the start screen while a game is currently in progress.
 */
class NewGameDialog : DialogFragment() {

    // Binding object instance corresponding to the [dialog_new_game.xml] layout
    private var binding: DialogNewGameBinding? = null

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("NewGameDialog", "onCreateView() called")
        binding = DialogNewGameBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("NewGameDialog", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = viewModel

            // Assign the fragment
            newGameDialog = this@NewGameDialog
        }
    }

    fun newGame() {
        Log.d("NewGameDialog", "newGame() called")
        viewModel.updateStats(false)
        viewModel.newGame()

        findNavController().navigate(R.id.action_newGameDialog_to_gameFragment)
    }

    fun cancel() {
        Log.d("NewGameDialog", "cancel() called")
        dismiss()
    }

    override fun onDestroyView() {
        Log.d("NewGameDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}