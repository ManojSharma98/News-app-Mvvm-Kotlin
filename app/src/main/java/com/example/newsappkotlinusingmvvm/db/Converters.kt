package com.example.newsappkotlinusingmvvm.db

import androidx.room.TypeConverter
import com.example.newsappkotlinusingmvvm.model.Source


class Converters {

    @TypeConverter
    fun fromSource(source: Source) :String{
        return source.name
    }
    @TypeConverter
    fun toSource(name:String):Source
    {
        return Source(name,name)
    }
}