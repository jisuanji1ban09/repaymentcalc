package com.itwzh.repaymentcalc.widget

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class MySmooth(context: Context) : LinearSmoothScroller(context) {
    companion object {
        const val SNAP_TO_CENTER = 2
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_CENTER
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_CENTER
    }

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        when (snapPreference) {
            SNAP_TO_CENTER -> {
                return (boxStart + boxEnd) / 2 - (viewStart + viewEnd) / 2
            }
            else -> throw IllegalArgumentException(
                "snap preference should be one of the"
                        + " constants defined in SmoothScroller, starting with SNAP_"
            )
        }
        return 0
    }


}

fun RecyclerView.smoothTo(position: Int) {
    val smooth = MySmooth(getContext())
    smooth.targetPosition = position;
    layoutManager?.startSmoothScroll(smooth);
}

