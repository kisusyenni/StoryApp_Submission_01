package com.kisusyenni.storyapp.view.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.kisusyenni.storyapp.R


class EmailEditText: AppCompatEditText {

    private lateinit var iconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        iconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_email_24) as Drawable

        compoundDrawablePadding = 16

        setDrawables(startOfTheText = iconDrawable)
        setHint(R.string.email_ed)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Email validation
                if (s.toString().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s)
                        .matches()
                ) error = context.getString(R.string.email_error)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

}