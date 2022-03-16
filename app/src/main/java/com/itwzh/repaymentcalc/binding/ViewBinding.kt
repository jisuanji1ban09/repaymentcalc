package com.itwzh.repaymentcalc.binding

import android.annotation.SuppressLint
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.itwzh.repaymentcalc.utlis.format

/**
 * author:王忠辉
 */
const val unitYuan = "元"
@BindingAdapter("formatDoubleToText")
fun formatDoubleToText(textView: TextView, number: Double) {
    textView.text = format(number)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("formatDoubleToTextAndUnit")
fun formatDoubleToTextAndUnit(textView: TextView, number: Double) {
    textView.text = format(number)+ unitYuan
}


@SuppressLint("SetTextI18n")
@BindingAdapter("formatIntToTextAndUnit")
fun formatIntToTextAndUnit(textView: TextView, number: Int) {
    textView.text = format(number.toDouble()).toString()+ unitYuan
}

@BindingAdapter("formatIntToText")
fun formatIntToText(textView: TextView, number: Int) {
    textView.text = number.toString()
}