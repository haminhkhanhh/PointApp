package com.example.pointapp.activities

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.adapters.SectionAdapter

class StickyHeaderDecoration(
    private val adapter: SectionAdapter
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 100 // px, điều chỉnh theo UI
    private val paint = Paint().apply {
        color = 0xFFEFEFEF.toInt()
    }
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = 0xFF333333.toInt()
        textSize = 36f
        typeface = Typeface.DEFAULT_BOLD
    }
    private val bounds = Rect()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val lm = parent.layoutManager as? RecyclerView.LayoutManager ?: return
        val childCount = parent.childCount

        var prevHeader: String? = null
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(child)
            val title = adapter.getSectionTitle(pos) ?: continue
            if (title == prevHeader) continue
            prevHeader = title

            val left = parent.paddingLeft.toFloat()
            val top = child.top.coerceAtLeast(0).toFloat()
            val right = parent.width - parent.paddingRight.toFloat()
            val bottom = top + headerHeight

            // Draw background
            c.drawRect(left, top, right, bottom, paint)

            // Draw text
            textPaint.getTextBounds(title, 0, title.length, bounds)
            val x = left + 32f
            val y = top + headerHeight / 2f + bounds.height() / 2f
            c.drawText(title, x, y, textPaint)
        }

        // Draw sticky header
        val firstView = parent.getChildAt(0) ?: return
        val firstPos = parent.getChildAdapterPosition(firstView)
        val header = adapter.getSectionTitle(firstPos) ?: return

        var offset = 0f
        // Sticky push up effect
        if (childCount > 1) {
            val nextView = parent.getChildAt(1)
            val nextPos = parent.getChildAdapterPosition(nextView)
            val nextHeader = adapter.getSectionTitle(nextPos)
            if (nextHeader != null && nextHeader != header && nextView.top < headerHeight) {
                offset = (nextView.top - headerHeight).toFloat()
            }
        }

        c.drawRect(
            parent.paddingLeft.toFloat(),
            offset,
            parent.width - parent.paddingRight.toFloat(),
            offset + headerHeight,
            paint
        )
        textPaint.getTextBounds(header, 0, header.length, bounds)
        c.drawText(
            header,
            parent.paddingLeft + 32f,
            offset + headerHeight / 2f + bounds.height() / 2f,
            textPaint
        )
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        if (adapter.getSectionTitle(pos) != null && (pos == 0 || adapter.getSectionTitle(pos) != adapter.getSectionTitle(pos - 1))) {
            outRect.top = headerHeight
        } else {
            outRect.top = 0
        }
    }
}