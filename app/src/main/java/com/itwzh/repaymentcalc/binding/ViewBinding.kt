package com.itwzh.repaymentcalc.binding

import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.itwzh.repaymentcalc.utlis.format

/**
 * author:王忠辉
 */
@BindingAdapter("formatDoubleTotext")
fun formatDoubleTotext(textView: TextView, number: Double) {
    textView.text = format(number)
}


@BindingAdapter("formatDoubleTotext")
fun formatDoubleTotext(textView: TextView, number: Int) {
    textView.text = number.toString()
}