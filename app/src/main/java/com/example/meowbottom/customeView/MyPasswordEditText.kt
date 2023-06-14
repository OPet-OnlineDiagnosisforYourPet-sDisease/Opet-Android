package com.example.meowbottom.customeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.meowbottom.R
import com.google.android.material.textfield.TextInputEditText

class MyPasswordEditText : TextInputEditText {


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
        //hint = "Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START


    }

    private fun init() {

        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //DO NOTHING
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //jika kurang dari 8
                if (p0?.toString()?.length!! < 8) {
                    error = context.getString(R.string.invalid_password)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //DO NOTHING
            }

        })
    }

}