package com.start.craftbox.Page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.start.craftbox.R
import com.start.startsetting.MaterialSettingsView

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        val settingsView = root.findViewById<MaterialSettingsView?>(R.id.settingsLayout)
        settingsView?.buildSettings{
            header("设置")
            switch("key_switch_1", "开关1", "开关1的描述", defaultValue = true, iconRes = R.drawable.bug_report_24px)
            switch("key_switch_2", "开关2", "开关2的描述", defaultValue = false)
            clickable("key_clickable", "可点击项", "可点击项的描述", iconRes = R.drawable.bug_report_24px, onClick = {
                Toast.makeText(requireContext(), "点击了可点击项", Toast.LENGTH_SHORT).show()
            })
        }
    }
}