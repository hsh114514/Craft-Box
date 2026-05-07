package com.start.craftbox.Code;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;

public class CombinedAnalyzeManager implements AnalyzeManager {
    private final AnalyzeManager tmManager;
    private final SyntaxAnalyzer myAnalyzer;

    public CombinedAnalyzeManager(AnalyzeManager tmManager, SyntaxAnalyzer myAnalyzer) {
        this.tmManager = tmManager;
        this.myAnalyzer = myAnalyzer;
    }

    @Override
    public void setReceiver(@Nullable StyleReceiver receiver) {
        tmManager.setReceiver(receiver);
        myAnalyzer.setReceiver(receiver);
    }

    @Override
    public void reset(@NonNull ContentReference content, @NonNull Bundle extraArguments) {
        tmManager.reset(content, extraArguments);
        myAnalyzer.reset(content, extraArguments);
    }

    @Override
    public void insert(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence insertedContent) {
        tmManager.insert(start, end, insertedContent);
        myAnalyzer.insert(start, end, insertedContent);
    }

    @Override
    public void delete(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence deletedContent) {
        tmManager.delete(start, end, deletedContent);
        myAnalyzer.delete(start, end, deletedContent);
    }

    @Override public void rerun() { tmManager.rerun(); myAnalyzer.rerun(); }
    @Override public void destroy() { tmManager.destroy(); myAnalyzer.destroy(); }
}
