package com.example.wordle.model

import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.BindingAdapter
import com.example.wordle.R
import com.google.android.material.button.MaterialButton


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