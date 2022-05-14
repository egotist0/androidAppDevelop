package com.example.note.translate

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.note.BaseActivity
import com.example.note.R
import com.example.note.translate.api2.YoudaoBean
import com.example.note.translate.interceptor.TimeConsumeInterceptor
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class TranslateActivity : BaseActivity(){
    private var btn: Button? = null
    private var searchResult: TextView? = null
    private var searchText:String=" "

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_translate)

        //识别输入栏内容

        val et = findViewById<EditText>(R.id.et_word)
//        et.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            //此处的p0是在输入栏输入的内容
//            override fun afterTextChanged(p0: Editable?) {
//                searchText=p0.toString()
//            }
//        })

        btn = findViewById(R.id.send_request)
        searchResult = findViewById(R.id.show_text)

        btn?.setOnClickListener {
            updateShowTextView("", false)
            click()
        }

        val translateWords = intent.getStringExtra("translate_words")
        searchText=translateWords!!
        if (!TextUtils.isEmpty(translateWords)) {
            et?.setText(translateWords)
            click()
        }
    }

    //OkHttp 版本提供了EventListener接口，可以让调用者接收一系列网络请求过程中的事件，例如DNS解析、TSL/SSL连接、Response接收等。
    //通过继承此接口，调用者可以监视整个应用中网络请求次数、流量大小、耗时(比如dns解析时间，请求时间，响应时间等等)情况。
    //EventListener是一个类，包括了很多函数
    private val okhttpListener = object : EventListener() {
        //“I18”代表“国际化”。 Android 的本地化资源机制使您无需修改​​代码即可支持多种语言环境。
        @SuppressLint("SetTextI18n")
        //域名解析
        //domain name：域名
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            //updateShowTextView("\nDns Search: $domainName")
        }

        //响应
        @SuppressLint("SetTextI18n")
        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            //updateShowTextView("\nResponse Start")
        }
    }

    //客户端对象的建立
    //通过它我们可以方便的对OkHttpClient进行设置
    private val client: OkHttpClient = OkHttpClient.Builder()
        //添加拦截器，添加多个自定义的拦截器，例如打印请求与响应内容，为请求添加统一header等。
        //添加的是应用拦截器
        //主要用于查看请求信息及返回信息，如链接地址、头信息、参数信息等
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener)
        //最后构建
        .build()

    //创建gson---JSON的解析器
    private val gson = GsonBuilder().create()



    @SuppressLint("SetTextI18n")
    //append参数是布尔型变量，默认是true
    private fun updateShowTextView(text: String, append: Boolean = true) {
        if (Looper.getMainLooper() !== Looper.myLooper()) {
            // 如果当前是子线程，提交到主线程中去更新 UI.
            runOnUiThread {
                updateShowTextView(text, append)
            }
        } else {

            //若append为true，则输出文本为原文本加上添加的text
            //若为false，则输出文本仅为text
            searchResult?.text = if (append) searchResult?.text.toString() + text else text
        }
    }

    //请求
    private fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            //请求的目标网址
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        //异步的请求方法，但Callback是执行在子线程中的，因此不能在此进行UI更新操作
        client.newCall(request).enqueue(callback)

    }

    //点击之后的操作
    private fun click() {
        val url = "https://dict.youdao.com/jsonapi?q=${searchText}"
        request(url, object : Callback {
            //执行call对象如果发生了错误
            override fun onFailure(call: Call, e: IOException) {
                //e是指发生异常的exception
                //printStackTrace()在命令行打印异常信息在程序中出错的位置及原因
                e.printStackTrace()
                //显示这个错误
                updateShowTextView(e.message.toString(), false)
            }

            @SuppressLint("SetTextI18n")
            //用于获取响应
            override fun onResponse(call: Call, response: Response) {
                //最终是一个字符串
                //response.isSuccessful用于检查call对象是否执行成功
                val respFormatText = if (response.isSuccessful) {

                    //返回Response对象的ResponseBody对象的内容,大概是电影列表的信息
                    val bodyString = response.body?.string()
                    /*if (bodyString != null) {
                        updateShowTextView(bodyString)
                    }
                    else{
                        updateShowTextView("nothing")
                    }*/
                    //提供两个参数，分别是json字符串以及需要转换对象的类型（解析代码）
                    val youdaoBean = gson.fromJson(bodyString, YoudaoBean::class.java)
                    var output: String? = ""
                    //updateShowTextView(bodyString!!)
                    if (youdaoBean.web_trans!=null) {
                        val tranList = youdaoBean.web_trans.web_translation[0].trans

                        for (trans in tranList) {
                            output += (trans.value + "；")
                        }
                    }
                    else{
                        output="未查找到 "
                    }


                    output = output?.substring(0, output?.length!! - 1)
                    "\n\n\n${output}\n"
                } else {
                    "\n\n\nResponse fail: ${response.body?.string()}, http status code: ${response.code}."
                }
                //显示这个字符串
                updateShowTextView(respFormatText)
            }
        })
    }


}
