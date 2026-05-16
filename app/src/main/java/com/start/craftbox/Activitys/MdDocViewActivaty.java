package com.start.craftbox.Activitys;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.start.craftbox.R;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.ImagesPlugin;

public class MdDocViewActivaty extends AppCompatActivity {
    TextView md_textview;
    Markwon markwon;

    String md_content = "# Craft Box\n" +
            "\n" +
            "**一款专为 Minecraft PE 0.14.3 设计的开发工具箱。**\n" +
            "\n" +
            "---\n" +
            "\n" +
            "## 功能\n" +
            "\n" +
            "* **贴图转换**：Tga和png贴图互相转换。\n" +
            "* **图文教程文档**：在线更新的开发图文教程。\n" +
            "```bash\n" +
            "git clone [https://github.com/hsh114514/Craft-Box.git](https://github.com/hsh114514/Craft-Box.git)\n" +
            "```";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_md_doc_view);
        md_textview = findViewById(R.id.markdown_content_text);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reader_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        markwon = Markwon.builder(this)
                .build();

        markwon.setMarkdown(md_textview, md_content);

        MaterialButton materialButton = findViewById(R.id.btn_1);
        materialButton.setOnClickListener(v ->{

        });
    }

}