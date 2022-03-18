package com.example.wordle.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.FragmentStartBinding
import com.example.wordle.model.GameViewModel
import java.util.*
import kotlin.concurrent.schedule

/**
 * This is the first screen of the Wordle app.
 */
class StartFragment : Fragment() {

    // Binding object instance corresponding to the fragment_start.xml layout
    private var binding: FragmentStartBinding? = null

    // input the "WORDLE" letters into the header textViews
    private fun type(letter: String, textView: TextView) {
        textView.text = letter
        textView.setBackgroundResource(R.drawable.filled_border)
    }

    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("StartFragment", "onCreateView() called")
        binding = FragmentStartBinding.inflate(inflater, container, false)
        binding!!.resumeButton.isEnabled =
            gameViewModel.gameState.getBoolean("game_in_progress", false)
        binding!!.motionLayout.addTransitionListener(
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
                            R.id.set_w_text -> type("W", binding!!.wHeader)
                            R.id.set_o_text -> type("O", binding!!.oHeader)
                            R.id.set_r_text -> type("R", binding!!.rHeader)
                            R.id.set_d_text -> type("D", binding!!.dHeader)
                            R.id.set_l_text -> type("L", binding!!.lHeader)
                            R.id.set_e_text -> type("E", binding!!.eHeader)
                            R.id.transform_w -> transform(binding!!.wHeader)
                            R.id.transform_o -> transform(binding!!.oHeader)
                            R.id.transform_r -> transform(binding!!.rHeader)
                            R.id.transform_d -> transform(binding!!.dHeader)
                            R.id.transform_l -> transform(binding!!.lHeader)
                            R.id.transform_e -> transform(binding!!.eHeader)
                        }
                    }
                }
            }
        )
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
        // Animate the WORDLE header textView
        with(binding!!.motionLayout) {
            transitionToState(R.id.start, 0)
            Timer().schedule(500) {
                setTransition(R.id.type)
                transitionToEnd {
                    setTransition(R.id.flip_tiles)
                    transitionToEnd {
                        setTransition(R.id.bounce)
                        transitionToEnd()
                    }
                }
            }
        }
    }

    // Set the header textViews to be right side up and with the correct text color and background
    private fun transform(textView: TextView) {
        textView.rotation = 180F
        textView.rotationY = 180F
        textView.setBackgroundResource(R.color.green)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.hint_text_color))
    }

    // starts a new game or shows the new game dialog if one is already in progress
    fun newGame() {
        when (gameViewModel.gameState.getBoolean("game_in_progress", false)) {
            true -> findNavController().navigate(R.id.action_startFragment_to_newGameDialog)
            false -> {
                gameViewModel.newGame()
                findNavController().navigate(R.id.action_startFragment_to_gameFragment)
            }
        }
    }

    // Continues the game from before
    fun resumeGame() {
        Log.d("StartFragment", "resumeGame() called")
        gameViewModel.resumeGame()

        // Navigate the the game screen
        findNavController().navigate(R.id.action_startFragment_to_gameFragment)
    }

    // View the Stats dialog
    fun viewStats() {
        Log.d("StartFragment", "viewStats() called")
        findNavController().navigate(R.id.action_startFragment_to_statsDialog)
    }

    // View the Help dialog
    fun viewHelp() {
        Log.d("StartFragment", "viewHelp() called")
        findNavController().navigate(R.id.action_startFragment_to_helpDialog)
    }

    override fun onDestroyView() {
        Log.d("StartFragment", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}