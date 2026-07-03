package com.start.craftbox.Activitys;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.start.craftbox.Entity.Patch.PatchNode;
import com.start.craftbox.Entity.Patch.PatchPackage;
import com.start.craftbox.Entity.Patch.SinglePatch;
import com.start.craftbox.R;
import com.start.craftbox.Tools.AssetsUtil;
import com.start.craftbox.Tools.PatchApplier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatchManagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patch_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.patch_manager_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sp = getSharedPreferences("patch_manager", MODE_PRIVATE);
        String jsonStr1 = AssetsUtil.readStringFromAssets(this, "patch1.json");
        String jsonStr2 = AssetsUtil.readStringFromAssets(this, "patch2.json");

        List<PatchPackage> totalPackages = new ArrayList<>();
        totalPackages.add(PatchApplier.parseJsonToPackage(jsonStr1));
        totalPackages.add(PatchApplier.parseJsonToPackage(jsonStr2));

        RecyclerView rvPatches = findViewById(R.id.rv_patches);
        rvPatches.setLayoutManager(new LinearLayoutManager(this));
        PackAdapter adapter = new PackAdapter(totalPackages);
        rvPatches.setAdapter(adapter);

        findViewById(R.id.btn_start_patch).setOnClickListener(v -> {
            for (PatchPackage pack : totalPackages) {
                Log.d("PatchManagerActivity", "开始应用补丁包: " + pack.pack_name);
                for (SinglePatch patch : pack.patches) {
                    Log.d("PatchManagerActivity", "开始应用补丁: " + patch.name);
                    for (PatchNode node : patch.nodes) {
                        Log.d("PatchManagerActivity", "开始应用补丁节点: " + node.offset);
                        Log.d("PatchManagerActivity", "旧数据: " + node.oldBytes);
                        Log.d("PatchManagerActivity", "新数据: " + node.newBytes);
                    }
                }
            }
        });
    }

    private static class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {
        private final List<PatchPackage> packageList;
        private final Set<Integer> expandedPositions = new HashSet<>();

        public PackAdapter(List<PatchPackage> packageList) {
            this.packageList = packageList;
        }

        @NonNull
        @Override
        public PackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pack_card, parent, false);
            return new PackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PackViewHolder holder, int position) {
            PatchPackage pack = packageList.get(position);

            holder.tvPackName.setText(pack.pack_name);
            holder.tvAuthor.setText("作者: " + pack.author);
            holder.tvDesc.setText(pack.description);

            boolean isExpanded = expandedPositions.contains(position);
            holder.layoutExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.divider.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.ivArrow.setRotation(isExpanded ? 180f : 0f);

            holder.containerSubPatches.removeAllViews();
            if (pack.patches != null) {
                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                for (SinglePatch patch : pack.patches) {
                    View subView = inflater.inflate(R.layout.item_sub_patch, holder.containerSubPatches, false);

                    TextView tvSubName = subView.findViewById(R.id.tv_sub_patch_name);
                    TextView tvSubDesc = subView.findViewById(R.id.tv_sub_patch_desc);
                    MaterialSwitch switchBtn = subView.findViewById(R.id.switch_patch);

                    tvSubName.setText(patch.name);
                    tvSubDesc.setText(patch.description);

                    switchBtn.setOnCheckedChangeListener(null);
                    switchBtn.setChecked(patch.isChecked);
                    switchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> patch.isChecked = isChecked);

                    holder.containerSubPatches.addView(subView);
                }
            }

            holder.layoutHeader.setOnClickListener(v -> {
                RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
                TransitionManager.beginDelayedTransition(recyclerView, new AutoTransition());

                if (expandedPositions.contains(position)) {
                    expandedPositions.remove(position);
                    holder.layoutExpandable.setVisibility(View.GONE);
                    holder.divider.setVisibility(View.GONE);
                    holder.ivArrow.animate().rotation(0f).setDuration(200).start();
                } else {
                    expandedPositions.add(position);
                    holder.layoutExpandable.setVisibility(View.VISIBLE);
                    holder.divider.setVisibility(View.VISIBLE);
                    holder.ivArrow.animate().rotation(180f).setDuration(200).start();
                }
            });
        }

        @Override
        public int getItemCount() {
            return packageList != null ? packageList.size() : 0;
        }

        static class PackViewHolder extends RecyclerView.ViewHolder {
            View layoutHeader;
            View divider;
            LinearLayout layoutExpandable;
            LinearLayout containerSubPatches;
            TextView tvPackName, tvAuthor, tvDesc;
            ImageView ivArrow;

            public PackViewHolder(@NonNull View itemView) {
                super(itemView);
                layoutHeader = itemView.findViewById(R.id.layout_pack_header);
                divider = itemView.findViewById(R.id.divider);
                layoutExpandable = itemView.findViewById(R.id.layout_expandable);
                containerSubPatches = itemView.findViewById(R.id.container_sub_patches);
                tvPackName = itemView.findViewById(R.id.tv_pack_name);
                tvAuthor = itemView.findViewById(R.id.tv_author);
                tvDesc = itemView.findViewById(R.id.tv_description);
                ivArrow = itemView.findViewById(R.id.iv_toggle_arrow);
            }
        }
    }
}
