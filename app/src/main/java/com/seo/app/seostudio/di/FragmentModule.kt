package com.seo.app.seostudio.di

import android.content.Context
import com.seo.app.seostudio.adapters.BulkKeywordAdapter
import com.seo.app.seostudio.adapters.SimilarKeywordAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped

@InstallIn(FragmentComponent::class)
@Module
object FragmentModule {
    @Provides
    @FragmentScoped
    fun provideBulkKeyboardAdapter(@ApplicationContext context: Context) =
        BulkKeywordAdapter(context)

    @Provides
    @FragmentScoped
    fun provideSimilarKeywordAdapter(@ApplicationContext context: Context) =
        SimilarKeywordAdapter(context)
}