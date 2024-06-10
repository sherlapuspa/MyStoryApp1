package com.dicoding.mystoryapp.cv

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R


class PassCV : AppCompatEditText, View.OnTouchListener {

    var passMatched: Boolean = false

    init {
        init()
    }

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
        transformationMethod = PasswordTransformationMethod.getInstance()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPasswordValidity()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

    override fun onFocusChanged(isInFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(isInFocus, direction, previouslyFocusedRect)
        if (!isInFocus) {
            checkPasswordValidity()
        }
    }

    private fun checkPasswordValidity() {
        passMatched = (text?.length ?: 0) >= 8
        error = if (!passMatched) {
            resources.getString(R.string.passwordLess)
        } else {
            null
        }
    }
}