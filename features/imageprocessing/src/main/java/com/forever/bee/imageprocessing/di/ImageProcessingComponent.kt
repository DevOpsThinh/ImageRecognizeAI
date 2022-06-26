/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imageprocessing.di

import android.content.Context
import com.forever.bee.imageprocessing.presentation.animeStyle.AnimeFragment
import com.forever.bee.imagerecognizeai.di.ImageProcessingModuleDependencies
import dagger.BindsInstance
import dagger.Component

/**
 * A regular Dagger Component for connect to Hilt's dependency graph
 * */
@Component(
    dependencies = [ImageProcessingModuleDependencies::class],
    modules = [ImageProcessingModule::class])
interface ImageProcessingComponent {

    fun inject(fragment: AnimeFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun moduleDependencies(sharingModuleDependencies: ImageProcessingModuleDependencies): Builder
        fun build(): ImageProcessingComponent
    }
}