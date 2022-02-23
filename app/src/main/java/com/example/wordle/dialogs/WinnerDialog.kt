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
import com.example.wordle.databinding.DialogWinnerBinding
import com.example.wordle.model.GameViewModel

class WinnerDialog : DialogFragment(R.layout.dialog_winner) {

    // Binding object instance corresponding to the start_fragment.xml layout
    private var binding: DialogWinnerBinding? = null

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("WinnerDialog", "onCreateView() called")
        binding = DialogWinnerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("WinnerDialog", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = viewModel

            // Assign the fragment
            winnerDialog = this@WinnerDialog
        }
    }

    // starts a new game and navigates back to GameFragment
    fun playAgain() {
        viewModel.newGame()

        findNavController().navigate(R.id.action_winnerDialog_to_gameFragment)
    }

    // navigates back to the StartFragment
    fun quitGame() {
        findNavController().navigate(R.id.action_winnerDialog_to_startFragment)
    }


}