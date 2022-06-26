package com.forever.bee.imageprocessing.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFac @Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = viewModels[modelClass]

        if (creator == null) {
            for ((k, v) in viewModels) {
                if (modelClass.isAssignableFrom(k)) {
                    creator = v
                    break
                }
            }
        }

        if (creator == null) {
            throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}