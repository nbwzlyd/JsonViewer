package com.yuyh.jsonviewer.library.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.yuyh.jsonviewer.library.BuildConfig
import com.yuyh.jsonviewer.library.JsonRecyclerView
import com.yuyh.jsonviewer.library.R
import com.yuyh.jsonviewer.library.bean.ServerDataBean
import com.yuyh.jsonviewer.library.utils.SizeUtils
import kotlinx.android.synthetic.main.server_data_layout.view.*
import java.util.*
import kotlin.math.abs

class ServerDataSuspendView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var recyclerView: RecyclerView? = null

    private var serverDataAdapter: ServerDataAdapter? = null

    private var mtextClear: TextView? = null

    private var mhideShow: TextView? = null
    private var isFullScreen = false


    private val mData = ArrayList<ServerDataBean>()


    private var manager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null

    private var isAdd = false
    private var isSmallStyle = false

    private lateinit var closeView: TextView

    internal var x: Float = 0.toFloat()
    internal var y: Float = 0.toFloat()
    private var mTouchX: Float = 0.toFloat()
    private var mTouchY: Float = 0.toFloat()
    private var mStartX: Float = 0.toFloat()
    private var mStartY: Float = 0.toFloat()

    init {
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.server_data_layout, this)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        serverDataAdapter = ServerDataAdapter()
        recyclerView!!.adapter = serverDataAdapter
        mtextClear = findViewById(R.id.text_clear)
        circumstances.text = BuildConfig.FLAVOR
        mtextClear!!.setOnClickListener {
            mData.clear()
            serverDataAdapter!!.notifyDataSetChanged()
        }
        pause.setOnClickListener {
//            DebugTools.instance.pause = !DebugTools.instance.pause
//            pause.text = if (DebugTools.instance.pause) "开启" else "暂停"
        }

        mhideShow = findViewById(R.id.text_hide_show)
        recyclerView!!.layoutParams.height = SizeUtils.dp2px(150f)
        recyclerView!!.layoutParams.width = SizeUtils.getScreenWidth(context)
        mhideShow!!.setOnClickListener {
            isFullScreen = !isFullScreen
            mhideShow!!.text = if (isFullScreen) "半屏" else "全屏"
            val params = recyclerView!!.layoutParams
            params.height = if (isFullScreen) SizeUtils.getScreenHeight(context) - SizeUtils.dp2px(150f) else SizeUtils.dp2px(150f)
            layoutParams = params
        }
        closeView = findViewById(R.id.close_button)


        findViewById<View>(R.id.close_button).setOnClickListener {
            if (isSmallStyle) {
                updateViewToLarge()
                isSmallStyle = false
                closeView.text = "缩小"
            } else {
                updateViewToSmall()
                isSmallStyle = true
                closeView.text = "打开"
            }
        }
    }

    fun updateJsonText(json: ServerDataBean) {
        mData.add(json)
        serverDataAdapter!!.notifyItemInserted(mData.size - 1)
        serverDataAdapter!!.notifyItemRangeChanged(0, mData.size + 1)
    }


    inner class ServerDataAdapter : RecyclerView.Adapter<ServerDataHolder>() {


        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServerDataHolder {
            return ServerDataHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.server_data_item, viewGroup, false))
        }

        override fun onBindViewHolder(serverDataHolder: ServerDataHolder, i: Int) {
            val serverDataBean = mData[i]

            if (!TextUtils.isEmpty(serverDataBean.json)) {
                serverDataHolder.jsonTextView.bindJson(serverDataBean.json,true)
            }
            if (serverDataBean.isPost) {
                serverDataHolder.jsonUrlView.text = "post请求   " + serverDataBean.url
                serverDataHolder.jsonUrlView.setTextColor(Color.parseColor("#3ACDFE"))
            } else {
                serverDataHolder.jsonUrlView.text = serverDataBean.url
                serverDataHolder.jsonUrlView.setTextColor(Color.parseColor("#FF4081"))
            }
            serverDataHolder.jsonUrlView.setTextColor(if (serverDataBean.isPost) Color.parseColor("#3ACDFE") else Color.parseColor("#FF4081"))
            serverDataHolder.mCopyView.setOnClickListener { v ->
                Toast.makeText(context,"json数据已经复制",Toast.LENGTH_SHORT).show()
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("", serverDataBean.json)
                cm.primaryClip = clipData
            }
        }


        override fun getItemCount(): Int {
            return mData.size
        }
    }


    inner class ServerDataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val jsonTextView = itemView.findViewById(R.id.rv_json) as JsonRecyclerView
        val jsonUrlView = itemView.findViewById(R.id.title_url) as TextView
        val mCopyView = itemView.findViewById(R.id.text_copy) as TextView

        init {
            jsonTextView.setValueTextColor(Color.parseColor("#303F9F"))
            jsonTextView.setTextSize(14f)
            jsonTextView.setScaleEnable(true)
        }

    }


    fun openServerDataView() {

        if (isAdd) {
            return
        }

        if (manager == null) {
            manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }

        val windowParamsType: Int = if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        if (manager != null) {
            params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    windowParamsType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT)
            params!!.y = 0
            params!!.x = params!!.y

            params!!.width = SizeUtils.getScreenWidth(context)
            params!!.gravity = Gravity.START or Gravity.TOP
            //加载View界面
            manager!!.addView(this, params) //把View添加到WindowManager中
            isAdd = true
        }

    }


    fun setServerDataView(visibility: Int) {
        this.visibility = visibility
    }

    fun release() {
        isAdd = false
        manager!!.removeView(this)
        manager = null
        mData.clear()
        serverDataAdapter = null
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isSmallStyle) {
            true
        } else super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //获取到状态栏的高度
        val frame = Rect()
        getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.rawX
        y = event.rawY - statusBarHeight // statusBarHeight是系统状态栏的高度

        when (event.action) {
            MotionEvent.ACTION_DOWN // 捕获手指触摸按下动作
            -> {
                // 获取相对View的坐标，即以此View左上角为原点
                mTouchX = event.x
                mTouchY = event.y
                mStartX = x
                mStartY = y
            }
            MotionEvent.ACTION_MOVE // 捕获手指触摸移动动作
            -> updateViewPosition()
            MotionEvent.ACTION_UP // 捕获手指触摸离开动作
            -> {
                updateViewPosition()
                mTouchY = 0f
                mTouchX = mTouchY
                if (abs(x - mStartX) < 5 && abs(y - mStartY) < 5) {
                    if (isSmallStyle) {
                        updateViewToLarge()
                        isSmallStyle = false
                        closeView.text = "缩小悬浮窗"
                    } else {
                    }
                }
            }
        }
        return true
    }

    private fun updateViewPosition() {
        // 更新浮动窗口位置参数
        params!!.x = (x - mTouchX).toInt()
        params!!.y = (y - mTouchY).toInt()
        manager!!.updateViewLayout(this, params) // 刷新显示
    }


    private fun updateViewToSmall() {
        params!!.width = SizeUtils.dp2px(40f)
        params!!.height = SizeUtils.dp2px(40f)
        manager!!.updateViewLayout(this, params)
        isSmallStyle = true
    }

    private fun updateViewToLarge() {
        params!!.width = SizeUtils.getScreenWidth(context)
        params!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        manager!!.updateViewLayout(this, params)
    }
}


