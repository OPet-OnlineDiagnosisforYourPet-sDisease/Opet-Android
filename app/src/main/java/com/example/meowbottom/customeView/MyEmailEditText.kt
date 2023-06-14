package com.example.meowbottom.customeView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.meowbottom.R
import com.google.android.material.textfield.TextInputEditText

class MyEmailEditText : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //hint = "example@gmail.com"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
    private fun init() {

        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(p0).matches()) {
                    error = context.getString(R.string.email_invalid)
                } else if (p0.isNullOrEmpty()) {
                    error = context.getString(R.string.empty_email)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //DO NOTHING
            }

        })
    }

}