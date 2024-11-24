package com.example.inzyn.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(value: List<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String): List<Int> {
        return value.split(",").map { it.toInt() }
    }
}