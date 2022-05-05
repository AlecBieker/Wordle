package com.example.wordle.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.FragmentGameBinding
import com.example.wordle.model.GameViewModel
import android.widget.Toast
import com.example.wordle.model.ViewState

/**
 * This is the game screen of the Wordle app
 * This file coordinates event handling and the flow of information between the UI layer and the
 * [GameViewModel] using binding references to the UI layer and calls to the [GameViewModel].
 */
class GameFragment : Fragment() {

    // Binding object instance corresponding to the fragment_game.xml layout
    private var binding: FragmentGameBinding? = null

    // reference to the viewModel
    private val gameViewModel: GameViewModel by activityViewModels()

    // toast var for use by notAWord() and tooShort()
    private var toast: Toast? = null

    private var currentRow: MotionLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameFragment", "onCreateView() called")
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding!!.root
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
        setCurrentRow()
    }

    override fun onPause() {
        super.onPause()
        Log.d("GameFragment", "onPause() called")
        gameViewModel.updateGameState()
    }

    override fun onResume() {
        super.onResume()
        Log.d("GameFragment", "onResume() called")
        if (gameViewModel.transition.value != null) {
            disableButtons()
            currentRow?.transitionToState(R.id.start, 0)
            when (gameViewModel.transition.value) {
                "flip_tiles" -> revealHints()
                "wiggle" -> wiggle()
                "bounce" -> bounce()
            }
        }
    }

    // sets the motionLayout corresponding to the currently active row to currentRow
    private fun setCurrentRow() {
        currentRow =
            binding!!.gameLayout[(gameViewModel.tries.value ?: 0).plus(3)] as MotionLayout
    }

    // retrieves the currently active tile or the last in the row if it is full
    private fun getTile(): View {
        return if (gameViewModel.currentWord.value?.length!! == 5) {
            currentRow!![4]
        } else {
            currentRow!![gameViewModel.currentWord.value?.length!!]
        }
    }

    // disables keyboard buttons
    private fun disableButtons() {
        for (i in 8..36) {
            binding!!.gameLayout[i].isEnabled = false
        }
    }

    // enables keyboard buttons
    private fun enableButtons() {
        for (i in 8..36) {
            binding!!.gameLayout[i].isEnabled = true
        }
    }

    // does the "pop" animation and calls on viewModel to update text variables
    fun setText(char: Char) {
        if (char != ' ' && gameViewModel.currentWord.value?.length != 5) {
            currentRow!!.viewTransition(R.id.pop, getTile())
        }
        gameViewModel.updateText(char)
    }

    // checks if the word is a valid word from the list
    fun onEnter() {
        Log.d("GameFragment", "onEnter() called")
        disableButtons()
        when (gameViewModel.isAWord()) {
            0 -> tooShort()
            1 -> {
                if (gameViewModel.settings.getBoolean("hard_mode", false) && gameViewModel.tries.value!! >= 1) {
                    hardMode()
                }
                else revealHints()
            }
            2 -> notAWord()
        }
    }

    private fun hardMode() {
        Log.d("GameFragment", "hardMode() called")
        val triple = gameViewModel.isUsingHints()
        when (triple.first) {
            0 -> revealHints()
            1 -> missedYellowHint(triple.third)
            2 -> missedGreenHint(triple.second, triple.third)
        }
    }

    private fun missedYellowHint(letter: Char) {
        Log.d("GameFragment", "missedYellowHint() called")
        val string = getString(R.string.missed_yellow_hint, letter)
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        toast?.show()
        wiggle()
    }

    private fun missedGreenHint(index: Int, letter: Char) {
        Log.d("GameFragment", "missedGreenHint() called")
        val string = getString(
            R.string.missed_green_hint,
            resources.getStringArray(R.array.ordinals)[index],
            letter
        )
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        toast?.show()
        wiggle()
    }

    // changes the colors of the tiles to hint at the answer
    private fun revealHints() {
        Log.d("GameFragment", "revealHints() called")
        val hints = gameViewModel.isLetterCorrect()
        with(currentRow!!) {
            addTransitionListener(
                object : TransitionAdapter() {
                    override fun onTransitionTrigger(
                        motionLayout: MotionLayout,
                        triggerId: Int,
                        positive: Boolean,
                        progress: Float
                    ) {
                        super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                        if (0.001 < progress && progress < 1) {
                            when (triggerId) {
                                R.id.transform_A -> transform(0, hints[0], motionLayout)
                                R.id.transform_B -> transform(1, hints[1], motionLayout)
                                R.id.transform_C -> transform(2, hints[2], motionLayout)
                                R.id.transform_D -> transform(3, hints[3], motionLayout)
                                R.id.transform_E -> transform(4, hints[4], motionLayout)
                            }
                        }
                    }
                }
            )
            setTransition(R.id.flip_tiles)
            gameViewModel.updateTransition("flip_tiles")
            transitionToEnd {
                gameViewModel.updateTransition(null)
                gameViewModel.updateKeyStates(hints)
                isCorrect()
            }
        }
    }

    // rotates textViews to be right side up and sets the colors
    private fun transform(ind: Int, hint: ViewState, motionLayout: MotionLayout) {
        gameViewModel.updateTileStates(ind, hint)
        val textView: TextView = motionLayout[ind] as TextView
        textView.rotation = 180F
        textView.rotationY = 180F
    }

    // Checks if the word is the correct word and moves to the next row if not
    private fun isCorrect() {
        Log.d("GameFragment", "isCorrect() called")
        when (gameViewModel.isWordCorrect()) {
            0 -> winner()
            1 -> gameOver()
            2 -> {
                setCurrentRow()
                enableButtons()
            }
        }
    }

    /* wiggles the current row to indicate a problem with the users input
       and re enables buttons */
    private fun wiggle() {
        Log.d("GameFragment", "wiggle() called")
        with(currentRow!!) {
            setTransition(R.id.wiggle)
            gameViewModel.updateTransition("wiggle")
            transitionToEnd {
                gameViewModel.updateTransition(null)
                enableButtons()
            }
        }
    }

    // creates a toast stating the word is not in the words list and calls wiggle()
    private fun notAWord() {
        Log.d("GameFragment", "notAWord() called")
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(context, "Not in words list", Toast.LENGTH_SHORT)
        toast?.show()
        wiggle()
    }

    // creates a toast stating the word is too short and calls wiggle()
    private fun tooShort() {
        Log.d("GameFragment", "tooShort() called")
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(context, "Not enough letters", Toast.LENGTH_SHORT)
        toast?.show()
        wiggle()
    }

    // updates stats then calls bounce
    private fun winner() {
        Log.d("GameFragment", "winner() called")
        gameViewModel.updateStats(true)
        bounce()
    }

    // plays the "bounce" animation then navigates to the winner dialog
    private fun bounce() {
        with(currentRow!!) {
            setTransition(R.id.bounce)
            gameViewModel.updateTransition("bounce")
            transitionToEnd {
                gameViewModel.updateTransition(null)
                findNavController().navigate(R.id.action_gameFragment_to_winnerDialog)
            }
        }
    }

    // navigates to the GameOver dialog
    private fun gameOver() {
        Log.d("GameFragment", "gameOver() called")
        gameViewModel.updateStats(false)
        findNavController().navigate(R.id.action_gameFragment_to_gameOverDialog)
    }

    fun viewStats() {
        findNavController().navigate(R.id.action_gameFragment_to_statsDialog)
    }

    fun viewHelp() {
        findNavController().navigate(R.id.action_gameFragment_to_helpDialog)
    }

    override fun onDestroyView() {
        Log.d("GameFragment", "onDestroyView() called")
        super.onDestroyView()
        toast = null
        currentRow = null
        binding = null
    }
}