package com.example.wordle.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.DialogGameOverBinding
import com.example.wordle.model.GameViewModel
import java.util.*

/**
 * This is the dialog that displays when the user loses a game
 */
class GameOverDialog : DialogFragment() {

    // Binding object instance corresponding to the dialog_game_over.xml layout
    private var binding: DialogGameOverBinding? = null

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameOverDialog", "onCreateView() called")
        binding = DialogGameOverBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("GameOverDialog", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = viewModel

            // Assign the fragment
            gameOverDialog = this@GameOverDialog
        }
        binding!!.answerTextView.text = viewModel.answer.value
    }

    // starts a new game and navigates back to GameFragment
    fun tryAgain() {
        Log.d("GameOverDialog", "tryAgain() called")
        viewModel.newGame()
        findNavController().navigate(R.id.action_gameOverDialog_to_gameFragment)
    }

    // navigates back to the StartFragment
    fun quitGame() {
        Log.d("GameOverDialog", "quitGame() called")
        findNavController().navigate(R.id.action_gameOverDialog_to_startFragment)
    }

    fun lookupWord() {
        val lowercase = viewModel.answer.value?.lowercase()
        val queryUrl = Uri.parse(getString(R.string.queryUrl, lowercase))
        val intent = Intent(Intent.ACTION_VIEW, queryUrl)
        context?.startActivity(intent)
    }

    override fun onDestroyView() {
        Log.d("GameOverDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}