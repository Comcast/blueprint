package com.xfinity.blueprint.architecture

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import com.xfinity.blueprint.ComponentAdapter

class RecyclerViewScreenManager(private val recyclerView: RecyclerView) : ScreenManager {

    private var onSwipeToDeleteBehavior: ((position: Int) -> Unit)? = null

    override fun setOnSwipeToDeleteBehavior(onDeleteBehavior: (position: Int) -> Unit) {
        onSwipeToDeleteBehavior = onDeleteBehavior
    }

    override fun enableSwipeToDelete(deleteIcon: Drawable, deletionBackgroundColor: Int, vararg allowedViewTypes: Int) {
        val swipeHandler = object : ComponentSwipeToDeleteHelper(
            deleteIcon,
            deletionBackgroundColor, allowedViewTypes.asList()
        ) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onSwipeToDeleteBehavior?.invoke(viewHolder.adapterPosition)
                (recyclerView.adapter as ComponentAdapter).removeComponent(viewHolder.adapterPosition, true)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    override fun setBackgroundColor(color: Int) {
        recyclerView.setBackgroundColor(color)
    }

    override fun scrollToBottom() {
        recyclerView.adapter?.let {
            recyclerView.smoothScrollToPosition(it.itemCount - 1)
        }
    }
}

interface ScreenManager {
    fun setBackgroundColor(color: Int)
    fun scrollToBottom()
    fun enableSwipeToDelete(deleteIcon: Drawable, deletionBackgroundColor: Int, vararg allowedViewTypes: Int)
    fun setOnSwipeToDeleteBehavior(onDeleteBehavior: (position: Int) -> Unit)
}

abstract class ComponentSwipeToDeleteHelper(
    private val deleteIcon: Drawable,
    private val deletionBackgroundColor: Int,
    private val allowedViewTypes: List<Int>
) : ItemTouchHelper.SimpleCallback(LEFT, LEFT) {

    private val deleteIconIntrinsicsWidth = deleteIcon.intrinsicWidth
    private val deleteIconIntrinsicHeight = deleteIcon.intrinsicHeight
    private val background = ColorDrawable().apply { color = deletionBackgroundColor }
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder.itemViewType !in allowedViewTypes) return 0
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                canvas,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(canvas)

        val deleteIconTop = itemView.top + (itemHeight - deleteIconIntrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - deleteIconIntrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - deleteIconIntrinsicsWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + deleteIconIntrinsicHeight

        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(canvas)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(canvas: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        canvas?.drawRect(left, top, right, bottom, clearPaint)
    }
}