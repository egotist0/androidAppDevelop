package my.johnshaw21345.media1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn1).setOnClickListener {
            val intent = Intent(this,ImageActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn2).setOnClickListener {
            val intent = Intent(this,VideoActivity::class.java)
            startActivity(intent)
        }

    }
}