package com.start.craftbox.Page;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.start.craftbox.R;
import org.eclipse.tm4e.core.registry.IThemeSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolInputView;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorDiagnosticTooltipWindow;

public class CodeEditorFragment extends Fragment {
    ActivityResultLauncher<String[]> filePickerLauncher;
    ActivityResultLauncher<String> outputLauncher;
    MaterialButton undo;
    MaterialButton redo;
    SymbolInputView symbolInputView;
    CodeEditor codeEditor;

    static final String[] SYMBOLS = new String[]{
            "->", "{", "}", "(", ")",
            ",", ".", ";", "\"", "?",
            "+", "-", "*", "/", "<",
            ">", "[", "]", ":"
    };

    static final String[] SYMBOL_INSERT_TEXT = new String[]{
            "\t", "{}", "}", "(", ")",
            ",", ".", ";", "\"", "?",
            "+", "-", "*", "/", "<",
            ">", "[", "]", ":"
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                try {
                    InputStream is = requireContext().getContentResolver().openInputStream(uri);
                    File tempFile = new File(requireContext().getCacheDir(), "cachecode");
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[4096];
                    int len;
                    if (is != null) {
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            sb.append(new String(buffer, 0, len));
                        }
                    }
                    codeEditor.setText(sb.toString());
                    fos.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "无法读取文件", Toast.LENGTH_SHORT).show();
                }
            }
        });

        outputLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument(), uri -> {
            if (uri != null) {
                try {
                    FileOutputStream fos = (FileOutputStream) requireContext().getContentResolver().openOutputStream(uri);
                    fos.write(codeEditor.getText().toString().getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "无法保存文件", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return inflater.inflate(R.layout.fragment_code_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/JetBrainsMono-Regular.ttf");
        codeEditor = root.findViewById(R.id.code_editor);
        MaterialButton save = root.findViewById(R.id.btn_save);
        MaterialButton load = root.findViewById(R.id.btn_load);
        undo = root.findViewById(R.id.btn_undo);
        redo = root.findViewById(R.id.btn_redo);
        symbolInputView = root.findViewById(R.id.symbol_input_view);
        symbolInputView.addSymbols(SYMBOLS, SYMBOL_INSERT_TEXT);
        symbolInputView.bindEditor(codeEditor);
        symbolInputView.forEachButton(btn -> btn.setTypeface(customFont));
        codeEditor.subscribeEvent(ContentChangeEvent.class, (event, unsubscribe) -> {
            checkCanUndo();
        });


        load.setOnClickListener(view -> {
            filePickerLauncher.launch(new String[]{"*/*"});
        });
        save.setOnClickListener(view -> {
            outputLauncher.launch("code.txt");
        });

        undo.setOnClickListener(view -> {
            if (codeEditor.canUndo()) codeEditor.undo();
            checkCanUndo();
        });

        redo.setOnClickListener(view -> {
            if (codeEditor.canRedo()) codeEditor.redo();
            checkCanUndo();
        });



        try {
            FileProviderRegistry.getInstance().addFileProvider(
                    new AssetsFileResolver(requireContext().getApplicationContext().getAssets())
            );
            var themeRegistry = ThemeRegistry.getInstance();
            var name = "dark_vs";
            var themeAssetsPath = "textmate/" + name + ".json";
            var model = new ThemeModel(
                    IThemeSource.fromInputStream(
                            FileProviderRegistry.getInstance().tryGetInputStream(themeAssetsPath),
                            themeAssetsPath, null
                    ),
                    name
            );
            model.setDark(true);
            themeRegistry.loadTheme(model);
            ThemeRegistry.getInstance().setTheme("dark_vs");
            GrammarRegistry.getInstance().loadGrammars("languages.json");
            codeEditor.setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
            var languageScopeName = "source.mcpe-ui2";
            var language = TextMateLanguage.create(
                    languageScopeName, true
            );
            codeEditor.setEditorLanguage(language);
            codeEditor.setTypefaceText(customFont);
            codeEditor.indentLines(false);
            codeEditor.setHighlightBracketPair(true);
            codeEditor.setDisplayLnPanel(true);
            codeEditor.setTabWidth(4);
            codeEditor.setLineNumberEnabled(true);
            codeEditor.getProps().drawSideBlockLine = true;
            codeEditor.setBlockLineEnabled(true);
            var component = codeEditor.getComponent(EditorAutoCompletion.class);
            codeEditor.getComponent(EditorDiagnosticTooltipWindow.class).setEnabled(true);
            component.setEnabled(true);
            codeEditor.invalidate();
        } catch (Exception e) {
            Log.e("Editor", "onViewCreated: " + e.getMessage());
        }

    }

    void checkCanUndo() {
        if (!(undo != null && redo != null))return;
        undo.setEnabled(codeEditor.canUndo());
        redo.setEnabled(codeEditor.canRedo());
    }


}