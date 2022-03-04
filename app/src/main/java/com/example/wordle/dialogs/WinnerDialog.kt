package com.example.wordle.dialogs

import android.content.Intent
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

class WinnerDialog : DialogFragment() {

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

    fun share() {
        var gameSummary = getString(
            R.string.game_summary,
            resources.getStringArray(R.array.ordinals)[viewModel.tries.value!!])
        val backgrounds = viewModel.backgrounds.value!!
        val size = viewModel.letters.value?.size!!.minus(1)
        for (i in 0..size) {
            if (i % 5 == 0 && i != size) gameSummary += "\n"
            when (backgrounds[i]) {
                R.color.green -> gameSummary += getString(R.string.green_square)
                R.color.yellow -> gameSummary += getString(R.string.yellow_square)
                R.color.gray -> gameSummary += getString(R.string.black_square)
            }
            Log.d("WinnerDialog", "gameSummary = $gameSummary")
        }

        // Create an ACTION_SEND implicit intent with game summary in the intent extras
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, gameSummary)

        // Check if there's an app that can handle this intent before launching it
        if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
            // Start a new activity with the given intent (this may open the share dialog on a
            // device if multiple apps can handle this intent)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        Log.d("WinnerDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}