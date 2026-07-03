package com.start.startsetting

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MaterialSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutManager = LinearLayoutManager(context)
        overScrollMode = OVER_SCROLL_NEVER
    }

    fun buildSettings(block: SettingsBuilder.() -> Unit) {
        val builder = SettingsBuilder()
        builder.block()
        val settingsAdapter = SettingsAdapter()
        adapter = settingsAdapter
        settingsAdapter.submitList(builder.getItems())
    }

    class SettingsBuilder {
        private val items = mutableListOf<SettingItem>()

        fun header(title: String) {
            items.add(SettingItem.Header(title))
        }

        fun switch(
            key: String,
            title: String,
            subtitle: String? = null,
            iconRes: Int? = null,
            defaultValue: Boolean = false
        ) {
            items.add(SettingItem.Switch(key, title, subtitle, iconRes, defaultValue))
        }

        fun clickable(
            key: String,
            title: String,
            subtitle: String? = null,
            iconRes: Int? = null,
            onClick: () -> Unit
        ) {
            items.add(SettingItem.Clickable(key, title, subtitle, iconRes, onClick))
        }

        fun getItems(): List<SettingItem> = items
    }
}