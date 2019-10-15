package com.yuyh.jsonviewer.library

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import com.yuyh.jsonviewer.library.adapter.BaseJsonViewerAdapter
import com.yuyh.jsonviewer.library.adapter.JsonViewerAdapter
import com.yuyh.jsonviewer.library.view.JsonItemView

import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Created by yuyuhang on 2017/11/30.
 */
class JsonRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private var mAdapter: BaseJsonViewerAdapter<*>? = null

    internal var mode: Int = 0
    internal var oldDist: Float = 0.toFloat()

    private val touchListener = object : OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
            when (event.action and event.actionMasked) {
                MotionEvent.ACTION_DOWN -> mode = 1
                MotionEvent.ACTION_UP -> mode = 0
                MotionEvent.ACTION_POINTER_UP -> mode -= 1
                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = spacing(event)
                    mode += 1
                }

                MotionEvent.ACTION_MOVE -> if (mode >= 2) {
                    val newDist = spacing(event)
                    if (abs(newDist - oldDist) > 0.5f) {
                        zoom(newDist / oldDist)
                        oldDist = newDist
                    }
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, event: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    init {

        initView()
    }

    private fun initView() {
        layoutManager = LinearLayoutManager(context)
    }

    fun bindJson(jsonStr: String) {
        mAdapter = null
        mAdapter = JsonViewerAdapter(jsonStr)
        adapter = mAdapter
    }

    fun bindJson(array: JSONArray) {
        mAdapter = null
        mAdapter = JsonViewerAdapter(array)
        adapter = mAdapter
    }

    fun bindJson(`object`: JSONObject) {
        mAdapter = null
        mAdapter = JsonViewerAdapter(`object`)
        adapter = mAdapter
    }

    fun setKeyColor(color: Int) {
        BaseJsonViewerAdapter.KEY_COLOR = color
    }

    fun setValueTextColor(color: Int) {
        BaseJsonViewerAdapter.TEXT_COLOR = color
    }

    fun setValueNumberColor(color: Int) {
        BaseJsonViewerAdapter.NUMBER_COLOR = color
    }

    fun setValueBooleanColor(color: Int) {
        BaseJsonViewerAdapter.BOOLEAN_COLOR = color
    }

    fun setValueUrlColor(color: Int) {
        BaseJsonViewerAdapter.URL_COLOR = color
    }

    fun setValueNullColor(color: Int) {
        BaseJsonViewerAdapter.NUMBER_COLOR = color
    }

    fun setBracesColor(color: Int) {
        BaseJsonViewerAdapter.BRACES_COLOR = color
    }

    fun setTextSize(sizeDP: Float) {
        var sizeDP = sizeDP
        if (sizeDP < 10) {
            sizeDP = 10f
        } else if (sizeDP > 30) {
            sizeDP = 30f
        }

        if (BaseJsonViewerAdapter.TEXT_SIZE_DP != sizeDP) {
            BaseJsonViewerAdapter.TEXT_SIZE_DP = sizeDP
            if (mAdapter != null) {
                updateAll(sizeDP)
            }
        }
    }

    fun setScaleEnable(enable: Boolean) {
        if (enable) {
            addOnItemTouchListener(touchListener)
        } else {
            removeOnItemTouchListener(touchListener)
        }
    }

    fun updateAll(textSize: Float) {
        val manager = layoutManager

        val count = manager.childCount

        for (i in 0 until count) {
            val view = manager.getChildAt(i)
            loop(view, textSize)
        }
    }

    private fun loop(view: View, textSize: Float) {
        if (view is JsonItemView) {

            view.setTextSize(textSize)

            val childCount = view.childCount

            for (i in 0 until childCount) {
                val view1 = view.getChildAt(i)
                loop(view1, textSize)
            }
        }
    }

    private fun zoom(f: Float) {
        setTextSize(BaseJsonViewerAdapter.TEXT_SIZE_DP * f)
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }
}
