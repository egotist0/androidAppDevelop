package my.johnshaw21345.media1

import android.graphics.PixelFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.SeekBar
import android.widget.VideoView

class VideoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val video: VideoView = findViewById(R.id.videoView)
        val buttonPause: Button = findViewById(R.id.buttonPause)
        val buttonPlay: Button = findViewById(R.id.buttonPlay)



        buttonPause.setOnClickListener { video.pause() }
        buttonPlay.setOnClickListener { video.start() }


        video.holder.setFormat(PixelFormat.TRANSPARENT)
        video.setZOrderOnTop(true)
        video.setVideoPath("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218093206z8V1JuPlpe.mp4")


        var mediaController = MediaController(this)
        mediaController.setAnchorView(video)

        mediaController = object : MediaController(this) {
            override fun show(timeout: Int) {
                super.show(0)
            }
        }



        mediaController.setPadding(0, 0, 0, 1150)

        video.setMediaController(mediaController)
        video.start()


    }
}