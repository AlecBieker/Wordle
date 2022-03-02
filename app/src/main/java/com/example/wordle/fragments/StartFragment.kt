package com.example.wordle.fragments

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordle.R
import com.example.wordle.databinding.FragmentStartBinding
import com.example.wordle.model.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
        binding!!.motionLayout.addTransitionListener(
            object : TransitionAdapter() {
                override fun onTransitionTrigger(
                    motionLayout: MotionLayout,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                    super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                    Log.d("TransitionTrigger", "progress = $progress")
                    when (progress.times(10).roundToInt()) {
                        0 -> binding!!.wHeader.text = "W"
                    }
                }
            }
        )
        binding!!.motionLayout.setTransition(R.id.type)
        binding!!.motionLayout.transitionToEnd()
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


    // Shows the start new game confirmation dialog
    fun newGameDialog() {
        binding!!.motionLayout.addTransitionListener(
            object : TransitionAdapter() {
                override fun onTransitionTrigger(
                    motionLayout: MotionLayout,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                    super.onTransitionTrigger(motionLayout, triggerId, positive, progress)
                    Log.d("TransitionTrigger", "progress = $progress")
                    when (progress.times(100).roundToInt()) {
                        0 -> binding!!.wHeader.text = "W"
                        17 -> binding!!.oHeader.text = "O"
                        33 -> binding!!.rHeader.text = "R"
                        49 -> binding!!.dHeader.text = "D"
                        65 -> binding!!.lHeader.text = "L"
                        81 -> binding!!.eHeader.text = "E"
                    }
                }
            }
        )
        binding!!.motionLayout.setTransition(R.id.type)
        binding!!.motionLayout.transitionToEnd()

        // findNavController().navigate(R.id.action_startFragment_to_newGameDialog)
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