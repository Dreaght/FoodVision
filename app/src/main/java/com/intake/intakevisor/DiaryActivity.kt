package com.intake.intakevisor

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DiaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Retrieve the fragments from the intent
        val fragments = intent.getSerializableExtra("food_fragments") as? ArrayList<*>

        fragments?.forEach { byteArray ->
            val bitmap = BitmapFactory.decodeByteArray(byteArray as ByteArray?, 0, byteArray.size)
            // Use the `bitmap` (e.g., display it in an ImageView or process it)
        }
    }

}