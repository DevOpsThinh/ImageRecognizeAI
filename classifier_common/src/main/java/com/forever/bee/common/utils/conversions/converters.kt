/**
 * Artificial Intelligence on Android with Kotlin
 *
 * Learner: Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.common.utils.conversions

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.json.Json

class FloatArrayConverters {
    private val floatStrategy = FloatArraySerializer()

    @TypeConverter
    fun fromFloatArray(value: FloatArray): String = Json.encodeToString(floatStrategy, value)

    @TypeConverter
    fun toFloatArray(value: String): FloatArray = Json.decodeFromString(floatStrategy, value)
}