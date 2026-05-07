package com.start.craftbox.Code;

import android.os.Bundle;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.text.TextRange;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class JsonUiLanguageWrapper implements Language {
    private final TextMateLanguage delegate;
    private final SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();

    public JsonUiLanguageWrapper(TextMateLanguage delegate) {
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public AnalyzeManager getAnalyzeManager() {
        return new CombinedAnalyzeManager(delegate.getAnalyzeManager(), syntaxAnalyzer);
    }

    @NonNull
    @Override
    public Formatter getFormatter() {
        return delegate.getFormatter();
    }

    @Override
    public int getInterruptionLevel() {
        return delegate.getInterruptionLevel();
    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position,
                                    @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments)
            throws CompletionCancelledException {
        delegate.requireAutoComplete(content, position, publisher, extraArguments);
    }

    @Override
    public int getIndentAdvance(@NonNull ContentReference content, int line, int column) {
        return delegate.getIndentAdvance(content, line, column);
    }

    @Override
    public boolean useTab() {
        return delegate.useTab();
    }

    @Override
    public SymbolPairMatch getSymbolPairs() {
        return delegate.getSymbolPairs();
    }

    @Override
    public NewlineHandler[] getNewlineHandlers() {
        return delegate.getNewlineHandlers();
    }

    @Override
    public void destroy() {
        delegate.destroy();
        syntaxAnalyzer.destroy();
    }
}
