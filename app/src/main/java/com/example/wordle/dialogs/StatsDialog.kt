package com.example.wordle.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.wordle.R
import com.example.wordle.databinding.DialogStatsBinding
import com.example.wordle.model.GameViewModel
import java.util.Collections.max

/**
 * This is the Statistics Dialog that shows information about the users play history
 */
class StatsDialog : DialogFragment() {

    private var binding: DialogStatsBinding? = null

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("StatsDialog", "onCreateView() called")
        binding = DialogStatsBinding.inflate(inflater, container, false)
        setValues()
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
            viewModel = viewModel

            // Assign the fragment
            statsDialog = this@StatsDialog
        }
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    private fun setValues() {
        Log.d("StatsDialog", "setValues() called")
        var playCount = viewModel.stats.getInt("play_count", 0)
        if (viewModel.gameState.getBoolean("game_in_progress", false)) {
            playCount--
        }
        binding!!.playCountValue.text = playCount.toString()
        binding!!.winPercentageValue.text = viewModel.getWinPercentage(playCount)
        binding!!.currentStreakValue.text = viewModel.stats.getInt("current_streak", 0).toString()
        binding!!.maxStreakValue.text = viewModel.stats.getInt("max_streak", 0).toString()
        for (i in 1..6) {
            val view = binding!!.statsLayout[i + 15] as TextView
            view.text = viewModel.stats.getInt("wins_$i", 0).toString()
        }
    }

    private fun setConstraints() {
        Log.d("StatsDialog", "setConstraints() called")
        val wins: MutableList<Float> = mutableListOf()
        val widths: MutableList<Int> = mutableListOf()
        val layout = binding!!.statsLayout
        for (i in 1..6) {
            wins.add(viewModel.stats.getInt("wins_$i", 0).toFloat())
        }
        val max = binding!!.barOne.maxWidth.toFloat()
        val min = binding!!.barOne.minWidth.toFloat()
        for (item in wins) {
            widths.add(item.div(max(wins)).times(max - min).plus(min).toInt())
        }
        ConstraintSet().apply {
            clone(layout)
            constrainWidth(R.id.bar_one, widths[0])
            constrainWidth(R.id.bar_two, widths[1])
            constrainWidth(R.id.bar_three, widths[2])
            constrainWidth(R.id.bar_four, widths[3])
            constrainWidth(R.id.bar_five, widths[4])
            constrainWidth(R.id.bar_six, widths[5])
            applyTo(layout)
        }
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