package com.start.craftbox.Views.Palette;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.ViewHolder> {
    private List<Integer> colors;
    private OnColorClickListener listener;

    public interface OnColorClickListener {
        void onColorClick(int color);
        void onColorLongClick(int position);
    }

    public PaletteAdapter(List<Integer> colors, OnColorClickListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new View(parent.getContext());
        int size = (int) (48 * parent.getContext().getResources().getDisplayMetrics().density);
        v.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int color = colors.get(position);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(24f);
        shape.setColor(color);
        shape.setStroke(2, 0x1F000000);
        holder.itemView.setBackground(shape);
        holder.itemView.setOnClickListener(v -> listener.onColorClick(color));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onColorLongClick(position);
            return true;
        });
    }

    @Override public int getItemCount() { return colors.size(); }
    static class ViewHolder extends RecyclerView.ViewHolder { ViewHolder(View v) { super(v); } }
}