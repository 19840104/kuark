package org.kuark.config.context

import java.util.*

/**
 * 客户端信息
 */
class ClientInfo {

    /**
     * 请求的ip
     */
    var ip: String? = null

    /**
     * 访问的域名
     */
    var domain: String? = null

    /**
     * 访问的url
     */
    var url: String? = null

    /**
     * 请求的参数
     */
    var params: String? = null

    /**
     * 请求的数据
     */
    var requestData: String? = null

    /**
     * 请求referer
     */
    var requestReferer: String? = null

    /**
     * 请求类型(GET/POST等)
     */
    var requestType: String? = null

    /**
     * 客户端操作系统
     */
    var os: String? = null

    /**
     * 客户端浏览器
     */
    var browser: String? = null

    /**
     * 客户端地区-语言
     */
    var locale: Locale? = null

    /**
     * 客户端时区
     */
    var timeZone: TimeZone? = null

}