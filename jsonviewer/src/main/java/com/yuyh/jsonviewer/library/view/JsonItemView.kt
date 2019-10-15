package com.yuyh.jsonviewer.library.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.yuyh.jsonviewer.library.R
import com.yuyh.jsonviewer.library.adapter.BaseJsonViewerAdapter

/**
 * Created by yuyuhang on 2017/11/29.
 */
class JsonItemView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(mContext, attrs, defStyleAttr) {

    private var mTvLeft: TextView? = null
    private var mTvRight: TextView? = null
    private var mIvIcon: ImageView? = null

    val rightText: CharSequence
        get() = mTvRight!!.text

    init {

        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        LayoutInflater.from(mContext).inflate(R.layout.jsonviewer_layout_item_view, this, true)

        mTvLeft = findViewById(R.id.tv_left)
        mTvRight = findViewById(R.id.tv_right)
        mIvIcon = findViewById(R.id.iv_icon)
    }

    fun setTextSize(textSizeDp: Float) {
        var textSizeDp = textSizeDp
        if (textSizeDp < 12) {
            textSizeDp = 12f
        } else if (textSizeDp > 30) {
            textSizeDp = 30f
        }

        TEXT_SIZE_DP = textSizeDp.toInt()

        mTvLeft!!.textSize = TEXT_SIZE_DP.toFloat()
        mTvRight!!.textSize = TEXT_SIZE_DP.toFloat()
        mTvRight!!.setTextColor(BaseJsonViewerAdapter.BRACES_COLOR)

        // align the vertically expand/collapse icon to the text
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DP*1.2f, resources.displayMetrics).toInt()

        val layoutParams = mIvIcon!!.layoutParams as LayoutParams
        layoutParams.height = textSize
        layoutParams.width = textSize
        layoutParams.topMargin = textSize / 8

        mIvIcon!!.layoutParams = layoutParams
    }

    fun setRightColor(color: Int) {
        mTvRight!!.setTextColor(color)
    }

    fun hideLeft() {
        mTvLeft!!.visibility = View.GONE
    }

    fun showLeft(text: CharSequence?) {
        mTvLeft!!.visibility = View.VISIBLE
        if (text != null) {
            mTvLeft!!.text = text
        }
    }

    fun hideRight() {
        mTvRight!!.visibility = View.GONE
    }

    fun showRight(text: CharSequence?) {
        mTvRight!!.visibility = View.VISIBLE
        if (text != null) {
            mTvRight!!.text = text
        }
    }

    fun hideIcon() {
        mIvIcon!!.visibility = View.GONE
    }

    fun showIcon(isPlus: Boolean) {
        mIvIcon!!.visibility = View.VISIBLE
        mIvIcon!!.setImageResource(if (isPlus) R.drawable.ic_plus else R.drawable.ic_minus)
        mIvIcon!!.contentDescription = resources.getString(if (isPlus) R.string.jsonViewer_icon_plus else R.string.jsonViewer_icon_minus)
    }

    fun setIconClickListener(listener: View.OnClickListener) {
        mIvIcon!!.setOnClickListener(listener)
    }

    fun addViewNoInvalidate(child: View) {
        var params: ViewGroup.LayoutParams? = child.layoutParams
        if (params == null) {
            params = generateDefaultLayoutParams()
            if (params == null) {
                throw IllegalArgumentException("generateDefaultLayoutParams() cannot return null")
            }
        }
        addViewInLayout(child, -1, params)
    }

    companion object {

        var TEXT_SIZE_DP = 12
    }
}
