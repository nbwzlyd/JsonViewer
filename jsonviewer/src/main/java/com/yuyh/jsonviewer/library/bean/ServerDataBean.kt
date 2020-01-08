package com.yuyh.jsonviewer.library.bean

import java.io.Serializable

class ServerDataBean : Serializable {

    var json: String = ""
    var url: String = ""
    var isPost: Boolean = false

    constructor()

    constructor(requestUrl: String, json: String) {
        this.json = json
        this.url = requestUrl
        this.isPost = false
    }
}
