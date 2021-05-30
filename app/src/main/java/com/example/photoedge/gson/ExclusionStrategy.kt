package com.example.photoedge.gson

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
 * Created to be added to [com.google.gson.Gson] in order to exclude any field with the [Exclude]
 * annotation.
 */
class ExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }

    override fun shouldSkipField(field: FieldAttributes): Boolean {
        return field.getAnnotation(Exclude::class.java) != null
    }
}
