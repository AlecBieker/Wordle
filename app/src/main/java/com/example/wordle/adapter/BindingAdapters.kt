package com.example.wordle.adapter

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.BindingAdapter
import com.example.wordle.R
import com.example.wordle.ui.model.ViewState
import com.google.android.material.button.MaterialButton


/**
 * Sets tile background and textColor attributes based on entered [ViewState]
 */
@BindingAdapter("tileState")
fun bindTileState(
    tile: TextView,
    State: ViewState?
) {
    with(tile) {
        when (State) {
            ViewState.FILLED -> {
                setTextColor(getColor(context, R.color.text_color))
                setBackgroundResource(R.drawable.filled_border)
            }
            ViewState.CORRECT -> {
                setTextColor(getColor(context, R.color.hint_text_color))
                setBackgroundResource(R.color.green)
            }
            ViewState.PRESENT -> {
                setTextColor(getColor(context, R.color.hint_text_color))
                setBackgroundResource(R.color.yellow)
            }
            ViewState.ABSENT -> {
                setTextColor(getColor(context, R.color.hint_text_color))
                setBackgroundResource(R.color.gray)
            }
            null -> {
                setTextColor(getColor(context, R.color.text_color))
                setBackgroundResource(R.drawable.border)
            }
        }
    }
}

/**
 * Sets key background and textColor attributes based on entered [ViewState]
 */
@BindingAdapter("keyState")
fun bindKeyState(
    key: MaterialButton,
    State: ViewState?
) {
    with (key) {
        when (State) {
            ViewState.CORRECT -> {
                setTextColor(getColor(context, R.color.hint_text_color))
                setBackgroundColor(getColor(context, R.color.green))
            }
            ViewState.PRESENT -> {
                setTextColor(getColor(context, R.color.hint_text_color))
                setBackgroundColor(getColor(context, R.color.yellow))
            }
            ViewState.ABSENT -> {
                setTextColor(getColor(context, R.color.hint_text_color))
                setBackgroundColor(getColor(context, R.color.gray))
            }
            null -> {
                setTextColor(getColor(context, R.color.text_color))
                setBackgroundColor(getColor(context, R.color.key_color))
            }
            else -> {}
        }
    }
}

/**
 * Binds the bar width to the Int calculated in StatsViewModel
 */
@BindingAdapter("barWidths")
fun bindBarWidths(
    layout: ConstraintLayout,
    widths: List<Int>
) {
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