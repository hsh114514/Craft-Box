package com.start.craftbox.Tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.start.craftbox.Entity.Patch.PatchNode;
import com.start.craftbox.Entity.Patch.PatchPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatchExporter {

    private static class TempBlock {
        long offset;
        List<Byte> oldCluster = new ArrayList<>();
        List<Byte> newCluster = new ArrayList<>();
        TempBlock(long offset) { this.offset = offset; }
    }

    public static List<PatchNode> diffSoFiles(File originalSo, File modifiedSo) throws IOException {
        if (originalSo.length() != modifiedSo.length()) {
            throw new IllegalArgumentException("SO文件大小不一致，无法进行绝对偏移量比对！");
        }

        byte[] orgBytes = new byte[(int) originalSo.length()];
        byte[] modBytes = new byte[(int) modifiedSo.length()];

        try (FileInputStream fisOrg = new FileInputStream(originalSo);
             FileInputStream fisMod = new FileInputStream(modifiedSo)) {
            fisOrg.read(orgBytes);
            fisMod.read(modBytes);
        }

        List<PatchNode> nodes = new ArrayList<>();
        int i = 0;
        int size = orgBytes.length;

        while (i < size) {
            if (orgBytes[i] != modBytes[i]) {
                TempBlock block = new TempBlock(i);
                while (i < size && orgBytes[i] != modBytes[i]) {
                    block.oldCluster.add(orgBytes[i]);
                    block.newCluster.add(modBytes[i]);
                    i++;
                }

                PatchNode node = new PatchNode();
                node.offset = "0x" + Long.toHexString(block.offset).toUpperCase();
                node.oldBytes = bytesToHex(toPrimitiveArray(block.oldCluster));
                node.newBytes = bytesToHex(toPrimitiveArray(block.newCluster));
                nodes.add(node);
            }
            i++;
        }
        return nodes;
    }

    public static void exportPatchPackage(PatchPackage patchPackage, File outputFile) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(patchPackage, writer);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static byte[] toPrimitiveArray(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) bytes[i] = list.get(i);
        return bytes;
    }
}