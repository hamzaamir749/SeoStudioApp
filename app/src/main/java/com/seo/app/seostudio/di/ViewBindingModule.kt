package com.seo.app.seostudio.di

import android.content.Context
import com.seo.app.seostudio.activties.MainActivity
import com.seo.app.seostudio.databinding.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext


@Module
@InstallIn(FragmentComponent::class)
class ViewBindingModuleFrgament {

    // Main Fragments bindings
    @Provides
    fun provideMainFragmentBinding(@ActivityContext context: Context): FragmentMainBinding {
        return FragmentMainBinding.inflate((context as MainActivity).layoutInflater)
    }


    @Provides
    fun provideSplashFragmentBinding(@ActivityContext context: Context): FragmentSplashBinding {
        return FragmentSplashBinding.inflate((context as MainActivity).layoutInflater)
    }

    @Provides
    fun providesingleFragmentBinding(@ActivityContext context: Context): FragmentSingleBinding {
        return FragmentSingleBinding.inflate((context as MainActivity).layoutInflater)
    }

    @Provides
    fun provideBulkFragmentBinding(@ActivityContext context: Context): FragmentBulkBinding {
        return FragmentBulkBinding.inflate((context as MainActivity).layoutInflater)
    }

    @Provides
    fun provideMoreFragmentBinding(@ActivityContext context: Context): FragmentMoreBinding {
        return FragmentMoreBinding.inflate((context as MainActivity).layoutInflater)
    }


}

@Module
@InstallIn(ActivityComponent::class)
class ViewBindingModuleActivity {
    @Provides
    fun provideMainActivityBinding(@ActivityContext context: Context): ActivityMainBinding {
        return ActivityMainBinding.inflate((context as MainActivity).layoutInflater)
    }


}