package com.example.hector25

import android.app.Application
import com.example.hector25.di.initKoin
import io.ktor.http.ContentType

class Hector25Application : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}