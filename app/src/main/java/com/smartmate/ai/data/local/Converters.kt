package com.smartmate.ai.data.local

import androidx.room.TypeConverter
import com.smartmate.ai.domain.Category
import com.smartmate.ai.domain.Priority
import com.smartmate.ai.domain.RepeatOption

class Converters {
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromRepeatOption(value: RepeatOption): String = value.name

    @TypeConverter
    fun toRepeatOption(value: String): RepeatOption = RepeatOption.valueOf(value)
}
