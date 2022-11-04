package com.kisusyenni.storyapp.view.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.kisusyenni.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var iconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        compoundDrawablePadding = 16
        iconDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24) as Drawable

        setDrawables(startOfTheText = iconDrawable)

        setHint(R.string.password_ed)

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    // Password validation
                    if (s.length < 6) error = context.getString(R.string.password_error)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}