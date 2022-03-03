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
@BindingAdapter("formatDoubleTotext")
fun formatDoubleTotext(textView: TextView, number: Double) {
    textView.text = format(number)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("formatDoubleTotextAndUnit")
fun formatDoubleTotextAndUnit(textView: TextView, number: Double) {
    textView.text = format(number)+ unitYuan
}


@SuppressLint("SetTextI18n")
@BindingAdapter("formatDoubleTotextAndUnit")
fun formatDoubleTotextAndUnit(textView: TextView, number: Int) {
    textView.text = number.toString()+ unitYuan
}

@BindingAdapter("formatDoubleTotext")
fun formatDoubleTotext(textView: TextView, number: Int) {
    textView.text = number.toString()
}