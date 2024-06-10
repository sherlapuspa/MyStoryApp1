package com.dicoding.mystoryapp.cv

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R

class EmailCV : AppCompatEditText, View.OnFocusChangeListener {

    var emailMatched = false
    private lateinit var existingEmail: String
    private var emailInUse = false

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

        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEmailValidity()
                if (emailInUse) {
                    checkEmailInUseValidity()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onFocusChange(v: View?, isInFocus: Boolean) {
        if (!isInFocus) {
            checkEmailValidity()
            if (emailInUse) {
                checkEmailInUseValidity()
            }
        }
    }

    private fun checkEmailValidity() {
        emailMatched = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!emailMatched) {
            resources.getString(R.string.invalidEmailAddress)
        } else {
            null
        }
    }

    private fun checkEmailInUseValidity() {
        error = if (emailInUse && text.toString().trim() == existingEmail) {
            resources.getString(R.string.emailIsInUse)
        } else {
            null
        }
    }

    fun setErrorMessage(message: String, email: String) {
        existingEmail = email
        emailInUse = true
        error = if (text.toString().trim() == existingEmail) {
            message
        } else {
            null
        }
    }
}