package zikrulla.production.quranapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import zikrulla.production.quranapp.R
import zikrulla.production.quranapp.service.AudioService
import zikrulla.production.quranapp.util.Audios
import zikrulla.production.quranapp.util.Constants
import zikrulla.production.quranapp.util.Edition

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}