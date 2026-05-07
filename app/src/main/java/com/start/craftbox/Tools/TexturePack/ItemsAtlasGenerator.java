package com.start.craftbox.Tools.TexturePack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ItemsAtlasGenerator {

    private static final int TILE_SIZE = 16;
    private static final int GRID_COLS = 32;
    private static final int ATLAS_WIDTH = TILE_SIZE * GRID_COLS;   // 512
    private static final int ATLAS_HEIGHT = TILE_SIZE * 32;         // 512 (32行)

    private Context context;
    private File outputDir;

    public ItemsAtlasGenerator(Context context, File outputDir) {
        this.context = context;
        this.outputDir = outputDir;
        if (!outputDir.exists()) outputDir.mkdirs();
    }

    public void generate(File zipFile) throws IOException {
        List<String> items = ItemsOrder.getOrder();  // 固定300个条目
        int totalItems = items.size();  // 300

        Bitmap atlas = Bitmap.createBitmap(ATLAS_WIDTH, ATLAS_HEIGHT, Bitmap.Config.ARGB_8888);
        atlas.eraseColor(0x00000000);

        // 预加载生物蛋 spritesheet
        Bitmap spawnEggs1 = null;
        Bitmap spawnEggs2 = null;
        try {
            spawnEggs1 = BitmapFactory.decodeStream(context.getAssets().open("spawn_eggs1.png"));
            spawnEggs2 = BitmapFactory.decodeStream(context.getAssets().open("spawn_eggs2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 普通药水列表（必须与 ItemsOrder 中普通药水部分的顺序完全一致）
        // 根据你提供的 ItemsOrder.java 第4行内容调整
        List<String> normalPotions = Arrays.asList(
                "水瓶", "迅捷药水", "迟缓药水", "missing", "missing",
                "力量药水", "瞬间治疗药水", "瞬间伤害药水", "跳跃提升药水", "missing",
                "再生药水", "missing", "抗火药水", "水肺药水", "隐身药水",
                "missing", "夜视药水", "missing", "虚弱药水", "剧毒药水",
                "missing", "missing", "missing", "missing", "空瓶子"
        );

        // 预先计算第一个生物蛋和第一个喷溅药水的索引
        int firstSpawnEggIndex = -1;
        int firstSplashIndex = -1;
        for (int i = 0; i < totalItems; i++) {
            String name = items.get(i);
            if ("生物蛋".equals(name) && firstSpawnEggIndex == -1) firstSpawnEggIndex = i;
            if ("喷溅药水".equals(name) && firstSplashIndex == -1) firstSplashIndex = i;
        }

        TgaUtils tgaUtils = new TgaUtils();

        try (ZipFile zip = new ZipFile(zipFile)) {
            for (int i = 0; i < totalItems; i++) {
                String chineseName = items.get(i);
                int gridX = i % GRID_COLS;
                int gridY = i / GRID_COLS;
                int destX = gridX * TILE_SIZE;
                int destY = gridY * TILE_SIZE;

                Bitmap tile = null;

                // 特殊处理生物蛋
                if ("生物蛋".equals(chineseName) && firstSpawnEggIndex != -1) {
                    int offset = i - firstSpawnEggIndex;
                    if (offset >= 0 && offset < 27) {
                        if (offset < 3 && spawnEggs1 != null) {
                            tile = Bitmap.createBitmap(spawnEggs1, offset * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
                        } else if (offset >= 3 && spawnEggs2 != null) {
                            int subOffset = offset - 3;
                            tile = Bitmap.createBitmap(spawnEggs2, subOffset * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
                // 特殊处理喷溅药水
                else if ("喷溅药水".equals(chineseName) && firstSplashIndex != -1) {
                    int offset = i - firstSplashIndex;
                    if (offset >= 0 && offset < normalPotions.size()) {
                        String normalName = normalPotions.get(offset);
                        String splashKey;
                        if ("missing".equals(normalName) || "空瓶子".equals(normalName)) {
                            splashKey = "喷溅药水";  // 缺失或空瓶子使用默认
                        } else {
                            splashKey = "喷溅" + normalName;
                        }
                        String path = ItemNameMapper.getPath(splashKey);
                        if (path != null) {
                            tile = loadImageFromZip(zip, path, tgaUtils);
                            if (tile == null) {
                                String fallbackPath = ItemNameMapper.getPath("喷溅药水");
                                if (fallbackPath != null) {
                                    tile = loadImageFromZip(zip, fallbackPath, tgaUtils);
                                }
                            }
                        }
                    }
                }
                // 普通物品
                else if (chineseName != null && !chineseName.equals("missing") && !chineseName.equals("唱片")) {
                    String path = ItemNameMapper.getPath(chineseName);
                    if (path != null) {
                        if (path.startsWith("assets:")) {
                            String assetPath = path.substring(7);
                            try (InputStream is = context.getAssets().open(assetPath)) {
                                // assets 中的文件通常是 PNG，但也可能是 TGA，根据后缀判断
                                if (assetPath.toLowerCase().endsWith(".tga")) {
                                    tile = tgaUtils.tgaToBitmap(is);
                                } else {
                                    tile = BitmapFactory.decodeStream(is);
                                }
                            }
                        } else {
                            tile = loadImageFromZip(zip, path, tgaUtils);
                        }
                    }
                }

                if (tile != null) {
                    Bitmap scaled = Bitmap.createScaledBitmap(tile, TILE_SIZE, TILE_SIZE, true);
                    drawBitmapToAtlas(atlas, scaled, destX, destY);
                    tile.recycle();
                    scaled.recycle();
                }
            }
        }

        if (spawnEggs1 != null) spawnEggs1.recycle();
        if (spawnEggs2 != null) spawnEggs2.recycle();

        // 保存图集为 TGA
        File outputFile = new File(outputDir, "items-opaque.tga");
        TgaUtils tgaUtilsSave = new TgaUtils();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            tgaUtilsSave.bitmapToTga(atlas, fos);
        }
        atlas.recycle();
    }

    /**
     * 从 ZIP 包中加载图片，自动识别 PNG 或 TGA 格式
     */
    private Bitmap loadImageFromZip(ZipFile zip, String path, TgaUtils tgaUtils) {
        ZipEntry entry = zip.getEntry(path);
        if (entry == null) return null;
        try {
            try (InputStream is = zip.getInputStream(entry)) {
                if (path.toLowerCase().endsWith(".tga")) {
                    return tgaUtils.tgaToBitmap(is);
                } else {
                    return BitmapFactory.decodeStream(is);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void drawBitmapToAtlas(Bitmap atlas, Bitmap tile, int x, int y) {
        int[] pixels = new int[tile.getWidth() * tile.getHeight()];
        tile.getPixels(pixels, 0, tile.getWidth(), 0, 0, tile.getWidth(), tile.getHeight());
        atlas.setPixels(pixels, 0, tile.getWidth(), x, y, tile.getWidth(), tile.getHeight());
    }
}
