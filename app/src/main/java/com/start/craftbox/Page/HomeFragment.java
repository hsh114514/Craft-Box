package com.start.craftbox.Page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.start.craftbox.Activitys.FileManagerActivity;
import com.start.craftbox.Activitys.MdInfoActivaty;
import com.start.craftbox.MainActivity;
import com.start.craftbox.R;
import com.start.craftbox.Tools.SPTool;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        MaterialCardView card = root.findViewById(R.id.card1);

        card.setOnClickListener(view -> {
            startActivity(new MdInfoActivaty());
        });



    }

    private void startActivity(Activity newactivity) {
        Intent intent = new Intent(requireActivity(), newactivity.getClass());
        startActivity(intent);
    }
}