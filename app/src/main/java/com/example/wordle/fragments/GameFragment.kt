package com.example.wordle.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.FragmentGameBinding
import com.example.wordle.model.GameViewModel
import com.example.wordle.wordsList1
import com.example.wordle.wordsList2

/**
 * This is the game screen of the Wordle app
 */
class GameFragment : Fragment() {

    // Binding object instance corresponding to the game_fragment.xml layout
    private var binding: FragmentGameBinding? = null

    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameFragment", "onCreateView() called")
        val fragmentBinding = FragmentGameBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("GameFragment", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner
            // Assign the view model to a property in the binding class
            viewModel = gameViewModel
            // Assign the fragment
            gameFragment = this@GameFragment
        }
    }

    // retrieves the MotionLayout corresponding to the currently active row
    private fun getRow(): MotionLayout {
        return binding!!.gameLayout[gameViewModel.tries.value!! + 1] as MotionLayout
    }

    // retrieves the TextView corresponding to the index value passed in
    private fun getTile(int: Int): TextView {
        return getRow()[int] as TextView
    }

    private fun disableButtons() {
        binding!!.enterButton.isEnabled = false
        binding!!.deleteButton.isEnabled = false
    }

    private fun enableButtons() {
        binding!!.enterButton.isEnabled = true
        binding!!.deleteButton.isEnabled = true
    }

    // checks if the word is a valid word from the list
    fun isAWord() {
        Log.d("GameViewModel", "isAWord() called")
        disableButtons()
        val str = gameViewModel.currentWord.value
        if (str?.length == 5) {
            if (str in wordsList1 || str in wordsList2) {
                isLetterCorrect(str)
            } else {
                notAWord()
            }
        } else {
            tooShort()
        }
    }

    // checks each letter for whether it is correct, present or absent
    // and creates a list of colors representing each state
    private fun isLetterCorrect(str: String) {
        Log.d("GameFragment", "isLetterCorrect() called")
        val colors = mutableListOf<Int>()
        for (i in (0..4)) {
            when {
                str[i] == gameViewModel.answer.value!![i] -> colors.add(R.color.green)
                str[i] in gameViewModel.answer.value!! -> colors.add(R.color.yellow)
                else -> colors.add(R.color.darkGray)
            }
        }
        revealHints(colors, str)
    }

    // Checks if the word is the correct word and moves to the next row if not
    private fun isCorrect(str: String) {
        Log.d("GameFragment", "isCorrect called")
        when {
            str == gameViewModel.answer.value -> winner()
            gameViewModel.tries.value!! >= 5 -> gameOver()
            else -> {
                gameViewModel.nextRow()
                enableButtons()
            }
        }
    }

    // changes textView background color and rotates right side up
    private fun transform(int: Int, color: Int) {
        getTile(int).setBackgroundResource(color)
        getTile(int).rotation = 180F
        getTile(int).rotationY = 180F
    }

    // changes the colors of the tiles to hint at the answer
    private fun revealHints(colors: MutableList<Int>, str: String) {
        Log.d("GameFragment", "revealHints() called")
        getRow().addTransitionListener(
            object : TransitionAdapter() {
                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                    super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                    when (triggerId) {
                        R.id.transform_A -> transform(0, colors[0])
                        R.id.transform_B -> transform(1, colors[1])
                        R.id.transform_C -> transform(2, colors[2])
                        R.id.transform_D -> transform(3, colors[3])
                        R.id.transform_E -> transform(4, colors[4])
                    }
                }
            }
        )
        getRow().setTransition(R.id.flip_tiles)
        getRow().transitionToEnd { isCorrect(str) }
    }

    // wiggles the current row to indicate a problem with the users input
    // and re enables buttons
    private fun wiggle() {
        getRow().setTransition(R.id.wiggle)
        getRow().transitionToEnd()
        enableButtons()
    }

    // creates a toast stating the word is not in the words list and calls wiggle()
    private fun notAWord() {
        Log.d("GameFragment", "notAWord() called")
        Toast.makeText(context, "Not in words list", Toast.LENGTH_SHORT).show()
        wiggle()
    }

    // creates a toast stating the word is too short and calls wiggle()
    private fun tooShort() {
        Log.d("GameFragment", "tooShort() called")
        Toast.makeText(context, "Not enough letters", Toast.LENGTH_SHORT).show()
        wiggle()
    }

    // plays bouncing animation then navigates to the winner dialog
    private fun winner() {
        Log.d("GameFragment", "winner() called")
        getRow().setTransition(R.id.bounce)
        getRow().transitionToEnd {
            findNavController().navigate(R.id.action_gameFragment_to_winnerDialog)
        }
    }

    // navigates to the GameOver dialog
    private fun gameOver() {
        Log.d("GameFragment", "gameOver() called")
        findNavController().navigate(R.id.action_gameFragment_to_gameOverDialog)
    }

    override fun onDestroyView() {
        Log.d("GameFragment", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}