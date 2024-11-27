package com.example.inzyn.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(value: List<Int>): String {
        if (value.isEmpty()) {
            return ""
        }
        return value.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String): List<Int> {
        if (value.isEmpty()) {
            return emptyList()
        }
        return value.split(",").map { it.toInt() }
    }
}