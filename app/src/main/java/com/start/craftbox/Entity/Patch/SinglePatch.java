package com.start.craftbox.Entity.Patch;

import java.util.List;

public class SinglePatch {
    public String patch_id;
    public String name;
    public String description;
    public boolean enabled_by_default;
    public List<PatchNode> nodes;

    public transient boolean isChecked;
}