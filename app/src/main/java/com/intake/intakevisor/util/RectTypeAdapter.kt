package com.intake.intakevisor.util

import android.graphics.Rect
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type

class RectTypeAdapter : JsonDeserializer<Rect> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Rect {
        val jsonObject = json?.asJsonObject
        val left = jsonObject?.get("left")?.asInt ?: 0
        val top = jsonObject?.get("top")?.asInt ?: 0
        val right = jsonObject?.get("right")?.asInt ?: 0
        val bottom = jsonObject?.get("bottom")?.asInt ?: 0
        return Rect(left, top, right, bottom)
    }
}
