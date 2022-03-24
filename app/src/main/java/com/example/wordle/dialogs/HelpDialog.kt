package com.example.wordle.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.wordle.R
import com.example.wordle.databinding.DialogHelpBinding

/**
 * This is the dialog that explains how to play the game
 */
class HelpDialog : DialogFragment() {
    // Binding object instance corresponding to the dialog_help.xml layout
    private var binding: DialogHelpBinding? = null

    private var currentProgress: Float? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("HelpDialog", "onCreateView() called")
        binding = DialogHelpBinding.inflate(inflater, container, false)
        binding!!.helpDialogLayout.addTransitionListener(
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
                            R.id.transform_1 -> transform(binding!!.exampleTile1, R.color.green)
                            R.id.transform_7 -> transform(binding!!.exampleTile7, R.color.yellow)
                            R.id.transform_14 -> transform(binding!!.exampleTile14, R.color.gray)
                        }
                    }
                }
            }
        )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("HelpDialog", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = viewModel

            // Assign the fragment
            helpDialog = this@HelpDialog
        }
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onPause() {
        super.onPause()
        Log.d("HelpDialog", "onPause() called")
        currentProgress = binding!!.helpDialogLayout.progress
    }

    override fun onResume() {
        super.onResume()
        Log.d("HelpDialog", "onResume() called")
        if (currentProgress != null) {
            with (binding!!.helpDialogLayout) {
                progress = currentProgress!!
                transitionToEnd {
                    currentProgress = null
                }
            }
        }
    }

    private fun transform(textView: TextView, color: Int) {
        textView.rotation = 180F
        textView.rotationY = 180F
        textView.setBackgroundResource(color)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.hint_text_color))
    }

    fun close() {
        dismiss()
    }

    override fun onDestroyView() {
        Log.d("HelpDialog", "onDestroyView() called")
        super.onDestroyView()
        currentProgress = null
        binding = null
    }
}