package com.example.currency.presentation.utils.ext

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.currencytask.R
import com.google.android.material.snackbar.Snackbar

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

fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

fun View.showSnackBar(message: String?, duration: Int = Snackbar.LENGTH_SHORT) {
    try {
        Snackbar.make(context, this, message.toString(), duration).show()
    } catch (e: Throwable) {
        throw e
    }
}

fun View.showSnackBarWithAction(
    message: String?,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    actionMessage: String = "Retry",
    @ColorRes messageColor: Int = R.color.color_red,
    action: () -> Unit
) {
    try {
        Snackbar.make(
            this,
            message.toString(),
            duration
        ).apply {
            setAction(actionMessage) { action.invoke() }
                .setActionTextColor(
                    ContextCompat.getColor(context, (messageColor))
                )
            show()
        }

    } catch (e: Throwable) {
        throw e
    }
}