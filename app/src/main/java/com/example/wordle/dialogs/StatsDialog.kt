package com.example.wordle.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.wordle.databinding.DialogStatsBinding
import com.example.wordle.model.GameViewModel

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
    }

    fun close() {
        Log.d("StatsDialog", "close() called")
        dismiss()
    }

    private fun setValues() {
        Log.d("StatsDialog", "setValues() called")
        var playCount = viewModel.stats.getInt("play_count", 0)
        if (viewModel.gameState.getBoolean("game_in_progress", false)) {
            playCount --
        }
        binding!!.playCountValue.text = playCount.toString()
        binding!!.winPercentageValue.text = viewModel.getWinPercentage(playCount)
        binding!!.currentStreakValue.text = viewModel.stats.getInt("current_streak", 0).toString()
        binding!!.maxStreakValue.text = viewModel.stats.getInt("max_streak", 0).toString()
    }



    override fun onDestroyView() {
        Log.d("StatsDialog", "onDestroyView() called")
        super.onDestroyView()
        binding = null
    }
}