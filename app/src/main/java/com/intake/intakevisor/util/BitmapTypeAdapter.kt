package com.intake.intakevisor.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import android.util.Base64

class BitmapTypeAdapter : JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {
    override fun serialize(src: Bitmap?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        src?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return JsonObject().apply {
            addProperty("image", encoded)
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Bitmap {
        val jsonObject = json?.asJsonObject
        val encodedString = jsonObject?.get("image")?.asString
        val decodedString = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}
