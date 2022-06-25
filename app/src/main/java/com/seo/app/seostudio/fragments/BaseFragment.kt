package com.seo.app.seostudio.fragments

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseFragment:Fragment() {
    private fun NavController.isFragmentRemovedFromBackStack(destinationId: Int) =
        try {
            getBackStackEntry(destinationId)
            false
        } catch (e: Exception) {
            true
        }

    
    private val navOptions = NavOptions.Builder()
        .setEnterAnim(android.R.anim.fade_in)
        .setExitAnim(android.R.anim.fade_out)
        .setPopEnterAnim(android.R.anim.fade_in)
        .setPopExitAnim(android.R.anim.fade_out).build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun navigateToFragment(fragmentId: Int,actionId: Int?, bundle: Bundle = bundleOf()) {
        CoroutineScope(Dispatchers.Main).launch {
            findNavController().apply {
                if (currentDestination?.id != fragmentId) {
                    if (!isFragmentRemovedFromBackStack(fragmentId)) {
                        popBackStack(fragmentId, false)
                    } else {
                        try {
                            if (actionId != null)
                                navigate(actionId, bundle,navOptions)
                            else
                                navigate(fragmentId, bundle, navOptions)
                        } catch (e: Exception) {
                            navigate(fragmentId, bundle, navOptions)
                        }
                    }
                }
            }
        }
    }


}