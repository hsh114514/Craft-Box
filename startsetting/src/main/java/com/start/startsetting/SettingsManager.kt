package com.start.startsetting

import android.content.Context
import android.content.SharedPreferences

class SettingsManager private constructor(context: Context) {

    private val sp: SharedPreferences = context.applicationContext.getSharedPreferences(
        DEFAULT_SP_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val DEFAULT_SP_NAME = "start_settings_config"

        @Volatile
        private var INSTANCE: SettingsManager? = null

        @JvmStatic
        fun initialize(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context).also { INSTANCE = it }
            }
        }

        fun getInstance(): SettingsManager {
            return INSTANCE ?: throw IllegalStateException(
                "SettingsManager 尚未初始化。请先在 Application 中调用 initialize(context)。"
            )
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is Boolean -> sp.getBoolean(key, defaultValue) as T
            is String -> sp.getString(key, defaultValue) as T
            is Int -> sp.getInt(key, defaultValue) as T
            is Float -> sp.getFloat(key, defaultValue) as T
            is Long -> sp.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("不支持的存储数据类型")
        }
    }

    fun <T : Any> put(key: String, value: T) {
        val editor = sp.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> throw IllegalArgumentException("不支持的存储数据类型")
        }
        editor.apply()
    }
}