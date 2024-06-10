package com.dicoding.mystoryapp.cv

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R

class NameCV : AppCompatEditText, View.OnFocusChangeListener {

    var nameMatched = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.rounded_gray_bg)
        inputType = InputType.TYPE_CLASS_TEXT
        onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, isInFocus: Boolean) {
        if (!isInFocus) {
            checkNameValidity()
        }
    }

    private fun checkNameValidity() {
        nameMatched = text.toString().trim().isNotEmpty()
        error = if (!nameMatched) {
            resources.getString(R.string.nullName)
        } else {
            null
        }
    }
}