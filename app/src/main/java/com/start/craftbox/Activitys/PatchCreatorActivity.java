package com.start.craftbox.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.start.craftbox.Entity.Patch.PatchNode;
import com.start.craftbox.Entity.Patch.PatchPackage;
import com.start.craftbox.Entity.Patch.SinglePatch;
import com.start.craftbox.R;
import com.start.craftbox.Tools.AssetsUtil;
import com.start.craftbox.Tools.PatchExporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PatchCreatorActivity extends AppCompatActivity {
    private final List<SelectableNode> allDiffNodes = new ArrayList<>();
    private final List<SinglePatch> builtPatches = new ArrayList<>();
    private CreatorModuleAdapter mainAdapter;
    private int patchCounter = 1;

    public static class SelectableNode {
        public PatchNode node;
        public boolean isSelected;

        public SelectableNode(PatchNode node) {
            this.node = node;
            this.isSelected = false;
        }
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("FilePicker", "onActivityResult: " + result.getResultCode());
                if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("未选择文件")
                            .setMessage("取消了文件选择,请重新选择文件")
                            .setCancelable(false)
                            .setPositiveButton("重新选择文件", (dialog, which) -> startFilePicker())
                            .setNegativeButton("取消", (dialog, which) -> finish())
                            .show();
                } else if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String[] selectedPaths = result.getData().getStringArrayExtra(FileManagerActivity.EXTRA_RESULT_PATHS);
                    if (selectedPaths != null && selectedPaths.length > 0) {
                        File originalSo = AssetsUtil.getFileFromAssets(this, "Raw/libminecraftpe_r.so");
                        File modifiedSo = new File(selectedPaths[0]);
                        runDiffWorkflow(originalSo, modifiedSo);
                    }
                }
            }
    );

    private void startFilePicker() {
        Intent intent = new Intent(this, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_CHOICE_MODE, 1);
        filePickerLauncher.launch(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patch_creator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.patch_creator_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rvMain = findViewById(R.id.rv_creator_modules);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new CreatorModuleAdapter(builtPatches, allDiffNodes, this::showAddPatchDialog);
        rvMain.setAdapter(mainAdapter);
        findViewById(R.id.btn_save_pack).setOnClickListener(v -> saveFinalPatchPackage());

        startFilePicker();
    }


    private void runDiffWorkflow(File orig, File mod) {
        try {
            List<PatchNode> diffs = PatchExporter.diffSoFiles(orig, mod);
            allDiffNodes.clear();
            for (PatchNode node : diffs) {
                allDiffNodes.add(new SelectableNode(node));
            }
            if (mainAdapter != null) {
                mainAdapter.notifyItemChanged(2);
            }
            Toast.makeText(this, "比对完成，发现 " + allDiffNodes.size() + " 个差异点", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "比对失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddPatchDialog() {
        List<PatchNode> selectedNodes = new ArrayList<>();
        for (SelectableNode sNode : allDiffNodes) {
            if (sNode.isSelected) {
                selectedNodes.add(sNode.node);
            }
        }

        if (selectedNodes.isEmpty()) {
            Toast.makeText(this, "请先在列表中勾选要归属于该补丁的差异点！", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_patch_meta, null);
        TextInputEditText etPatchName = dialogView.findViewById(R.id.et_dialog_patch_name);
        TextInputEditText etPatchDesc = dialogView.findViewById(R.id.et_dialog_patch_desc);

        new MaterialAlertDialogBuilder(this)
                .setTitle("创建补丁")
                .setMessage("当前已选中 " + selectedNodes.size() + " 个修补点")
                .setView(dialogView)
                .setPositiveButton("确定添加", (dialog, which) -> {
                    String name = etPatchName.getText().toString().trim();
                    String desc = etPatchDesc.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "补丁名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SinglePatch patch = new SinglePatch();
                    patch.patch_id = "patch_" + (patchCounter++);
                    patch.name = name;
                    patch.description = desc.isEmpty() ? "暂无描述" : desc;
                    patch.enabled_by_default = false;
                    patch.nodes = selectedNodes;

                    builtPatches.add(patch);

                    allDiffNodes.removeIf(selectableNode -> selectableNode.isSelected);

                    if (mainAdapter != null) {
                        mainAdapter.notifyItemChanged(1);
                        mainAdapter.notifyItemChanged(2);
                    }

                    Toast.makeText(this, "成功创建补丁 [" + name + "] 并收纳了 " + selectedNodes.size() + " 个修补点", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void saveFinalPatchPackage() {
        if (mainAdapter.metadataName.isEmpty() || mainAdapter.metadataAuthor.isEmpty() || mainAdapter.metadataDesc.isEmpty()) {
            Toast.makeText(this, "请填写完整的补丁包名称和作者名称！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (builtPatches.isEmpty()) {
            Toast.makeText(this, "当前补丁包内还没有任何补丁，请至少添加一个补丁！", Toast.LENGTH_SHORT).show();
            return;
        }
        PatchPackage finalPack = new PatchPackage();
        finalPack.pack_name = mainAdapter.metadataName;
        finalPack.author = mainAdapter.metadataAuthor;
        finalPack.description = mainAdapter.metadataDesc;
        finalPack.patches = builtPatches;

        File outputFile = new File(getExternalFilesDir(null), mainAdapter.metadataName + ".json");
        try {
            PatchExporter.exportPatchPackage(finalPack, outputFile);
            Toast.makeText(this, "补丁包导出成功！生成路径:\n" + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "文件导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class DiffNodeAdapter extends RecyclerView.Adapter<DiffNodeAdapter.NodeViewHolder> {

        private final List<SelectableNode> nodeList;

        public DiffNodeAdapter(List<SelectableNode> nodeList) {
            this.nodeList = nodeList;
        }

        @NonNull
        @Override
        public NodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diff_node_select, parent, false);
            return new NodeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NodeViewHolder holder, int position) {
            SelectableNode item = nodeList.get(position);
            PatchNode node = item.node;

            holder.tvOffset.setText("偏移量: " + node.offset);
            holder.tvBytes.setText(String.format("旧: %s -> 新: %s", node.oldBytes, node.newBytes));

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(item.isSelected);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.isSelected = isChecked);

            holder.itemView.setOnClickListener(v -> holder.checkBox.toggle());
        }

        @Override
        public int getItemCount() {
            return nodeList != null ? nodeList.size() : 0;
        }

        static class NodeViewHolder extends RecyclerView.ViewHolder {
            TextView tvOffset, tvBytes;
            MaterialCheckBox checkBox;

            public NodeViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOffset = itemView.findViewById(R.id.tv_node_offset);
                tvBytes = itemView.findViewById(R.id.tv_node_bytes);
                checkBox = itemView.findViewById(R.id.cb_node_select);
            }
        }
    }

    private static class CreatorModuleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int CARD_META = 0;
        private static final int CARD_BUILT_LIST = 1;
        private static final int CARD_DIFF_POOL = 2;
        private String metadataName = "";
        private String metadataAuthor = "";
        private String metadataDesc = "";
        private final List<SinglePatch> builtPatches;
        private final List<SelectableNode> diffNodes;
        private final OnAddPatchClickListener addPatchListener;
        private RecyclerView attachedRecyclerView;

        public interface OnAddPatchClickListener {
            void onAddClick();
        }

        private boolean isBuiltExpanded = true;
        private boolean isDiffExpanded = true;

        public CreatorModuleAdapter(List<SinglePatch> builtPatches, List<SelectableNode> diffNodes, OnAddPatchClickListener addPatchListener) {
            this.builtPatches = builtPatches;
            this.diffNodes = diffNodes;
            this.addPatchListener = addPatchListener;
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            this.attachedRecyclerView = recyclerView;
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            this.attachedRecyclerView = null;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == CARD_META) {
                return new MetaViewHolder(inflater.inflate(R.layout.item_creator_meta, parent, false));
            } else if (viewType == CARD_BUILT_LIST) {
                return new BuiltListViewHolder(inflater.inflate(R.layout.item_creator_patches_card, parent, false));
            } else {
                return new DiffPoolViewHolder(inflater.inflate(R.layout.item_creator_diff_pool_card, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = holder.getItemViewType();

            if (type == CARD_META) {
                MetaViewHolder h = (MetaViewHolder) holder;
                h.etName.setText(metadataName);
                h.etAuthor.setText(metadataAuthor);
                h.etDesc.setText(metadataDesc);

                h.etName.addTextChangedListener(new android.text.TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(android.text.Editable s) {
                        metadataName = s.toString().trim();
                    }
                });

                h.etAuthor.addTextChangedListener(new android.text.TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(android.text.Editable s) {
                        metadataAuthor = s.toString().trim();
                    }
                });

                h.etDesc.addTextChangedListener(new android.text.TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(android.text.Editable s) {
                        metadataDesc = s.toString().trim();
                    }
                });

            } else if (type == CARD_BUILT_LIST) {
                BuiltListViewHolder h = (BuiltListViewHolder) holder;
                h.tvTitle.setText("已打包的补丁 (" + builtPatches.size() + ")");

                h.rvInner.setLayoutManager(new LinearLayoutManager(h.itemView.getContext()));
                InnerBuiltAdapter innerAdapter = new InnerBuiltAdapter(builtPatches);
                h.rvInner.setAdapter(innerAdapter);

                setupSwipeToDelete(h.rvInner, innerAdapter);

                h.layoutExpand.setVisibility(isBuiltExpanded ? View.VISIBLE : View.GONE);
                h.ivArrow.setRotation(isBuiltExpanded ? 180f : 0f);

                h.header.setOnClickListener(v -> {
                    if (attachedRecyclerView != null && attachedRecyclerView.isLaidOut()) {
                        TransitionManager.beginDelayedTransition(attachedRecyclerView, new AutoTransition());
                    }
                    isBuiltExpanded = !isBuiltExpanded;
                    notifyItemChanged(CARD_BUILT_LIST);
                });

            } else if (type == CARD_DIFF_POOL) {
                DiffPoolViewHolder h = (DiffPoolViewHolder) holder;
                h.tvTitle.setText("未处理的差异码 (" + diffNodes.size() + ")");

                h.rvInner.setLayoutManager(new LinearLayoutManager(h.itemView.getContext()));
                DiffNodeAdapter innerAdapter = new DiffNodeAdapter(diffNodes);
                h.rvInner.setAdapter(innerAdapter);

                h.layoutExpand.setVisibility(isDiffExpanded ? View.VISIBLE : View.GONE);
                h.ivArrow.setRotation(isDiffExpanded ? 180f : 0f);

                h.btnPack.setOnClickListener(v -> addPatchListener.onAddClick());

                h.header.setOnClickListener(v -> {
                    if (attachedRecyclerView != null && attachedRecyclerView.isLaidOut()) {
                        TransitionManager.beginDelayedTransition(attachedRecyclerView, new AutoTransition());
                    }
                    isDiffExpanded = !isDiffExpanded;
                    notifyItemChanged(CARD_DIFF_POOL);
                });
            }
        }

        private void setupSwipeToDelete(RecyclerView recyclerView, InnerBuiltAdapter adapter) {
            ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END | ItemTouchHelper.START) {
                @Override
                public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    SinglePatch removedPatch = builtPatches.get(position);
                    for (PatchNode node : removedPatch.nodes) {
                        diffNodes.add(new SelectableNode(node));
                    }
                    builtPatches.remove(position);
                    adapter.notifyItemRemoved(position);
                    notifyItemChanged(CARD_BUILT_LIST);
                    notifyItemChanged(CARD_DIFF_POOL);
                }
            };
            new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        static class MetaViewHolder extends RecyclerView.ViewHolder {
            TextInputEditText etName, etAuthor, etDesc;

            MetaViewHolder(View v) {
                super(v);
                etName = v.findViewById(R.id.et_pack_name);
                etAuthor = v.findViewById(R.id.et_author);
                etDesc = v.findViewById(R.id.et_pack_description);
            }
        }

        static class BuiltListViewHolder extends RecyclerView.ViewHolder {
            View header, layoutExpand;
            TextView tvTitle;
            ImageView ivArrow;
            RecyclerView rvInner;

            BuiltListViewHolder(View v) {
                super(v);
                header = v.findViewById(R.id.header_built_patches);
                layoutExpand = v.findViewById(R.id.expand_built_container);
                tvTitle = v.findViewById(R.id.tv_title_built_patches);
                ivArrow = v.findViewById(R.id.iv_arrow_built);
                rvInner = v.findViewById(R.id.rv_inner_built_patches);
            }
        }

        static class DiffPoolViewHolder extends RecyclerView.ViewHolder {
            View header, layoutExpand;
            TextView tvTitle;
            ImageView ivArrow;
            RecyclerView rvInner;
            MaterialButton btnPack;

            DiffPoolViewHolder(View v) {
                super(v);
                header = v.findViewById(R.id.header_diff_pool);
                layoutExpand = v.findViewById(R.id.expand_diff_container);
                tvTitle = v.findViewById(R.id.tv_title_diff_pool);
                ivArrow = v.findViewById(R.id.iv_arrow_diff);
                rvInner = v.findViewById(R.id.rv_inner_diff_nodes);
                btnPack = v.findViewById(R.id.btn_inner_add_patch);
            }
        }
    }

    private static class InnerBuiltAdapter extends RecyclerView.Adapter<InnerBuiltAdapter.SubBuiltViewHolder> {
        private final List<SinglePatch> patches;

        public InnerBuiltAdapter(List<SinglePatch> patches) {
            this.patches = patches;
        }

        @NonNull
        @Override
        public SubBuiltViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new SubBuiltViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubBuiltViewHolder holder, int position) {
            SinglePatch p = patches.get(position);
            holder.t1.setText(p.name);
            holder.t2.setText(p.description + " (" + p.nodes.size() + "个修改点)");
        }

        @Override
        public int getItemCount() {
            return patches != null ? patches.size() : 0;
        }

        static class SubBuiltViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;

            SubBuiltViewHolder(View v) {
                super(v);
                t1 = v.findViewById(android.R.id.text1);
                t2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}