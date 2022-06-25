package com.seo.app.seostudio.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebBackForwardList
import android.webkit.WebHistoryItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.seo.app.seostudio.R
import com.seo.app.seostudio.activties.MainActivity
import com.seo.app.seostudio.ads.InterstitialHelper
import com.seo.app.seostudio.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment() {

    @Inject
    lateinit var binding: FragmentMainBinding

    var seacrhfrag = SingleFragment()
    var bulkfrag = BulkFragment()
    var morefrag = MoreFragment()
    lateinit var currentfrag: Fragment

    private var isExitpressed = false
    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Home backpress handle
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                homeBackPress()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressedCallback)
        ////
        var fm = childFragmentManager

        try {
            fm.findFragmentByTag("3")?.let {
                fm.beginTransaction().remove(it).commit()
            }
            fm.findFragmentByTag("2")?.let {
                fm.beginTransaction().remove(it).commit()
            }
            fm.findFragmentByTag("1")?.let {
                fm.beginTransaction().remove(it).commit()
            }
        } catch (e: java.lang.Exception) {
        } catch (e: Exception) {
        }

        fm.beginTransaction().add(R.id.container, morefrag, "3").commit()
        fm.beginTransaction().hide(morefrag).commit()
        fm.beginTransaction().add(R.id.container, bulkfrag, "2").commit()
        fm.beginTransaction().hide(bulkfrag).commit()
        fm.beginTransaction().add(R.id.container, seacrhfrag, "1").commit()
        currentfrag = seacrhfrag
        binding.bottomnavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            binding.bottomnavigation.getBadge(itemId)?.let { badgeDrawable ->
                if (badgeDrawable.isVisible)  // check whether the item showing badge
                    binding.bottomnavigation.removeBadge(itemId)  //  remove badge notification
            }
            return@setOnNavigationItemSelectedListener when (item.itemId) {
                R.id.bottomNavigationhomeMenuId -> {
                    goToFragment(seacrhfrag)
                    true
                }
                R.id.bottomNavigationtrendingMenuId -> {
                    goToFragment(bulkfrag)
                    true
                }
                R.id.bottomNavigationprogressMenuId -> {
                    goToFragment(morefrag)
                    true
                }
                else -> false
            }
        }

        //Splash ad
        CoroutineScope(Dispatchers.IO).launch {
            delay(200)
            withContext(Dispatchers.Main) {
                activity?.let {
                    InterstitialHelper.loadAndShowAd(
                        it, resources.getString(R.string.intertitial)
                    ) {

                    }
                }
                MainActivity.intertitialShown=false
            }
        }
        ////////
    }


    fun goToFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().hide(currentfrag).show(fragment).commit()
        currentfrag = fragment

    }

    private fun homeBackPress() {
        if (currentfrag!=seacrhfrag) {
            binding.bottomnavigation.selectedItemId=R.id.bottomNavigationhomeMenuId
            goToFragment(seacrhfrag)

        } else {
            if (isExitpressed) {
                activity?.let {
                    activity?.finish()
                }
            }
            isExitpressed = true
            Toast.makeText(requireContext().applicationContext, "Tap Again to Exit", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    isExitpressed = false
                }
            }
        }
    }

}