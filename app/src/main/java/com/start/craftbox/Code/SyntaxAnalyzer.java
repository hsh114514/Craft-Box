package com.start.craftbox.Code;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;

public class SyntaxAnalyzer implements AnalyzeManager {

    private StyleReceiver receiver;
    private ContentReference content;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> analysisTask;
    public DiagnosticsContainer diagnostics = new DiagnosticsContainer();

    public DiagnosticsContainer getDiagnostics() {
        return diagnostics;
    }

    @Override
    public void setReceiver(@Nullable StyleReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void reset(@NonNull ContentReference content, @NonNull Bundle extraArguments) {
        this.content = content;
        triggerAnalysis();
    }

    @Override
    public void insert(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence insertedContent) {
        triggerAnalysis();
    }

    @Override
    public void delete(@NonNull CharPosition start, @NonNull CharPosition end, @NonNull CharSequence deletedContent) {
        triggerAnalysis();
    }

    @Override
    public void rerun() {
        triggerAnalysis();
    }

    @Override
    public void destroy() {
        executor.shutdownNow();
        receiver = null;
    }

    private void triggerAnalysis() {
        if (analysisTask != null) analysisTask.cancel(true);
        analysisTask = executor.submit(this::doFullAnalysis);
    }

    private void doFullAnalysis() {
        if (content == null || receiver == null) return;
        String text = content.toString();
        if (text.isEmpty()) return;

        try {
            String cleanText = maskComments(text);

            new JSONObject(cleanText);

            receiver.setDiagnostics(this, null);

        } catch (JSONException e) {
            String message = e.getMessage();
            int offset = extractOffset(message);

            if (offset != -1) {
                int safeOffset = Math.min(offset, Math.max(0, text.length() - 1));
                CharPosition pos = content.getCharPosition(safeOffset);

                DiagnosticRegion region = new DiagnosticRegion(
                        pos.line,
                        pos.column,
                        DiagnosticRegion.SEVERITY_ERROR
                );

                diagnostics.addDiagnostic(region);

                // 提交诊断
                if (receiver != null) {
                    receiver.setDiagnostics(this, diagnostics);
                }
            }
        }
    }

    private String maskComments(String text) {
        StringBuilder sb = new StringBuilder(text);
        Matcher m1 = Pattern.compile("//.*").matcher(text);
        while (m1.find()) {
            for (int i = m1.start(); i < m1.end(); i++) sb.setCharAt(i, ' ');
        }
        Matcher m2 = Pattern.compile("/\\*(?s:.*?)\\*/").matcher(text);
        while (m2.find()) {
            for (int i = m2.start(); i < m2.end(); i++) sb.setCharAt(i, ' ');
        }
        return sb.toString();
    }

    private int extractOffset(String message) {
        if (message == null) return -1;
        try {
            Pattern pattern = Pattern.compile("at (\\d+)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            }
        } catch (Exception e) {}
        return -1;
    }

}
