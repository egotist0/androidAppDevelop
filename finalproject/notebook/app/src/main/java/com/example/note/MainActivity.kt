package com.example.note

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.example.note.adapter.NoteAdapter
import com.example.note.dao.CRUD
import com.example.note.dao.NoteDatabase
import com.example.note.entity.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : BaseActivity(), OnItemClickListener {


    private var dbHelper: NoteDatabase? = null

    private val context: Context = this

    //输出
    val TAG = "System==="

    //悬浮按钮组件
    private var fa_btn: FloatingActionButton? = null

    private var adapter: NoteAdapter? = null
    private val noteList: MutableList<Note> = ArrayList()
    private var myToolbar: Toolbar? = null
//    private var incompleteNum=0
//    private var completeNum=0


    //返回的mode值，-1表示无操作，0表示新增，1表示修改，2表示删除
    var returnMode = 0

    //弹出菜单
    private var popupWindow: PopupWindow? = null

    //灰色蒙版
    private var popupCover: PopupWindow? = null
    private var viewGroup: ViewGroup? = null
    private var coverView: ViewGroup? = null
    private var main: RelativeLayout? = null


    //显示矩阵，显示手机屏幕的宽高
    private var metrics: DisplayMetrics? = null

    private lateinit var textView: TextView
    private lateinit var imageView: ImageView

    private var sharedPreferences: SharedPreferences? = null



    //按顺序运行的
    override fun onCreate(savedInstanceState: Bundle?) {
        //setNightMode()
        //ChangeModeController.getInstance().init

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //recreate();
        //获取组件
        fa_btn = findViewById(R.id.fab)
        //        tv = findViewById(R.id.tv);
        val listView = findViewById<ListView>(R.id.lv)
        adapter = NoteAdapter(applicationContext, noteList)
        listView.setAdapter(adapter)
        myToolbar = findViewById(R.id.myToolbar)

        //设置Action Bar，自定义Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //设置toolber取代action bar
        myToolbar!!.setNavigationIcon(R.drawable.ic_main_dehaze_24) //设置菜单栏图标
        //刷新页面
        refreshListView() //访问数据库后就进行刷新

        val countList = CRUD(context)
        countList.open()
        var (incompleteNum, completeNum) = countList.getState()
        countList.close()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannel(notificationManager)
        sendNotification(notificationManager,incompleteNum,completeNum)

        //给lv设置点击事件
        listView.setOnItemClickListener(this)
        //点击悬浮按钮，设置点击事件
        fa_btn!!.setOnClickListener(View.OnClickListener { //创建意图
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            //设置新建笔记的mode值，与修改笔记的mode值为3进行区分
            intent.putExtra("mode", 4)
            //启动活动，获取结果
            startActivityForResult(intent, 0)
        })
        initPopUpView() //初始化弹出窗口
        //设置事件监听
        myToolbar!!.setNavigationOnClickListener(View.OnClickListener { showPopUpView() })
    }

    //初始化一个弹出窗口
    fun initPopUpView() {
        //渲染布局
        val layoutInflater =
            this@MainActivity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //要显示的内容
        viewGroup = layoutInflater.inflate(R.layout.setting_layout, null) as ViewGroup
        coverView = layoutInflater.inflate(R.layout.setting_cover, null) as ViewGroup
        main = findViewById(R.id.main_layout)
        metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics) //获取屏幕的宽高
    }

    //获取屏幕的宽高
    fun showPopUpView() {
        val width = metrics!!.widthPixels
        val height = metrics!!.heightPixels
        popupCover = PopupWindow(coverView, width, height, false) //focusable表示无法获得焦点
        popupWindow = PopupWindow(viewGroup, (width * 0.7).toInt(), height, true)
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        //在主界面加载成功之后，显示弹出
        findViewById<View>(R.id.main_layout).post {
            //显示位置,在左上角显示
            popupCover!!.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0)
            popupWindow!!.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0)

            textView =viewGroup!!.findViewById(R.id.setting_settings_text)
            imageView=viewGroup!!.findViewById(R.id.setting_settings_image)

            textView!!.setOnClickListener{
                val intent=Intent(this@MainActivity, UserSettingsActivity::class.java)
                startActivity(intent)
            }
            imageView!!.setOnClickListener{
                val intent=Intent(this@MainActivity, UserSettingsActivity::class.java)
                startActivity(intent)
            }


            coverView!!.setOnTouchListener { v, event ->
                popupWindow!!.dismiss() //弹出窗口消失
                true
            }
            popupWindow!!.setOnDismissListener {
                popupCover!!.dismiss() //对弹出窗口进行监听，若消失，则灰色蒙版也消失
            }


        }
    }

    /**
     * 引入menu菜单栏
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        //显示搜索栏
        val mySearch = menu.findItem(R.id.action_search)
        val searchView = mySearch.actionView as SearchView
        //设置搜索默认提示文字
        searchView.queryHint = "搜索"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //提交是进行搜索
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            //输入字符改变是进行搜索
            override fun onQueryTextChange(newText: String): Boolean {
                adapter!!.filter.filter(newText)
                if (returnMode != -1) refreshListView()
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * 设置菜单栏按钮监听
     *
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> AlertDialog.Builder(this@MainActivity)
                .setMessage("确定要全部删除吗？")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    dbHelper = NoteDatabase(context)
                    val db = dbHelper!!.writableDatabase
                    db.delete("notes", null, null) //删除数据表的所有数据
                    db.execSQL("update sqlite_sequence set seq=0 where name='notes'") //将索引设置为1
                    refreshListView()
                }
                .setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }
                .create().show()
        }
        return super.onOptionsItemSelected(item)
    }

    //接收startActivityForResult的结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val note_Id: Long //笔记的id，主键
        //接收结果
        returnMode = data!!.extras!!.getInt("mode", -1)
        val content = data.extras!!.getString("content")
        val time = data.extras!!.getString("time")
        val tag = data.extras!!.getInt("tag", 1)
        val image = data.extras!!.getString("image")
        val video = data.extras!!.getString("video")
        val state=data.extras!!.getInt("state",0)
        Log.e("TAG", "get img uri to be store:" + data.extras!!.getString("image"))
        note_Id = data.extras!!.getLong("id", 0)
        Log.d(TAG, "returnMode:$returnMode")
        if (returnMode == 1) { //修改笔记
            //将结果写入Note实体类
            val newNote = Note(content, time, tag, image, video,state)
            newNote.id = note_Id //需要通过id进行修改
            val op = CRUD(context)
            op.open()
            op.updateNote(newNote)
            op.close()
        } else if (returnMode == 0) { //新增笔记
            //将结果写入Note实体类
            val newNote = Note(content, time, tag, image, video,state)
            val op = CRUD(context)
            op.open()
            op.addNote(newNote)
            op.close()
        } else if (returnMode == 2) { //删除笔记
            Log.d(TAG, "returnMode:$returnMode")
            val curNote = Note()
            curNote.id = note_Id
            val op = CRUD(context)
            op.open()
            op.removeNote(curNote)
            op.close()
        } else {
        }

        refreshListView() //访问数据库后就进行刷新
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 更新内容
     */
    fun refreshListView() {
        val op = CRUD(context)
        op.open()
        //设置adapter
        if (noteList.size > 0) {
            noteList.clear()
        }
        noteList.addAll(op.allNotes)
        //        if(sharePreferences.getBoolean("reverseSort",false)) sorNotes(noteList,2);
//        else sorNotes(noteList,1);
        op.close()
        adapter!!.notifyDataSetChanged()
    }

    /**
     * 重写点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.lv -> {
                val curNote = parent.getItemAtPosition(position) as Note
                val intent = Intent(this@MainActivity, EditActivity::class.java)
                intent.putExtra("content", curNote.content)
                intent.putExtra("id", curNote.id)
                intent.putExtra("time", curNote.time)
                //修改笔记的mode设置为3，与新建笔记的mode值为4进行区分
                intent.putExtra("mode", 3)
                intent.putExtra("tag", curNote.tog)
                intent.putExtra("image", curNote.image)
                Log.e("TAG", "img to be show:" + noteList[position].image)
                intent.putExtra("video", curNote.video)
                intent.putExtra("state",curNote.state)
                startActivityForResult(intent, 1)
                Log.d(TAG, "onItemClick" + (position + 1))
            }
        }
    }
    fun createChannel(notificationManager:NotificationManager){
        //版本高于8.0创建渠道
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //id指该渠道特定的表示，name指在设置中显示的该渠道名字，importance指该通知默认的重要性
            val notificationChannel = NotificationChannel("normal","普通的通知",NotificationManager.IMPORTANCE_DEFAULT)
            //高重要性：震动，横幅，通知栏显示
            val notificationChannel1 = NotificationChannel("important","重要的通知",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.createNotificationChannel(notificationChannel1)

        }


    }
    fun sendNotification(notificationManager:NotificationManager,incompleteNum:Int,completeNum:Int){
        val intent= Intent(this,MainActivity::class.java)
        //PendingIntent
        //用于点击通知后跳转到相应界面
        val pendingIntent=PendingIntent.getActivity(this,0,intent,0)
        //适配各种系统
        //构建一个通知对象
        val notification =NotificationCompat.Builder(this,"important")
            .setContentTitle("你好呀！")
            .setContentText("现在有${incompleteNum}件事情没有完成，完成率为${completeNum.toFloat()*100f/(completeNum.toFloat()+incompleteNum.toFloat())}%，还不能休息哦~")
            //传入一个Int变量即可
            .setSmallIcon(R.mipmap.ic_launcher)
            //传入一个bitmap变量
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher_round))
            //运用pendingintent
            .setContentIntent(pendingIntent)
            //点击通知后会自动取消
            .setAutoCancel(true)
            .build()
        //显示这个通知对象
        notificationManager.notify(1,notification)

    }


}