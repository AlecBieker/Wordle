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
import com.example.wordle.databinding.FragmentStartBinding
import com.example.wordle.model.GameViewModel

/**
 * This is the first screen of the Wordle app.
 */
class StartFragment : Fragment() {

    // Binding object instance corresponding to the start_fragment.xml layout
    private var binding: FragmentStartBinding? = null

    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("StartFragment", "onCreateView() called")
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("StartFragment", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = gameViewModel

            // Assign the fragment
            startFragment = this@StartFragment
        }
    }

    /**
     * Start a new game and navigate to the next screen
     */
    fun newGame() {
        Log.d("StartFragment", "newGame() called")
        // Start a new game
        gameViewModel.newGame()

        // Navigate to the game screen
        findNavController().navigate(R.id.action_startFragment_to_gameFragment)
    }

    /**
     * Continue the game from before
     */
    fun resumeGame() {
        Log.d("StartFragment", "continueGame() called")
        // Navigate the the game screen
        findNavController().navigate(R.id.action_startFragment_to_gameFragment)
    }


    override fun onDestroyView() {
        Log.d("StartFragment", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}