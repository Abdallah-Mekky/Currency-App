package com.example.currencyTask.presentation.utils.ext

import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun View.isVisible(shouldBeVisible: Boolean?) {
    if (shouldBeVisible == true) this.toVisible()
    else this.toGone()
}

fun View.toVisible() {
    visibility = View.VISIBLE
    if (this is Group) {
        this.requestLayout()
    }
}

fun View.toGone() {
    visibility = View.GONE
    if (this is Group) {
        this.requestLayout()
    }
}