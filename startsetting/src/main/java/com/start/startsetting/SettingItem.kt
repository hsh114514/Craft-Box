package com.start.startsetting

sealed class SettingItem {
    data class Header(val title: String) : SettingItem()

    data class Switch(
        val key: String,
        val title: String,
        val subtitle: String? = null,
        val iconRes: Int? = null,
        val defaultValue: Boolean = false
    ) : SettingItem()

    data class Clickable(
        val key: String,
        val title: String,
        val subtitle: String? = null,
        val iconRes: Int? = null,
        val onClick: () -> Unit
    ) : SettingItem()
}