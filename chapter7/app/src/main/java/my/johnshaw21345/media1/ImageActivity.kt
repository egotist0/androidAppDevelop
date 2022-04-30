package my.johnshaw21345.media1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList


class ImageActivity : AppCompatActivity() {
    private val pages: MutableList<View> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        addGif("https://img.soogif.com/I7Mh152DDjLjCp4agQBMI7QuvvsEEB9e.gif")
        addImage("https://sg-wotp.wgcdn.co/dcont/fb/image/wallpaper_1_1680x1050.jpg")
        addImage("https://sg-wotp.wgcdn.co/dcont/fb/image/wallpaper_2_1680x1050.jpg")
        addImage("https://sg-wotp.wgcdn.co/dcont/fb/image/wallpaper_3_1680x1050.jpg")
        addImage("https://sg-wotp.wgcdn.co/dcont/fb/image/wallpaper_4_1680x1050.jpg")

        val adapter = ViewAdapter()
        adapter.setDatas(pages)
        findViewById<ViewPager>(R.id.view_pager).adapter = adapter
    }

    private fun addImage(resId: Int) {
        val imageView = layoutInflater.inflate(R.layout.activity_image_item, null) as ImageView
        Glide.with(this)
            .load(resId)
            .into(imageView)
        pages.add(imageView)
    }


    private fun addImage(path: String) {
        val imageView = layoutInflater.inflate(R.layout.activity_image_item, null) as ImageView
        Glide.with(this)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
        pages.add(imageView)
    }

    private fun addGif(path: String) {
        val imageView = layoutInflater.inflate(R.layout.activity_image_item, null) as ImageView
        Glide.with(this)
            .asGif()
            .load(path)
            .error(R.drawable.error)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(imageView)
        pages.add(imageView)
    }

}