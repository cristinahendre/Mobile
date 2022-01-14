package com.example.roomversion.domain

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class DateDeserializer : JsonDeserializer<LocalDate?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOF: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        if (jsonElement == null) return null
        val dateStr: Long = jsonElement.asString.toLong()
        try {
            return Instant.ofEpochMilli(dateStr).atZone(ZoneId.systemDefault())
                .toLocalDate()
          //  return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return null
    }
}