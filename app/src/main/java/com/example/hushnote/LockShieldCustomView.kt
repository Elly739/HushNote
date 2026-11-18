package com.example.hushnote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class LockShieldCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val shieldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.warm_amber)
        style = Paint.Style.FILL
    }

    private val lockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.midnight_navy)
        style = Paint.Style.FILL
    }

    private val shieldPath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(width, height) * 0.4f

        // Draw Shield Shape
        shieldPath.reset()
        shieldPath.moveTo(centerX, centerY - radius)
        shieldPath.quadTo(centerX + radius, centerY - radius * 0.8f, centerX + radius, centerY)
        shieldPath.quadTo(centerX + radius, centerY + radius * 0.9f, centerX, centerY + radius)
        shieldPath.quadTo(centerX - radius, centerY + radius * 0.9f, centerX - radius, centerY)
        shieldPath.quadTo(centerX - radius, centerY - radius * 0.8f, centerX, centerY - radius)
        shieldPath.close()

        canvas.drawPath(shieldPath, shieldPaint)

        // Draw Lock Body inside Shield
        val lockWidth = radius * 0.5f
        val lockHeight = radius * 0.4f
        val left = centerX - lockWidth / 2f
        val top = centerY - lockHeight / 4f
        val right = centerX + lockWidth / 2f
        val bottom = centerY + lockHeight * 0.8f

        canvas.drawRoundRect(left, top, right, bottom, 12f, 12f, lockPaint)

        // Draw Lock Shackle
        val shacklePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.midnight_navy)
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawArc(
            centerX - lockWidth * 0.35f,
            top - lockHeight * 0.6f,
            centerX + lockWidth * 0.35f,
            top + 10f,
            180f,
            180f,
            false,
            shacklePaint
        )
    }
}
