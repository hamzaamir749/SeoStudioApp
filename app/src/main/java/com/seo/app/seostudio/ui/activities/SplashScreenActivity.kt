package com.seo.app.seostudio.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.seo.app.seostudio.databinding.ActivitySplashScreenBinding
import com.seo.app.seostudio.utils.openActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            Log.e("TESTING", "initViews: $token")
        })


        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            openActivity(MainActivity::class.java)
            finish()
        }

    }
}