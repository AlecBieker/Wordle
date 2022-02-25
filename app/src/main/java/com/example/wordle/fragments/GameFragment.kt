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
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.system.measureNanoTime

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
        return binding!!.gameLayout[gameViewModel.tries.value!!.plus(1)] as MotionLayout
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
        Log.d("GameFragment", "isAWord() called")
        disableButtons()
        val str = gameViewModel.currentWord.value
        if (str?.length == 5) {
            if (str in wordsList1 || str in wordsList2) {
                revealHints(str)
            } else {
                notAWord()
            }
        } else {
            tooShort()
        }
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

    // rotates textViews to be right side up
    private fun transform(int: Int, hint: Int, motionLayout: MotionLayout) {
        measureNanoTime {
            gameViewModel.updateColors(hint)
            val textView: TextView = motionLayout[int] as TextView
            textView.rotation = 180F
            textView.rotationY = 180F
        }
    }

    // changes the colors of the tiles to hint at the answer
    private fun revealHints(str: String) {
        Log.d("GameFragment", "revealHints() called")
        val hints = gameViewModel.isLetterCorrect(str)
        val currentRow = getRow()
        currentRow.addTransitionListener(
            object : TransitionAdapter() {
                override fun onTransitionTrigger(
                    motionLayout: MotionLayout,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                    Log.d("TransitionListener", ".addTransitionListener() called")
                    super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                    Log.d("Listener","progress = $progress")
                    when (progress.times(10).roundToInt()) {
                        1 -> transform(0, hints[0], motionLayout)
                        3 -> transform(1, hints[1], motionLayout)
                        5 -> transform(2, hints[2], motionLayout)
                        7 -> transform(3, hints[3], motionLayout)
                        9 -> transform(4, hints[4], motionLayout)
                    }
                }
            }
        )
        currentRow.setTransition(R.id.flip_tiles)
        getRow().transitionToEnd { isCorrect(str) }
    }

    // wiggles the current row to indicate a problem with the users input
    // and re enables buttons
    private fun wiggle() {
        Log.d("GameFragment", "wiggle() called")
        val currentRow = getRow()
        currentRow.setTransition(R.id.wiggle)
        currentRow.transitionToEnd { enableButtons() }
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