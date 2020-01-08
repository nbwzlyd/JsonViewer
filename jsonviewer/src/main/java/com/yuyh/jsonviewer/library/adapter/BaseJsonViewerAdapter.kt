package com.yuyh.jsonviewer.library.adapter

import androidx.recyclerview.widget.RecyclerView
import android.text.style.ForegroundColorSpan

/**
 * Created by yuyuhang on 2017/11/30.
 */
abstract class BaseJsonViewerAdapter<VH : androidx.recyclerview.widget.RecyclerView.ViewHolder> : androidx.recyclerview.widget.RecyclerView.Adapter<VH>() {
    companion object {

        var KEY_COLOR = -0x6dd867
        var TEXT_COLOR = -0xc54ab6
        var NUMBER_COLOR = -0xda551e
        var BOOLEAN_COLOR = -0x67d80
        var URL_COLOR = -0x992d2b
        var NULL_COLOR = -0x10a6cb
        var BRACES_COLOR = -0xb5aaa1

        var TEXT_SIZE_DP = 12f
    }
}
