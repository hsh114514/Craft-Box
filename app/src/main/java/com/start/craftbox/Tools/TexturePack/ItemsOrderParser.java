package com.start.craftbox.Tools.TexturePack;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ItemsOrderParser {

    public static List<String> parseOrderFromAssets(Context context) {
        List<String> items = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("items_order.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("！") || line.startsWith("#")) {
                    continue;
                }
                if (line.contains("未知") || line.contains("可空位") || line.equals("missing")) {
                    items.add(null);
                } else {
                    items.add(line);
                }
            }
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
