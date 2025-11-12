package com.example.csvzipfilter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    public static Path unzipToSiblingFolder(Path zipFile) throws IOException {
        Path parent = zipFile.toAbsolutePath().getParent();
        String baseName = stripExtension(zipFile.getFileName().toString());
        Path outDir = parent.resolve(baseName);
        Files.createDirectories(outDir);
        try (InputStream fis = Files.newInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path target = outDir.resolve(entry.getName()).normalize();
                if (!target.startsWith(outDir)) throw new IOException("Zip entry outside target dir: " + entry.getName());
                if (entry.isDirectory()) Files.createDirectories(target);
                else {
                    Files.createDirectories(target.getParent());
                    Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
        return outDir;
    }
    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
