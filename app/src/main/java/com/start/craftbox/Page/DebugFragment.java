package com.start.craftbox.Page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.start.craftbox.Activitys.FileManagerActivity;
import com.start.craftbox.Activitys.PatchCreatorActivity;
import com.start.craftbox.Activitys.PatchManagerActivity;
import com.start.craftbox.Entity.User;
import com.start.craftbox.R;
import com.start.craftbox.Tools.SPTool;

public class DebugFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        MaterialButton btn = root.findViewById(R.id.file_manager_start);
        btn.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), FileManagerActivity.class);
            intent.putExtra(FileManagerActivity.EXTRA_CHOICE_MODE, 0);
            requireActivity().startActivity(intent);
        });

        MaterialButton btn2 = root.findViewById(R.id.code_editor_start);
        btn2.setOnClickListener(view -> {
            Toast.makeText(requireContext(), "TODO 需要转换为Activity", Toast.LENGTH_SHORT).show();
        });

        MaterialButton btn3 = root.findViewById(R.id.dislogin_start);
        btn3.setOnClickListener(view -> {
            User.logout(requireContext());
            Toast.makeText(requireContext(), "已注销,请重启App", Toast.LENGTH_SHORT).show();
        });

        MaterialButton btn4 = root.findViewById(R.id.cancle_init_start);
        btn4.setOnClickListener(view -> {
            SPTool.saveBoolean(requireContext(), "isinit", false);
            Toast.makeText(requireContext(), "已取消初始化,请重启App", Toast.LENGTH_SHORT).show();
        });

        MaterialButton btn5 = root.findViewById(R.id.patch_manager_start);
        btn5.setOnClickListener(view -> startActivity(new PatchManagerActivity()));

        MaterialButton btn6 = root.findViewById(R.id.patch_creater_start);
        btn6.setOnClickListener(view -> startActivity(new PatchCreatorActivity()));
    }

    private void startActivity(Activity newactivity) {
        Intent intent = new Intent(requireContext(), newactivity.getClass());
        startActivity(intent);
    }


}