package com.start.craftbox.Views.Palette;

import static com.start.craftbox.Views.DrawableFactory.createCheckerboardDrawable;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.start.craftbox.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorPicker {
    private int selectedColor;
    private final Context context;
    private final List<Integer> paletteColors;
    private PaletteAdapter adapter;

    public ColorPicker(Context context, int initialColor) {
        this.context = context;
        this.selectedColor = initialColor;
        this.paletteColors = new ArrayList<>(Arrays.asList(
                0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFFFFFF,
                0xFF00FFFF, 0xFF800000, 0xFF008000, 0xFF000080, 0xFF808000
        ));
    }

    public ColorPicker(Context context, int initialColor, List<Integer> colors) {
        this.context = context;
        this.selectedColor = initialColor;
        this.paletteColors = colors;
    }

    public void show(OnColorConfirmedListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_picker, null);

        Slider sA = view.findViewById(R.id.slider_a);
        Slider sR = view.findViewById(R.id.slider_r);
        Slider sG = view.findViewById(R.id.slider_g);
        Slider sB = view.findViewById(R.id.slider_b);
        TextInputEditText etHex = view.findViewById(R.id.et_hex_input);
        LinearLayout ll = view.findViewById(R.id.chceckboard);
        ll.setBackground(createCheckerboardDrawable(20));
        View vNew = view.findViewById(R.id.color_preview_new);
        View vOld = view.findViewById(R.id.color_preview_old);
        RecyclerView rv = view.findViewById(R.id.rv_palette);

        vOld.setBackgroundColor(selectedColor);
        updateUI(selectedColor, sA, sR, sG, sB, etHex, vNew);

        etHex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String hex = s.toString();
                if (hex.length() == 8 || (hex.startsWith("#") && hex.length() == 9)) {
                    try {
                        int parsedColor = Color.parseColor(hex.startsWith("#") ? hex : "#" + hex);
                        // 只有当解析出的颜色和当前不同时才更新 Slider，避免死循环
                        if (parsedColor != selectedColor) {
                            updateUI(parsedColor, sA, sR, sG, sB, null, vNew);
                        }
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        Slider.OnChangeListener sliderChange = (slider, value, fromUser) -> {
            if (fromUser) {
                int color = Color.argb((int) sA.getValue(), (int) sR.getValue(), (int) sG.getValue(), (int) sB.getValue());
                selectedColor = color;
                vNew.setBackgroundColor(color);
                etHex.setText(String.format("%08X", color));
            }
        };
        sA.addOnChangeListener(sliderChange);
        sR.addOnChangeListener(sliderChange);
        sG.addOnChangeListener(sliderChange);
        sB.addOnChangeListener(sliderChange);

        rv.setLayoutManager(new GridLayoutManager(context, 5));
        adapter = new PaletteAdapter(paletteColors, new PaletteAdapter.OnColorClickListener() {
            @Override
            public void onColorClick(int color) {
                updateUI(color, sA, sR, sG, sB, etHex, vNew);
            }

            @Override
            public void onColorLongClick(int position) {
                paletteColors.set(position, selectedColor); //覆盖该格颜色
                adapter.notifyItemChanged(position);
            }
        });
        rv.setAdapter(adapter);

        new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton("应用", (d, w) -> listener.onConfirmed(selectedColor))
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateUI(int color, Slider a, Slider r, Slider g, Slider b, EditText hex, View preview) {
        selectedColor = color;
        a.setValue(Color.alpha(color));
        r.setValue(Color.red(color));
        g.setValue(Color.green(color));
        b.setValue(Color.blue(color));
        if (hex != null) hex.setText(String.format("%08X", color));
        preview.setBackgroundColor(color);
    }

    public interface OnColorConfirmedListener {
        void onConfirmed(int color);
    }


}