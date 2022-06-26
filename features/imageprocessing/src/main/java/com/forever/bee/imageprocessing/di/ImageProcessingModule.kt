/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imageprocessing.di

import com.forever.bee.common.utils.dependencies.CoroutineDispatchersProvider
import com.forever.bee.common.utils.dependencies.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck

@Module
@DisableInstallInCheck
abstract class ImageProcessingModule {
    @Binds
    abstract fun bindDispatchersProvider(
        dispatcherProvider: CoroutineDispatchersProvider
    ): DispatchersProvider
}