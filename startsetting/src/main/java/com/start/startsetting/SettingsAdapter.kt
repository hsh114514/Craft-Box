package com.start.startsetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsAdapter : ListAdapter<SettingItem, RecyclerView.ViewHolder>(SettingDiffCallback) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SWITCH = 1
        private const val TYPE_CLICKABLE = 2
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is SettingItem.Header -> TYPE_HEADER
        is SettingItem.Switch -> TYPE_SWITCH
        is SettingItem.Clickable -> TYPE_CLICKABLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_setting_header, parent, false))
            TYPE_SWITCH -> SwitchViewHolder(inflater.inflate(R.layout.item_setting_switch, parent, false))
            TYPE_CLICKABLE -> ClickableViewHolder(inflater.inflate(R.layout.item_setting_clickable, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val manager = SettingsManager.getInstance()
        val item = getItem(position)

        when (holder) {
            is HeaderViewHolder -> {
                val header = item as SettingItem.Header
                holder.tvHeaderTitle.text = header.title
            }
            is SwitchViewHolder -> {
                val switchItem = item as SettingItem.Switch

                // 绑定基础文案
                holder.tvTitle.text = switchItem.title
                holder.bindIconAndSubtitle(switchItem.iconRes, switchItem.subtitle)

                // 从管理中心读取配置状态
                val savedValue = manager.get(switchItem.key, switchItem.defaultValue)
                holder.switchButton.setOnCheckedChangeListener(null)
                holder.switchButton.isChecked = savedValue

                // 统一监听整张卡片的点击
                holder.itemView.setOnClickListener {
                    val targetState = !holder.switchButton.isChecked
                    holder.switchButton.isChecked = targetState
                    manager.put(switchItem.key, targetState)
                }
            }
            is ClickableViewHolder -> {
                val clickableItem = item as SettingItem.Clickable
                holder.tvTitle.text = clickableItem.title
                holder.bindIconAndSubtitle(clickableItem.iconRes, clickableItem.subtitle)

                holder.itemView.setOnClickListener { clickableItem.onClick() }
            }
        }
    }

    // --- ViewHolder 的 XML 组件绑定 ---

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeaderTitle: TextView = view.findViewById(R.id.tv_header_title)
    }

    class SwitchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvSubtitle: TextView = view.findViewById(R.id.tv_subtitle)
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)
        val switchButton: MaterialSwitch = view.findViewById(R.id.switch_button)

        fun bindIconAndSubtitle(iconRes: Int?, subtitle: String?) {
            ivIcon.visibility = if (iconRes != null) { ivIcon.setImageResource(iconRes); View.VISIBLE } else View.GONE
            tvSubtitle.visibility = if (subtitle != null) { tvSubtitle.text = subtitle; View.VISIBLE } else View.GONE
        }
    }

    class ClickableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvSubtitle: TextView = view.findViewById(R.id.tv_subtitle)
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)

        fun bindIconAndSubtitle(iconRes: Int?, subtitle: String?) {
            ivIcon.visibility = if (iconRes != null) { ivIcon.setImageResource(iconRes); View.VISIBLE } else View.GONE
            tvSubtitle.visibility = if (subtitle != null) { tvSubtitle.text = subtitle; View.VISIBLE } else View.GONE
        }
    }

    object SettingDiffCallback : DiffUtil.ItemCallback<SettingItem>() {
        override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            if (oldItem is SettingItem.Switch && newItem is SettingItem.Switch) return oldItem.key == newItem.key
            if (oldItem is SettingItem.Clickable && newItem is SettingItem.Clickable) return oldItem.key == newItem.key
            if (oldItem is SettingItem.Header && newItem is SettingItem.Header) return oldItem.title == newItem.title
            return false
        }
        override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean = oldItem == newItem
    }
}