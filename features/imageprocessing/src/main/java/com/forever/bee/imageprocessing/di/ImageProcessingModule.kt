/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imageprocessing.di

import androidx.lifecycle.ViewModelProvider
import com.forever.bee.common.utils.dependencies.CoroutineDispatchersProvider
import com.forever.bee.common.utils.dependencies.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.migration.DisableInstallInCheck
import dagger.multibindings.IntoMap

@Module
@DisableInstallInCheck
abstract class ImageProcessingModule {
    @Binds
    abstract fun bindDispatchersProvider(
        dispatcherProvider: CoroutineDispatchersProvider
    ): DispatchersProvider

    @Binds
    @Reusable
    abstract fun bindViewModelFactory(fac: ViewModelFac): ViewModelProvider.Factory
}