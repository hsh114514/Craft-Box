package com.start.craftbox.Code;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.text.TextRange;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class JsonLanguage implements Language {
    private SyntaxAnalyzer analyzer = new SyntaxAnalyzer();
    @NonNull
    @Override
    public AnalyzeManager getAnalyzeManager() {
        return analyzer;
    }

    @Override
    public SymbolPairMatch getSymbolPairs() {
        return new SymbolPairMatch.DefaultSymbolPairs();
    }

    @Override
    public int getInterruptionLevel() {
        return INTERRUPTION_LEVEL_SLIGHT;
    }

    @NonNull
    @Override
    public Formatter getFormatter() {
        return new Formatter() {
            @Override
            public void format(@NonNull Content text, @NonNull TextRange cursorRange) {

            }

            @Override
            public void formatRegion(@NonNull Content text, @NonNull TextRange rangeToFormat, @NonNull TextRange cursorRange) {

            }

            @Override
            public void setReceiver(@Nullable FormatResultReceiver receiver) {

            }

            @Override
            public boolean isRunning() {
                return false;
            }

            @Override
            public void destroy() {

            }
        };
    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                                    @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
    }

    @Override public int getIndentAdvance(@NonNull ContentReference content, int line, int column) { return 0; }
    @Override public boolean useTab() { return true; }
    @Override public NewlineHandler[] getNewlineHandlers() { return null; }
    @Override public void destroy() { analyzer.destroy(); }
}
