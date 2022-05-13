package com.example.wordle.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.wordle.databinding.DialogStatsBinding
import com.example.wordle.ui.model.StatsViewModel


/**
 * This is the Statistics Dialog that shows information about the users play history
 */
class StatsDialog : DialogFragment() {

    private var binding: DialogStatsBinding? = null

    private val statsViewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("StatsDialog", "onCreateView() called")
        binding = DialogStatsBinding.inflate(inflater, container, false)
        setConstraints()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("StatsDialog", "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = statsViewModel

            // Assign the fragment
            statsDialog = this@StatsDialog
        }
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    /**
     * Sets the widths for the bars
     */
    private fun setConstraints() {
        Log.d("StatsDialog", "setConstraints() called")
        val max = binding!!.barOne.maxWidth.toFloat()
        val min = binding!!.barOne.minWidth.toFloat()
        statsViewModel.calculateWidths(max, min)
    }

    fun close() {
        Log.d("StatsDialog", "close() called")
        dismiss()
    }

    override fun onDestroyView() {
        Log.d("StatsDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}