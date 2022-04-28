package com.bluixe.lecture2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException


class MainActivity : AppCompatActivity() {
    var difficulty = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.recycler_view)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = SearchItemAdapter(this)
//        val data = (1..100).map { "这是第${it}行" }
        val data = listOf<String>("两数之和","两数相加","无重复字符的最长子串","寻找两个正序数组的中位数",
            "最长回文子串","Z 字形变换","整数反转","字符串转换整数 (atoi)","回文数","正则表达式匹配",
            "盛最多水的容器","整数转罗马数字","罗马数字转整数","最长公共前缀","三数之和","最接近的三数之和",
            "电话号码的字母组合","四数之和","删除链表的倒数第 N 个结点","有效的括号","合并两个有序链表","括号生成",
            "合并K个升序链表","两两交换链表中的节点","K 个一组翻转链表","删除有序数组中的重复项","移除元素",
            "实现 strStr()","两数相除","串联所有单词的子串","下一个排列","最长有效括号","搜索旋转排序数组",
            "在排序数组中查找元素的第一个和最后一个位置","搜索插入位置","有效的数独","解数独","外观数列",
            "组合总和","组合总和 II","缺失的第一个正数","接雨水","字符串相乘","通配符匹配","跳跃游戏 II",
            "全排列","全排列 II","旋转图像","字母异位词分组","Pow(x, n)")
        this.difficulty = listOf<String>("EASY","MEDIUM","MEDIUM","HARD","MEDIUM","MEDIUM","MEDIUM",
            "MEDIUM","EASY","HARD","MEDIUM","MEDIUM","EASY","EASY","MEDIUM","MEDIUM","MEDIUM",
            "MEDIUM","MEDIUM","EASY","EASY","MEDIUM","HARD","MEDIUM","HARD","EASY","EASY","EASY",
            "MEDIUM","HARD","MEDIUM","HARD","MEDIUM","MEDIUM","EASY","MEDIUM","HARD","MEDIUM",
            "MEDIUM","MEDIUM","HARD","HARD","MEDIUM","HARD","MEDIUM","MEDIUM","MEDIUM","MEDIUM",
            "MEDIUM","MEDIUM")

        adapter.setContentList(data)
        rv.adapter = adapter
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val et = findViewById<EditText>(R.id.words_et)
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                adapter.setFilter(p0.toString())
            }
        })
    }
    fun getInfo(): List<String>? {
        var str: String? = ""
        try {
            val info = application.assets.open("info.txt")
            val len: Int = info.available()
            val buffer = ByteArray(len)
            info.read(buffer)
            str = String(buffer)
            info.close()


        } catch (e: IOException) {
            e.printStackTrace()
        }
        return str?.split("\n")
    }
}

