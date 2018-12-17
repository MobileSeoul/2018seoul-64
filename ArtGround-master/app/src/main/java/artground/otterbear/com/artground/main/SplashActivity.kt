package artground.otterbear.com.artground.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import artground.otterbear.com.artground.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo.animate().alpha(1.0f).withEndAction {
            logo.visibility = View.VISIBLE
        }.duration = 2000

        logo_text.animate().alpha(1.0f).withEndAction {
            logo_text.visibility = View.VISIBLE
            handler.postDelayed(appStartRunnable, 800)
        }.duration = 2000

//
//        val imageView = findViewById<ImageView>(R.id.logo)
//        (imageView.getBackground() as AnimationDrawable).start()
//
//        val prefs = AGPreferences(applicationContext)
//        if(prefs.text.equals("USED")){
//            val mainitent = Intent(this, MainActivity::class.java)
//            startActivity(mainitent)
//            finish()
//        }else{
//            val intent = Intent(this, PreperationActivity::class.java)
//            intent.putExtra("state", "launch")
//            startActivity(intent)
//            finish()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(appStartRunnable)
    }

    private val appStartRunnable = Runnable {
        val preperationCompleted = AGPreferences.isPreperationCompleted(this@SplashActivity)
        startActivity(Intent(this@SplashActivity, if (preperationCompleted) MainActivity::class.java else PreperationActivity::class.java))
        finish()
    }
}