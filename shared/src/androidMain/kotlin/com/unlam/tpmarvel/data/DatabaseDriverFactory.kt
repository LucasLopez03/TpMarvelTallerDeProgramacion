package com.unlam.tpmarvel.data

import app.cash.sqldelight.db.*
import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.unlam.tpmarvel.AppDatabase

class DatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context,
            "marvel.db")
    }
}