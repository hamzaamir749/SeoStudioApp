package com.seo.app.seostudio.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.seo.app.seostudio.R
import com.seo.app.seostudio.activties.MainActivity
import com.seo.app.seostudio.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
    @Inject
    lateinit var binding: FragmentSplashBinding


    private var LOADING_TIME = 4000L
    var coundownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.intertitialShown = true
        CountDownTimer()

    }

    fun CountDownTimer() {
        coundownTimer = object : CountDownTimer(LOADING_TIME, 10) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("timer", "on tick")
            }

            override fun onFinish() {
                movetonextactivity()
            }
        }
        coundownTimer?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        coundownTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        coundownTimer?.start()
    }

    fun movetonextactivity() {
        findNavController().navigate(R.id.mainFragment)
    }

}