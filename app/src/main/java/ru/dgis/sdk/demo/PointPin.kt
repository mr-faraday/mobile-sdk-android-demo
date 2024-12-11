package ru.dgis.sdk.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

class PointPin {
    private val context: Context
    val view: FrameLayout
    private val pinView: View
    val point: Point

    constructor(context: Context, point: Point) {
        this.context = context
        this.point = point

        view = FrameLayout(context)
        pinView = LayoutInflater
            .from(context)
            .inflate(R.layout.point_pin, view)

        pinView.findViewById<TextView>(R.id.number_text).text = point.order.toString()
    }
}
